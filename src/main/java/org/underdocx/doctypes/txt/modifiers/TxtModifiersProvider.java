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

package org.underdocx.doctypes.txt.modifiers;

import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deletearea.DeleteAreaModifier;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.modifiers.internal.AreaModifierWithCommonAncestorData;
import org.underdocx.doctypes.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.doctypes.modifiers.tablecell.TableCellModifierData;
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.TxtXml;
import org.underdocx.doctypes.txt.placeholders.TxtNodeInterpreter;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.util.Optional;

public class TxtModifiersProvider implements ModifiersProvider<TxtContainer, TxtXml> {
    @Override
    public SelectionModifier<Node, DeletePlaceholderModifierData, ModifierNodeResult> getDeletePlaceholderModifier() {
        return new TxtDeletePlaceholderModifier();
    }


    @Override
    public SelectionModifier<Selection<TxtContainer, ParametersPlaceholderData, TxtXml>, String, ModifierResult> getMarkupTextModifier() {
        return (selection, modifierData) -> getReplaceWithTextModifier().modify(selection, modifierData);
    }

    @Override
    public SelectionModifier<MSelection<TxtContainer, ParametersPlaceholderData, TxtXml>, TableCellModifierData, ModifierResult> getTableCellModifier() {
        return (selection, modifierData) -> ModifierResult.SUCCESS;
    }

    @Override
    public Optional<Node> findAncestorParagraphOrTableParent(Node node) {
        Optional<Node> p = Nodes.findAscendantNode(node, "p");
        if (p.isPresent()) {
            return Optional.ofNullable(p.get().getParentNode());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public SelectionModifier<Selection<TxtContainer, ParametersPlaceholderData, TxtXml>, String, ModifierResult> getReplaceWithTextModifier() {
        return new ReplaceWithTextModifier<>();
    }

    @Override
    public SelectionModifier<MSelection<TxtContainer, ParametersPlaceholderData, TxtXml>, AreaModifierWithCommonAncestorData, ModifierNodeResult> getDeleteAreaModifier() {
        return new DeleteAreaModifier<>(this);
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return TxtNodeInterpreter.INSTANCE;
    }
}
