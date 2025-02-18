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

package org.underdocx.enginelayers.defaultodtengine.modifiers.ifmodifier;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.enginelayers.baseengine.modifiers.ModifierResult;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.defaultodtengine.modifiers.deletearea.DeleteAreaModifier;
import org.underdocx.enginelayers.defaultodtengine.modifiers.internal.AbstractAreaModifier;
import org.underdocx.enginelayers.defaultodtengine.modifiers.internal.AreaModifierWithCommonAncestorData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

public class IfModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, IfModifierData, ModifierResult> {

    @Override
    protected Node getCommonAncestorNode(IfModifierData modifierData) {
        return COMMON_ANCESTOR_NEAREST.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
    }

    @Override
    protected ModifierResult modify() {
        if (modifierData.isMatch()) {
            DeletePlaceholderModifier.modify(area.left, DeletePlaceholderModifierData.DEFAULT);
            return DeletePlaceholderModifier.modify(area.right, DeletePlaceholderModifierData.DEFAULT);
        } else {
            return DeleteAreaModifier.deleteArea(new AreaModifierWithCommonAncestorData.DefaultAreaModifierWithCommonAncestorData(
                    area, getCommonAncestorNode(modifierData)
            ));
        }
    }
}