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

package org.underdocx.doctypes.odf.commands.forcommand;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.Range;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.modifiers.formodifier.ForModifierData;
import org.underdocx.doctypes.odf.modifiers.forrowsmodifier.OdfForRowsModifier;
import org.underdocx.doctypes.odf.modifiers.forrowsmodifier.OdfForRowsModifierData;
import org.underdocx.doctypes.tools.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.environment.err.Problems;

import java.util.List;
import java.util.Optional;

public class ForRowsCommandHandler<C extends DocContainer<D>, D> extends AbstractForCommandHandler<C, D> {

    public ForRowsCommandHandler(ModifiersProvider modifiers) {
        super(modifiers);
    }

    public static String ROWGROUPSIZE_ATTR = "rowGroupSize";
    public static final String INLIST_ATTR = "parentTable";
    public static final String TABLE_NAME = "tableName";

    private static final PredefinedAttributesInterpreter<Optional<List<Integer>>> rowsListInterpreter =
            AttributeInterpreterFactory.createIntListAttributeInterpreter(TABLEROW_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<Integer>> rowsIntInterpreter =
            AttributeInterpreterFactory.createIntegerAttributeInterpreter(TABLEROW_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<Integer>> groupSizeInterpreter =
            AttributeInterpreterFactory.createIntegerAttributeInterpreter(ROWGROUPSIZE_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<Boolean>> isInTableAttr =
            AttributeInterpreterFactory.createBooleanAttributeInterpreter(INLIST_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<String>> tableNameAttr =
            AttributeInterpreterFactory.createStringAttributeInterpreter(TABLE_NAME);


    @Override
    protected ModifierNodeResult callModifier(ForModifierData forModifierData) {
        Range range = null;
        Optional<List<Integer>> intList = rowsListInterpreter.interpretAttributes(attributes);
        if (intList.isPresent()) {
            range = new Range(intList.get());
        } else {
            range = new Range(Problems.INVALID_VALUE.get(rowsIntInterpreter.interpretAttributes(attributes), TABLEROW_ATTR, null));
        }
        int rowGroupSize = groupSizeInterpreter.interpretAttributes(attributes).orElse(1);
        boolean isInTable = isInTableAttr.interpretAttributes(attributes).orElse(false);
        String tableName = tableNameAttr.interpretAttributes(attributes).orElse(null);
        OdfForRowsModifierData.DefaultOdfForRowsModifierData modifierData
                = new OdfForRowsModifierData.DefaultOdfForRowsModifierData(forModifierData, range, rowGroupSize, isInTable, tableName);
        return new OdfForRowsModifier<>(modifiers).modify(selection, modifierData);
    }

    @Override
    protected boolean isResponsible(JsonNode attributes) {
        return attributes.has(TABLEROW_ATTR);
    }
}