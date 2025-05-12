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

package org.underdocx.common.debug;


import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.List;

public class NodePrinter {

    private final boolean printAttributes;
    private final boolean printParent;
    private final Node node;

    public NodePrinter(Node node, boolean printParents, boolean printAttributes) {
        this.printParent = printParents;
        this.printAttributes = printAttributes;
        this.node = node;
    }

    public NodePrinter(Node node) {
        this(node, true, true);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (printParent) {
            List<Node> path = Convenience.reverse(Nodes.getAncestors(node, null));
            path.remove(path.size() - 1);
            path.forEach(element -> sb.append("<").append(element.getNodeName()).append(">"));
        }
        if (printAttributes) {
            sb.append("<").append(node.getNodeName());
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                sb.append(" ");
                sb.append(attributes.item(i).getNodeName());
                sb.append("=\"");
                sb.append(attributes.item(i).getNodeValue());
                sb.append("\"");
                if (i == attributes.getLength() - 1) {
                    sb.append(">");
                }
            }
        } else {
            sb.append("<").append(node.getNodeName()).append(">");
        }
        return sb.toString();
    }

    public static void print(Node node) {
        System.out.println(new NodePrinter(node));
    }

    public static void print(Node node, boolean writeParents, boolean writeAttributes) {
        System.out.println(new NodePrinter(node, writeParents, writeAttributes));
    }
}
