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

package org.underdocx.doctypes.odf.modifiers;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deletearea.DeleteAreaModifier;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.modifiers.internal.AreaModifierWithCommonAncestorData;
import org.underdocx.doctypes.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.doctypes.modifiers.tablecell.TableCellModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.OdfMarkupTextModifier;
import org.underdocx.doctypes.odf.modifiers.tablecell.OdfTableCellModifier;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.util.Optional;

public class OdfModifiersProvider<D extends AbstractOdfContainer<C>, C extends OdfDocument> implements ModifiersProvider<D, C> {
    @Override
    public SelectionModifier<Node, DeletePlaceholderModifierData, ModifierNodeResult> getDeletePlaceholderModifier() {
        return new OdfDeletePlaceholderModifier();
    }

    @Override
    public SelectionModifier<Selection<D, ParametersPlaceholderData, C>, String, ModifierResult> getReplaceWithTextModifier() {
        return new ReplaceWithTextModifier<>();
    }

    @Override
    public SelectionModifier<MSelection<D, ParametersPlaceholderData, C>, AreaModifierWithCommonAncestorData, ModifierNodeResult> getDeleteAreaModifier() {
        return new DeleteAreaModifier<>(this);
    }

    @Override
    public SelectionModifier<Selection<D, ParametersPlaceholderData, C>, String, ModifierResult> getMarkupTextModifier() {
        return new OdfMarkupTextModifier<>();
    }

    @Override
    public SelectionModifier<MSelection<D, ParametersPlaceholderData, C>, TableCellModifierData, ModifierResult> getTableCellModifier() {
        return new OdfTableCellModifier<>();
    }

    @Override
    public Optional<Node> findAncestorParagraphOrTableParent(Node node) {
        return OdfNodes.findAncestorParagraphOrTableParent(node);
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return OdfTextNodeInterpreter.INSTANCE;
    }
}
