/*
MIT License

Copyright (c) 2024 Gerald Winter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.underdocx.doctypes.odf.modifiers.forlistmodifier;

import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.modifiers.internal.AbstractAreaModifier;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.*;

public class OdfForListModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, OdfForListModifierData, ModifierNodeResult> {


    public OdfForListModifier(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected Node getCommonAncestorNode(OdfForListModifierData modifierData) {
        return COMMON_ANCESTOR_P_OR_TABLE_PARENT.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
    }

    @Override
    protected ModifierNodeResult modify() {
        List<Node> clonedListItems = cloneListItems();
        insertPlaceholdersInClonedRows(clonedListItems);
        ModifierNodeResult result = new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        new OdfDeletePlaceholderModifier().modify(area.right, DeletePlaceholderModifierData.DEFAULT);
        return result;
    }

    private void insertPlaceholdersInClonedRows(List<Node> clonedListItems) {
        for (int i = 0; i < clonedListItems.size(); i++) {
            Node listItem = clonedListItems.get(i);
            Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> firstLastPlaceholders = modifierData.getNodeReplacement(i);
            insertPlaceholderInListItem((TextListItemElement) listItem, firstLastPlaceholders.left, true);
            insertPlaceholderInListItem((TextListItemElement) listItem, firstLastPlaceholders.right, false);
        }
    }

    private void insertPlaceholderInListItem(TextListItemElement listItem, List<ParametersPlaceholderData> placeholderDatas, boolean insertBefore) {
        if (placeholderDatas != null) {
            TextualPlaceholderToolkit<ParametersPlaceholderData> toolkit
                    = Problems.PLACEHOLDER_TOOLKIT_MISSING.get(selection.getPlaceholderToolkit(), "placeholder toolkit instance");
            List<TextPElement> createdParagraphs = new ArrayList<>();
            boolean listItemWasEmpty = !listItem.hasChildNodes();
            placeholderDatas.forEach(placeholderData -> {
                TextPElement p = Convenience.add(createdParagraphs, listItem.newTextPElement());
                toolkit.createPlaceholder(p, placeholderData);
            });
            if (insertBefore && !listItemWasEmpty) {
                Nodes.insertNodes(listItem.getFirstChild(), createdParagraphs, true);
            }
        }
    }

    private List<Node> cloneListItems() {
        int requiredNumberOfListItems = modifierData.getRepeats();
        Node originListItem = findListAndCloneableListItem();
        if (requiredNumberOfListItems == 0) {
            Nodes.deleteNode(originListItem);
            return Collections.emptyList();
        } else if (requiredNumberOfListItems == 1) {
            return Arrays.asList(originListItem);
        } else {
            return Convenience.buildList(result -> {
                Node last = originListItem;
                result.add(originListItem);
                for (int i = 0; i < requiredNumberOfListItems - 1; i++) {
                    Node clone = originListItem.cloneNode(true);
                    Nodes.insertAfter(last, clone);
                    last = clone;
                    result.add(clone);
                }
            });
        }
    }


    private Node findListAndCloneableListItem() {
        Node list = Problems.CANT_FIND_DOM_ELEMENT.get(findList(), "list", null);
        return Problems.CANT_FIND_DOM_ELEMENT.get(getClonableItem(list), "list-item");
    }

    private Optional<Node> getClonableItem(Node list) {
        int searchedIndex = modifierData.getListItemIndex();
        return Convenience.buildOptional(result -> {
            int count = 1;
            TreeWalker treeWalker = new TreeWalker(list, list);
            TreeWalker.VisitState currentState = treeWalker.next();
            while (currentState != null && count <= searchedIndex) {
                if (currentState.isBeginVisit() && OdfElement.LIST_ITEM.is(currentState.getNode())) {
                    if (count == searchedIndex) {
                        result.value = currentState.getNode();
                        break;
                    }
                    count++;
                    currentState = treeWalker.nextSkipChildren();
                } else {
                    currentState = treeWalker.next();
                }
            }
        });
    }

    private Optional<Node> findList() {
        return Convenience.buildOptional(result -> {
            for (Node node : getAreaNodesIterator()) {
                if (OdfElement.LIST.is(node)) {
                    result.value = node;
                    return;
                }
            }
        });
    }
}
