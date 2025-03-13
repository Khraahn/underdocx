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

package org.underdocx.doctypes.odf.modifiers.stringmodifier.markup;

import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.IdentityHashMap;
import java.util.Map;

public class MarkupNodeBuilder {

    private final TextNodeInterpreter interpreter;
    private Map<Node, TextStyle> odfNodeXmlNodeMapping = null;

    public MarkupNodeBuilder(TextNodeInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Map<Node, TextStyle> createTree(Node odfNode, Node xmlNode) {
        odfNodeXmlNodeMapping = new IdentityHashMap<>();
        Nodes.deleteChildren(odfNode);
        createTree(odfNode, xmlNode, new TextStyle());
        return odfNodeXmlNodeMapping;
    }

    private void createTree(Node odfNode, Node xmlNode, TextStyle currentStyle) {
        Document targetDoc = odfNode.getOwnerDocument();
        if (xmlNode.hasChildNodes()) {
            NodeList nodeList = xmlNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node childToCopy = nodeList.item(i);
                if (childToCopy.getNodeType() == Node.TEXT_NODE) {
                    Node clonedNode = targetDoc.importNode(childToCopy, true);
                    interpreter.appendNodeText(odfNode, clonedNode.getTextContent());
                } else if (childToCopy instanceof Element) {
                    Node newSpan = interpreter.createTextContainer(odfNode);
                    TextStyle derivedTextStyle = deriveTextStyle((Element) childToCopy, currentStyle);
                    odfNodeXmlNodeMapping.put(newSpan, derivedTextStyle);
                    createTree(newSpan, childToCopy, derivedTextStyle);
                }
            }
        }
    }

    private TextStyle deriveTextStyle(Element childToCopy, TextStyle currentStyle) {
        TextStyle newStyle = new TextStyle(currentStyle);
        String tagName = childToCopy.getNodeName();
        switch (tagName) {
            case "b" -> newStyle.bold = true;
            case "i" -> newStyle.italic = true;
            case "u" -> newStyle.underline = true;
            case "font" -> {
                String size = childToCopy.getAttribute("size");
                if (size != null && !size.isBlank()) {
                    newStyle.fontSize = size.trim();
                }
                String name = childToCopy.getAttribute("name");
                if (name != null && !name.isBlank()) {
                    newStyle.fontName = name.trim();
                }
            }
        }
        return newStyle;
    }
}
