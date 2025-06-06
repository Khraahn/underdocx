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

package org.underdocx.doctypes.odf.modifiers.stringmodifier;

import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.markup.MarkupNodeBuilder;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.markup.MarkupXMLDOMParser;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.markup.NodeStyler;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.markup.TextStyle;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Map;

public class OdfMarkupTextModifier<C extends DocContainer<D>, D> implements SelectionModifier<Selection<C, ParametersPlaceholderData, D>, String, ModifierResult> {

    private static final MarkupXMLDOMParser parser = new MarkupXMLDOMParser();

    @Override
    public ModifierResult modify(Selection<C, ParametersPlaceholderData, D> selection, String modifierData) {
        Node placeholder = selection.getNode();
        Document xml = Problems.CODEC_ERROR.get(parser.tryParse(modifierData), null, modifierData);
        Map<Node, TextStyle> mapping = new MarkupNodeBuilder(OdfTextNodeInterpreter.INSTANCE).createTree(placeholder, xml);
        new NodeStyler().styleNodes(placeholder, mapping);
        return ModifierResult.SUCCESS;
    }
}
