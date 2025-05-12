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

package org.underdocx.doctypes.odf.modifiers.tablecell;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Tripple;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.tablecell.TableCellModifierData;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.tools.OdfTables;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class OdfTableCellModifier<C extends DocContainer<D>, D> implements SelectionModifier<MSelection<C, ParametersPlaceholderData, D>, TableCellModifierData, ModifierResult> {


    @Override
    public ModifierResult modify(MSelection<C, ParametersPlaceholderData, D> selection, TableCellModifierData modifierData) {
        return Convenience.build((ModifierResult) ModifierResult.IGNORED, result ->
                OdfTables.parseCellReference(modifierData.styleCellAddress()).ifPresent(address ->
                        findTable(selection, address).ifPresent(table -> {
                            Pair<Node, Node> pair = OdfTables.getStyleNodes(table, address.middle, address.right);
                            findParentCell(selection).ifPresent(cell ->
                                    result.value = setValue(cell, pair, modifierData.value()));
                        })));
    }

    private ModifierResult setValue(Node cell, Pair<Node, Node> templatePair, Object value) {
        String styleName = Problems.MISSING_VALUE.get(OdfTables.getCellStyle(templatePair), "style of template table cell");
        Node template = Problems.MISSING_VALUE.notNull(templatePair.right, "template table cell");
        if (value instanceof LocalDateTime time) {
            OdfAttribute.OFFICE_TIME_VALUE.setAttributeNS(cell, DateTimeFormatter.ofPattern("'PT'HH'H'mm'M'ss'S'").format(time));
        } else if (value instanceof LocalDate date) {
            OdfAttribute.OFFICE_DATE_VALUE.setAttributeNS(cell, DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date));
        } else if (value instanceof Number number) {
            OdfAttribute.OFFICE_VALUE.setAttributeNS(cell, String.valueOf(number.doubleValue()));
        } else if (value instanceof String) {
            /* no value to copy */
        } else {
            return ModifierResult.IGNORED;
        }
        OdfAttribute.TABLE_STYLE_NAME.setAttributeNS(cell, styleName);
        OdfAttribute.OFFICE_VALUE_TYPE.copy(template, cell);
        OdfAttribute.CALCEXT_VALUE_TYPE.copy(template, cell);
        OdfAttribute.OFFICE_CURRENCY.copy(template, cell);
        OdfTables.getCellParagraphStyle(templatePair).ifPresent(pStyle ->
                Nodes.getChildren(cell, OdfElement.PARAGRAPH).tryNext().ifPresent(p ->
                        OdfAttribute.TEXT_STYLE_NAME.setAttributeNS(p, pStyle)));
        return ModifierResult.SUCCESS;
    }

    private Optional<Node> findTable(MSelection<C, ParametersPlaceholderData, D> selection, Tripple<String, Integer, Integer> cellAddress) {
        if (cellAddress.left == null) {
            return findParentTable(selection);
        } else {
            return OdfTables.findTable(selection.getNode().getOwnerDocument(), cellAddress.left);
        }
    }

    private Optional<Node> findParentCell(MSelection<C, ParametersPlaceholderData, D> selection) {
        return Nodes.findAscendantNode(selection.getNode(), OdfElement.TABLE_CELL::is);
    }

    private Optional<Node> findParentTable(MSelection<C, ParametersPlaceholderData, D> selection) {
        return Nodes.findAscendantNode(selection.getNode(), OdfElement.TABLE::is);
    }
}
