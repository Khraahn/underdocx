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

package org.underdocx.doctypes.odf.modifiers.ifmodifier;

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.internal.AbstractAreaModifier;
import org.underdocx.doctypes.odf.modifiers.internal.AreaModifierWithCommonAncestorData;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

public class IfModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, IfModifierData, ModifierNodeResult> {

    public IfModifier(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected Node getCommonAncestorNode(IfModifierData modifierData) {
        return COMMON_ANCESTOR_NEAREST.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
    }

    @Override
    protected ModifierNodeResult modify() {
        if (modifierData.isMatch()) {
            modifiers.getDeletePlaceholderModifier().modify(area.left, DeletePlaceholderModifierData.DEFAULT);
            return modifiers.getDeletePlaceholderModifier().modify(area.right, DeletePlaceholderModifierData.DEFAULT);
        } else {
            return modifiers.getDeleteAreaModifier().modify(selection, new AreaModifierWithCommonAncestorData.DefaultAreaModifierWithCommonAncestorData(
                    area, getCommonAncestorNode(modifierData)
            ));
        }
    }
}