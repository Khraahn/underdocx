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

package de.underdocx.enginelayers.defaultodtengine.modifiers.deletearea;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.enginelayers.defaultodtengine.modifiers.internal.AbstractAreaModifier;
import de.underdocx.enginelayers.defaultodtengine.modifiers.internal.AreaModifierData;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.odf.OdfTools;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreeSplitter;
import org.w3c.dom.Node;

public class DeleteAreaModifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, AreaModifierData> {

    @Override
    protected boolean modify() {
        Nodes.deleteNodes(getAreaNodes());
        return true;
    }

    public static void deleteArea(AreaModifierData modifierData) {
        Pair<Node, Node> a = modifierData.getAreaPlaceholderNodes();
        Node ancestor = OdfTools.findOldestParagraphOrTableParent(a.right).get();
        TreeSplitter.split(a.left, ancestor, OdfTextNodeInterpreter.INSTANCE);
        TreeSplitter.split(a.right, ancestor, OdfTextNodeInterpreter.INSTANCE);
        Nodes.deleteNodes(Nodes.getSiblings(a.left, a.right));
    }
}
