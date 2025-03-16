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

package org.underdocx.doctypes.txt;

import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.internal.AbstractModifier;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.w3c.dom.Node;

import java.util.function.Predicate;

public class TxtImportModifier extends AbstractModifier<TxtContainer, TxtXml> implements SelectionModifier<Node, TxtImportModifierData, ModifierNodeResult> {
    public TxtImportModifier(ModifiersProvider<TxtContainer, TxtXml> modifiers) {
        super(modifiers);
    }

    @Override
    public ModifierNodeResult modify(Node selection, TxtImportModifierData modifierData) {
        Wrapper<Node> first = new Wrapper<>();
        Nodes.findAscendantNode(selection, "p").ifPresent(p -> {
            Nodes.findFirstDescendantNode(modifierData.getImportDoc().getDocument().getDoc(), "root").ifPresent(root -> {
                Nodes.getChildren(root, new FragmentFilter(modifierData.getBeginFragmentRegex(), modifierData.getEndFragmentRegex())).forEach(nodeToClone -> {
                    Node clone = nodeToClone.cloneNode(true);
                    clone = p.getOwnerDocument().adoptNode(clone);
                    if (first.value == null) {
                        first.value = clone;
                    }
                    p.getParentNode().insertBefore(clone, p);
                });
            });
            modifiers.getDeletePlaceholderModifier().modify(selection, DeletePlaceholderModifierData.DEFAULT);
        });
        if (first.value == null) {
            return ModifierResult.IGNORED;
        } else {
            return ModifierNodeResult.FACTORY.success(first.value, false);
        }
    }


    private static class FragmentFilter implements Predicate<Node> {

        private final Regex beginFragmentRegex;
        private final Regex endFragmentRegex;

        private enum FragmentState {
            WAIT_BEGIN,
            WAIT_END,
            COPY_ALL,
            DONE
        }

        private FragmentState state = FragmentState.COPY_ALL;

        protected FragmentFilter(Regex beginFragmentRegex, Regex endFragmentRegex) {
            this.beginFragmentRegex = beginFragmentRegex;
            this.endFragmentRegex = endFragmentRegex;

            if (beginFragmentRegex != null) {
                state = FragmentState.WAIT_BEGIN;
            }
        }

        @Override
        public boolean test(Node node) {
            return switch (state) {
                case COPY_ALL -> true;
                case WAIT_BEGIN -> {
                    if (beginFragmentRegex.contains(node.getTextContent())) {
                        state = FragmentState.WAIT_END;
                    }
                    yield false;
                }
                case WAIT_END -> {
                    if (endFragmentRegex != null && endFragmentRegex.contains(node.getTextContent())) {
                        state = FragmentState.DONE;
                        yield false;
                    }
                    yield true;
                }
                case DONE -> false;
            };
        }
    }
}
