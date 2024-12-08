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

package de.underdocx.common.placeholder.basic.textnodeinterpreter;

import org.w3c.dom.Node;

public interface TextNodeInterpreter {

    /**
     * return the text content of this single node (not of its children)
     *
     * @param node a node
     * @return text content or null
     */
    String getText(Node node);

    /**
     * returns true if this single node contains text
     * This is: W3C Node.TEXT_NODE, "s", "tab", "line-break"
     *
     * @param node node to check
     * @return node contains text itself
     */
    boolean isPlainTextType(Node node);

    /**
     * returns true if this single node contains ony node that represents text or
     * and/or other plain text containers, for example: "span", "a"
     *
     * @param node
     * @return
     */
    boolean isPlainTextContainerType(Node node);

    /**
     * returns true if this single node usualy contain text nodes but can contains other
     * nodes, e.g. "p"
     *
     * @param node
     * @return
     */
    boolean isPartialTextContainerType(Node node);

    /**
     * returns true if this node is a (partial) text container or a text representing node
     *
     * @param node
     * @return
     */
    boolean isTextRelatedType(Node node);

    boolean isTextOnlyType(Node node);

    Node createTextContainer(Node parent);

    void setNodeText(Node node, String text);

}
