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
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.tools.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.doctypes.odf.modifiers.forlistmodifier.ForListModifier;
import org.underdocx.doctypes.odf.modifiers.forlistmodifier.ForListModifierData;
import org.underdocx.doctypes.odf.modifiers.formodifier.ForModifierData;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;

import java.util.Optional;


public class ForListCommandHandler<C extends DocContainer<D>, D> extends AbstractForCommandHandler<C, D> {

    private static PredefinedAttributesInterpreter<Optional<Integer>> indexInterpreter =
            AttributeInterpreterFactory.createIntegerAttributeInterpreter(LISTITEM_ATTR);

    @Override
    protected ModifierNodeResult callModifier(ForModifierData forModifierData) {
        Integer listItem = indexInterpreter.interpretAttributes(attributes).orElse(0);
        ForListModifierData listData = new ForListModifierData.DefaultForListModifierData(forModifierData, listItem);
        return new ForListModifier<C, D>().modify(selection, listData);
    }

    @Override
    protected boolean isResponsible(JsonNode attributes) {
        return attributes.has(LISTITEM_ATTR);
    }
}