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

package org.underdocx.doctypes.odf.tools;

import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class OdfNodes {

    public static OdfFileDom getFileDom(Node odfNode) {
        return ((OdfFileDom) odfNode.getOwnerDocument());
    }

    public static Optional<Node> findAscendantParagraph(Node node, boolean ensureOfficeTextParent) {
        return (!ensureOfficeTextParent)
                ? Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase)
                : Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase
                && currentNode.getParentNode() instanceof OfficeTextElement);
    }

    public static Optional<TextParagraphElementBase> findOldestParagraph(Node node) {
        return Nodes.findOldestAncestorNode(node, currentNode -> currentNode instanceof TextParagraphElementBase).map(x -> (TextParagraphElementBase) x);
    }

    public static Optional<OdfStylableElement> findOldestParagraphOrTable(Node node) {
        return Nodes.findOldestAncestorNode(node, currentNode -> currentNode instanceof TextParagraphElementBase || currentNode instanceof TableTableElement).map(x -> (OdfStylableElement) x);
    }

    public static Optional<Node> findOldestParagraphOrTableParent(Node node) {
        Optional<OdfStylableElement> p = findOldestParagraphOrTable(node);
        if (p.isPresent()) {
            return Optional.ofNullable(p.get().getParentNode());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<OdfStylableElement> findAncestorParagraphOrTable(Node node) {
        return Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase || currentNode instanceof TableTableElement).map(x -> (OdfStylableElement) x);
    }

    public static Optional<Node> findAncestorParagraphOrTableParent(Node node) {
        Optional<OdfStylableElement> p = findAncestorParagraphOrTable(node);
        if (p.isPresent()) {
            return Optional.ofNullable(p.get().getParentNode());
        } else {
            return Optional.empty();
        }
    }


    public static Optional<Node> findFirstParagraphOrTableChild(AbstractOdfContainer doc) {
        return Convenience.buildOptional(result -> {
            Nodes.findFirstDescendantNode(doc.getContentRoot(), "office:text").ifPresent(node ->
                    findFirstParagraphOrTableChild((OfficeTextElement) node).ifPresent(found ->
                            result.value = found));
        });
    }

    public static Optional<Node> findFirstParagraphOrTableChild(OfficeTextElement content) {
        NodeList list = content.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof TextParagraphElementBase) {
                return Optional.of(node);
            } else if (node instanceof TableTableElement) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

}
