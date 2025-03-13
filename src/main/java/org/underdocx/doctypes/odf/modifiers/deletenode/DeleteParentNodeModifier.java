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

package org.underdocx.doctypes.odf.modifiers.deletenode;

import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.w3c.dom.Node;

import java.util.Optional;

public class DeleteParentNodeModifier<C extends DocContainer<D>, P, D> implements SelectionModifier<Selection<C, P, D>, DeleteParentModifierData, ModifierNodeResult> {
    @Override
    public ModifierNodeResult modify(Selection<C, P, D> selection, DeleteParentModifierData modifierData) {
        Node childNode = selection.getNode();
        return modify(childNode, modifierData);
    }

    public static ModifierNodeResult modify(Node childNode, DeleteParentModifierData modifierData) {
        Optional<Node> node = Nodes.findAscendantNode(childNode, modifierData.getParentFilter());
        if (node.isPresent()) {
            return DeleteNodeModifier.modify(node.get());
        } else return ModifierNodeResult.IGNORED;
    }
}
