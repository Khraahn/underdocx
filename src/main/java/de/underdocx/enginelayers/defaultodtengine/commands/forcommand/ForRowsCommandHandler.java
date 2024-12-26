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

package de.underdocx.enginelayers.defaultodtengine.commands.forcommand;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.PredefinedAttributesInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;
import de.underdocx.enginelayers.defaultodtengine.modifiers.formodifier.ForModifierData;
import de.underdocx.enginelayers.defaultodtengine.modifiers.forrowsmodifier.ForRowsModifier;
import de.underdocx.enginelayers.defaultodtengine.modifiers.forrowsmodifier.ForRowsModifierData;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Range;

import java.util.List;
import java.util.Optional;

public class ForRowsCommandHandler<C extends DocContainer<D>, D> extends AbstractForCommandHandler<C, D> {

    private static PredefinedAttributesInterpreter<Optional<List<Integer>>> rowsListInterpreter =
            AttributeInterpreterFactory.createIntListAttributeInterpreter(TABLEROW_ATTR);
    private static PredefinedAttributesInterpreter<Optional<Integer>> rowsIntInterpreter =
            AttributeInterpreterFactory.createIntegerAttributeInterpreter(TABLEROW_ATTR);

    @Override
    protected void callModifier(ForModifierData forModifierData) {
        Range range = null;
        Optional<List<Integer>> intList = rowsListInterpreter.interpretAttributes(attributes);
        if (intList.isPresent()) {
            range = new Range(intList.get());
        } else {
            range = new Range(Problems.INVALID_VALUE.get(rowsIntInterpreter.interpretAttributes(attributes), TABLEROW_ATTR, null));
        }
        ForRowsModifierData.DefaultForRowsModifierData modifierData
                = new ForRowsModifierData.DefaultForRowsModifierData(forModifierData, range);
        new ForRowsModifier<C, D>().modify(selection, modifierData);
    }

    @Override
    protected boolean isResponsible(JsonNode attributes) {
        return attributes.has(TABLEROW_ATTR);
    }
}