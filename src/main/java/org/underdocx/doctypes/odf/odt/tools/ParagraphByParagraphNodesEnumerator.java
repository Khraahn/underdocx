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

package org.underdocx.doctypes.odf.odt.tools;

import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParagraphByParagraphNodesEnumerator implements Enumerator<Node> {
    private final ParagraphWalker walker;
    private final List<Node> collectedNodes = new ArrayList<>();
    private Node next = null;
    private NodesProvider nodesProvider = null;
    private Node firstValidNode;

    public ParagraphByParagraphNodesEnumerator(
            AbstractOdfContainer<?> doc,
            NodesProvider nodesProvider,
            Node firstValidNode,
            boolean skipParagraphChildNodes) {
        this.nodesProvider = nodesProvider;
        this.firstValidNode = firstValidNode;
        walker = new ParagraphWalker(doc, skipParagraphChildNodes);
        findFirstValidParagraph();
        next = findNext();
    }

    private void findFirstValidParagraph() {
        if (firstValidNode != null) {
            // find top paragraph
            Node firstParagraphToUse = null;
            Node tmp = Nodes.findAscendantNode(firstValidNode, p -> p instanceof TextParagraphElementBase).orElse(null);
            while (tmp != null) {
                firstParagraphToUse = tmp;
                tmp = tmp.getParentNode();
                if (tmp != null) {
                    tmp = Nodes.findAscendantNode(tmp, p -> p instanceof TextParagraphElementBase).orElse(null);
                }
            }
            if (firstParagraphToUse != null) {
                while (next != null && next != firstParagraphToUse) {
                    if (walker.hasNext()) {
                        next = walker.next();
                    } else {
                        next = null;
                    }
                }
            }
        }
    }

    private Node findNext() {
        return collectNodes().isEmpty() ? null : collectedNodes.remove(0);
    }

    private List<Node> collectNodes() {
        while (collectedNodes.isEmpty() && walker.hasNext()) {
            TextParagraphElementBase paragraph = walker.next();
            if (paragraph != null) {
                collectedNodes.addAll(nodesProvider.collectNodes(paragraph, firstValidNode));
                firstValidNode = null;
            }
        }
        return collectedNodes;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Node next() {
        Node result = next;
        next = findNext();
        return result;
    }

    public interface NodesProvider {
        Collection<Node> collectNodes(TextParagraphElementBase p, Node firstValidNode);
    }
}