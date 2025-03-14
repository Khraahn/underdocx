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

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.w3c.dom.Node;

public class PlainTextNodeInterpreter implements TextNodeInterpreter {

    public static TextNodeInterpreter INSTANCE = new PlainTextNodeInterpreter();

    protected boolean is(Node node, String name) {
        return name.equals(node.getNodeName());
    }


    @Override
    public String getText(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) return node.getNodeValue();
        if (is(node, "span")) return "";
        return null;
    }

    @Override
    public boolean isPlainTextType(Node node) {
        return node.getNodeType() == Node.TEXT_NODE || is(node, "span");
    }

    @Override
    public boolean isPlainTextContainerType(Node node) {
        return is(node, "span");
    }

    @Override
    public boolean isPartialTextContainerType(Node node) {
        return is(node, "p");
    }

    @Override
    public boolean isTextRelatedType(Node node) {
        return isPlainTextContainerType(node) || isPlainTextType(node) || isPartialTextContainerType(node);
    }

    @Override
    public boolean isTextOnlyType(Node node) {
        return isPlainTextContainerType(node) || isPlainTextType(node);
    }

    @Override
    public Node createTextContainer(Node parent) {
        return Convenience.also(parent.getOwnerDocument().createElement("span"), span -> parent.appendChild(span));
    }

    @Override
    public void setNodeText(Node node, String text) {
        Nodes.setNodeText(node, text);
    }


    @Override
    public void appendNodeText(Node node, String text) {
        Nodes.appendNodeText(node, text);
    }

}
