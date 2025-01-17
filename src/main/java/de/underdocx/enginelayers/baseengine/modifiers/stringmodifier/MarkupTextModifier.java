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

package de.underdocx.enginelayers.baseengine.modifiers.stringmodifier;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.modifiers.Modifier;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.markup.MarkupNodeBuilder;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.markup.MarkupXMLDOMParser;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.markup.NodeStyler;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.markup.TextStyle;
import de.underdocx.environment.err.Problems;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Map;

public class MarkupTextModifier<C extends DocContainer<D>, P, D> implements Modifier<C, P, D, String> {

    private static MarkupXMLDOMParser parser = new MarkupXMLDOMParser();

    @Override
    public boolean modify(Selection<C, P, D> selection, String modifierData) {
        Document xml = Problems.CODEC_ERROR.get(parser.tryParse(modifierData), null, modifierData);
        Node placeholder = selection.getNode();
        Map<Node, TextStyle> mapping = new MarkupNodeBuilder(OdfTextNodeInterpreter.INSTANCE).createTree(placeholder, xml);
        new NodeStyler().styleNodes(placeholder, mapping);
        return true;
    }
}
