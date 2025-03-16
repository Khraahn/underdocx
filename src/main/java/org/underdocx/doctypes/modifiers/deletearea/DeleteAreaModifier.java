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

package org.underdocx.doctypes.modifiers.deletearea;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.internal.AbstractAreaModifier;
import org.underdocx.doctypes.modifiers.internal.AreaModifierWithCommonAncestorData;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;


public class DeleteAreaModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, AreaModifierWithCommonAncestorData, ModifierNodeResult> {

    public DeleteAreaModifier(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected Node getCommonAncestorNode(AreaModifierWithCommonAncestorData modifierData) {
        return modifierData.getCommonAncestor();
    }

    @Override
    protected ModifierNodeResult modify() {
        return Convenience.also(ModifierNodeResult.FACTORY.success(area.right, true), result -> Nodes.deleteNodes(getAreaNodes()));
    }
}
