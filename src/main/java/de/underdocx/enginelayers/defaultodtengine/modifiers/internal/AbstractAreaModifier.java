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

package de.underdocx.enginelayers.defaultodtengine.modifiers.internal;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.TextNodeInterpreter;
import de.underdocx.enginelayers.modelengine.MSelection;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.odf.OdfTools;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreeSplitter;
import org.w3c.dom.Node;

import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractAreaModifier<C extends DocContainer<D>, P, D, M extends AreaModifierData> implements MModifier<C, P, D, M> {

    protected MSelection<C, P, D> selection;
    protected M modifierData;
    protected BiFunction<Node, Node, Node> commonAncestorProvider;
    protected Pair<Node, Node> area;

    public AbstractAreaModifier(BiFunction<Node, Node, Node> commonAncestorProvider) {
        this.commonAncestorProvider = commonAncestorProvider;
    }

    public AbstractAreaModifier() {
        this.commonAncestorProvider = (first, second) ->
                OdfTools.findOldestParagraphOrTableParent(first).get();
    }

    @Override
    public boolean modify(MSelection<C, P, D> selection, M modifierData) {
        this.selection = selection;
        this.modifierData = modifierData;
        Node commonAncestor = commonAncestorProvider.apply(modifierData.getAreaPlaceholderNodes().left, modifierData.getAreaPlaceholderNodes().right);
        Pair<Node, Node> placeholderArea = modifierData.getAreaPlaceholderNodes();
        if (splitTrees()) {
            TreeSplitter.split(placeholderArea.left, commonAncestor, getTextNodeInterpreter());
            TreeSplitter.split(placeholderArea.right, commonAncestor, getTextNodeInterpreter());
        }
        this.area = new Pair<>(
                Nodes.findAncestorChild(placeholderArea.left, commonAncestor).get(),
                Nodes.findAncestorChild(placeholderArea.right, commonAncestor).get());
        return modify();
    }


    protected List<Node> getAreaNodes() {
        return Nodes.getSiblings(area.left, area.right);
    }

    protected boolean splitTrees() {
        return true;
    }

    protected TextNodeInterpreter getTextNodeInterpreter() {
        return OdfTextNodeInterpreter.INSTANCE;
    }


    abstract protected boolean modify();
}
