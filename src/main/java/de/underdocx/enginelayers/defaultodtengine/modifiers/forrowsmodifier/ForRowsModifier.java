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

package de.underdocx.enginelayers.defaultodtengine.modifiers.forrowsmodifier;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import de.underdocx.enginelayers.defaultodtengine.modifiers.internal.AbstractAreaModifier;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Range;
import de.underdocx.tools.odf.constants.OdfElement;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreeWalker;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForRowsModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, ForRowsModifierData> {
    @Override
    protected boolean modify() {
        Problems.INVALID_VALUE.check(modifierData.getRepeatRows().getLength() % modifierData.getRowGroupSize() == 0, "rowgroupsize", "" + modifierData.getRowGroupSize());
        List<Node> clonedRows = cloneRows();
        insertPlaceholdersInClonedRows(clonedRows);
        DeletePlaceholderModifier.modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        DeletePlaceholderModifier.modify(area.right, DeletePlaceholderModifierData.DEFAULT);
        return true;
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
        return Problems.CANT_FIND_DOM_ELEMENT.notNullOrEmpty(getCloneableRows(table), "row");
    }

    private List<Node> getCloneableRows(Node table) {
        Range repeatRows = modifierData.getRepeatRows();
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
