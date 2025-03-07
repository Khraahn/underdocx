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

package org.underdocx.enginelayers.odtengine.modifiers.forrowsmodifier;

import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.underdocx.common.codec.IntCodec;
import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Range;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.enginelayers.baseengine.modifiers.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.odtengine.modifiers.internal.AbstractAreaModifier;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class ForRowsModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, ForRowsModifierData, ModifierNodeResult> {
    Node resultNode = null;

    @Override
    protected Node getCommonAncestorNode(ForRowsModifierData modifierData) {
        return COMMON_ANCESTOR_P_OR_TABLE_PARENT.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
    }

    @Override
    protected ModifierNodeResult modify() {
        resultNode = null;
        Problems.INVALID_VALUE.check(modifierData.getRepeatRows().getLength() % modifierData.getRowGroupSize() == 0, "rowgroupsize", "" + modifierData.getRowGroupSize());
        List<Node> clonedRows = cloneRows();
        insertPlaceholdersInClonedRows(clonedRows);
        DeletePlaceholderModifier.modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        DeletePlaceholderModifier.modify(area.right, DeletePlaceholderModifierData.DEFAULT);
        return ModifierNodeResult.FACTORY.success(resultNode, false);
    }


    private void insertPlaceholdersInClonedRows(List<Node> clonedRows) {
        for (int group = 0; group < clonedRows.size() / modifierData.getRowGroupSize(); group++) {
            Node beginRow = clonedRows.get(group * modifierData.getRowGroupSize());
            Node endRow = clonedRows.get(group * modifierData.getRowGroupSize() + modifierData.getRowGroupSize() - 1);
            TableTableCellElement firstCell = getFirstCellOfRow(beginRow);
            TableTableCellElement lastCell = getLastCellOfRow(endRow);
            Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> firstLastPlaceholders = modifierData.getNodeReplacement(group);
            insertPlaceholderInCell(firstCell, firstLastPlaceholders.left, true);
            insertPlaceholderInCell(lastCell, firstLastPlaceholders.right, false);
        }
    }

    private void insertPlaceholderInCell(TableTableCellElement cell, List<ParametersPlaceholderData> placeholderDatas, boolean insertBefore) {
        if (placeholderDatas != null) {
            TextualPlaceholderToolkit<ParametersPlaceholderData> toolkit
                    = Problems.PLACEHOLDER_TOOLKIT_MISSING.get(selection.getPlaceholderToolkit(), "placeholder toolkit instance");
            List<TextPElement> createdParagraphs = new ArrayList<>();
            boolean cellWasEmpty = !cell.hasChildNodes();
            placeholderDatas.forEach(placeholderData -> {
                TextPElement p = Convenience.add(createdParagraphs, cell.newTextPElement());
                toolkit.createPlaceholder(p, placeholderData);
            });
            if (insertBefore && !cellWasEmpty) {
                Nodes.insertNodes(cell.getFirstChild(), createdParagraphs, true);
            }
        }
    }


    private TableTableCellElement getFirstCellOfRow(Node row) {
        return (TableTableCellElement) Nodes.findFirstDescendantNode(row, OdfElement.TABLE_CELL.createFilter()).get();
    }

    private TableTableCellElement getLastCellOfRow(Node row) {
        List<Node> cells = Nodes.findDescendantNodes(row, OdfElement.TABLE_CELL.createFilter(), true);
        return (TableTableCellElement) cells.get(cells.size() - 1);
    }

    private List<Node> cloneRows() {
        int requiredNumberOfRows = modifierData.getRepeats() * modifierData.getRowGroupSize();
        List<Node> originRows = findTableAndCloneableRows();
        int originSize = originRows.size();
        if (requiredNumberOfRows <= originRows.size()) {
            for (int i = 0; i < originSize - requiredNumberOfRows; i++) {
                Node removedNode = originRows.remove(originSize - i - 1);
                Nodes.deleteNode(removedNode);
            }
            return originRows;
        } else {
            return Convenience.buildList(result -> {
                Node last = originRows.get(originRows.size() - 1);
                result.addAll(originRows);
                for (int i = 0; i < requiredNumberOfRows - originRows.size(); i++) {
                    Node toClone = originRows.get(i % originSize);
                    Node clone = toClone.cloneNode(true);
                    Nodes.insertAfter(last, clone);
                    last = clone;
                    result.add(clone);
                }
            });
        }
    }

    private List<Node> findTableAndCloneableRows() {
        Node table = Problems.CANT_FIND_DOM_ELEMENT.get(findTable(), "table", null);
        resultNode = table;
        return Problems.CANT_FIND_DOM_ELEMENT.notNullOrEmpty(getCloneableRows(table), "row");
    }


    private void unRepeatRows(Node table, Range range) {
        Map<Node, Integer> rowsToUnrepeat = new HashMap<>();
        IntCodec intParser = new IntCodec();

        int maxRows = range.getMax();
        int count = 0;
        TreeWalker treeWalker = new TreeWalker(table, table);
        TreeWalker.VisitState currentState = treeWalker.next();
        while (currentState != null && count <= maxRows) {
            if (currentState.isBeginVisit() && OdfElement.TABLE_ROW.is(currentState.getNode())) {
                String repeatStr = OdfAttribute.TABLE_ROW_REPEAT.getAttributeNS(((Element) currentState.getNode()));
                int repeat = intParser.tryParse(repeatStr).orElse(1);
                if (repeat > 1 && range.contains(count) || range.contains(count + repeat)) {
                    rowsToUnrepeat.put(currentState.getNode(), repeat);
                }
                count = count + repeat;
                currentState = treeWalker.nextSkipChildren();
            } else {
                currentState = treeWalker.next();
            }
        }

        rowsToUnrepeat.forEach((node, repeats) -> {
            OdfAttribute.TABLE_ROW_REPEAT.removeAttributeNS((Element) node);
            for (int i = 0; i < repeats - 1; i++) {
                Node clone = node.cloneNode(true);
                node.getParentNode().insertBefore(clone, node);
            }
        });

    }

    private List<Node> getCloneableRows(Node table) {
        Range repeatRows = modifierData.getRepeatRows();
        unRepeatRows(table, repeatRows);
        return Convenience.buildList(result -> {
            int maxRows = repeatRows.getMax();
            int count = 0;
            TreeWalker treeWalker = new TreeWalker(table, table);
            TreeWalker.VisitState currentState = treeWalker.next();
            while (currentState != null && count <= maxRows) {
                if (currentState.isBeginVisit() && OdfElement.TABLE_ROW.is(currentState.getNode())) {
                    if (repeatRows.contains(count)) {
                        result.add(currentState.getNode());
                    }
                    count++;
                    currentState = treeWalker.nextSkipChildren();
                } else {
                    currentState = treeWalker.next();
                }
            }
        });
    }

    private Optional<Node> findTable() {
        if (modifierData.isInTable()) {
            return Nodes.findAscendantNode(selection.getNode(), OdfElement.TABLE::is);
        } else if (modifierData.getTableName() != null) {
            return OdfNodes.findTable(selection.getNode().getOwnerDocument(), modifierData.getTableName());
        } else {
            return Convenience.buildOptional(result -> {
                for (Node node : getAreaNodesIterator()) {
                    if (OdfElement.TABLE.is(node)) {
                        result.value = node;
                        return;
                    }
                }
            });
        }
    }

}
