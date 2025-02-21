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

package org.underdocx.enginelayers.odtengine.modifiers.internal;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreeSplitter;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.enginelayers.baseengine.modifiers.ModifierNodeResult;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.w3c.dom.Node;

import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractAreaModifier<C extends DocContainer<D>, P, D, M extends AreaModifierData, R extends ModifierNodeResult> implements MModifier<C, P, D, M, R> {

    protected static final BiFunction<Node, Node, Node> COMMON_ANCESTOR_P_OR_TABLE_PARENT =
            (first, second) -> OdfNodes.findOldestParagraphOrTableParent(first).get();
    protected static final BiFunction<Node, Node, Node> COMMON_ANCESTOR_NEAREST =
            (first, second) -> Nodes.findCommonAncestor(first, second).get();


    protected MSelection<C, P, D> selection;
    protected M modifierData;
    protected Pair<Node, Node> area;


    public AbstractAreaModifier() {
    }

    protected Node getCommonAncestorNode(M modifierData) {
        return COMMON_ANCESTOR_P_OR_TABLE_PARENT.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
    }

    @Override
    public R modify(MSelection<C, P, D> selection, M modifierData) {
        this.selection = selection;
        this.modifierData = modifierData;
        Node commonAncestor = getCommonAncestorNode(modifierData);
        this.area = splitTreeAndGetArea(modifierData, commonAncestor, OdfTextNodeInterpreter.INSTANCE);
        return modify();
    }

    public static <M extends AreaModifierData> Pair<Node, Node> splitTreeAndGetArea(M modifierData, Node commonAncestor, TextNodeInterpreter interpreter) {
        Pair<Node, Node> placeholderArea = modifierData.getAreaPlaceholderNodes();
        TreeSplitter.split(placeholderArea.left, commonAncestor, interpreter);
        TreeSplitter.split(placeholderArea.right, commonAncestor, interpreter);
        return new Pair<>(
                Nodes.findAncestorChild(placeholderArea.left, commonAncestor).get(),
                Nodes.findAncestorChild(placeholderArea.right, commonAncestor).get());
    }


    protected List<Node> getAreaNodes() {
        return getAreaNodesIterator().collect();
    }

    protected Enumerator<Node> getAreaNodesIterator() {
        return Nodes.getSiblingsIterator(area.left, area.right);
    }

    abstract protected R modify();
}
