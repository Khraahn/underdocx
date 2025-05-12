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

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreePaths;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.ods.OdsContainer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class OdfNodes {


    public static Optional<Node> findAscendantParagraph(Node node, boolean ensureOfficeTextParent) {
        return (!ensureOfficeTextParent)
                ? Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase)
                : Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase
                && currentNode.getParentNode() instanceof OfficeTextElement);
    }


    public static Optional<OdfStylableElement> findOldestParagraphOrTable(Node node) {
        return Nodes.findOldestAncestorNode(node, currentNode -> currentNode instanceof TextParagraphElementBase || currentNode instanceof TableTableElement).map(x -> (OdfStylableElement) x);
    }


    public static Optional<Node> findFirstParagraphOrTableChild(AbstractOdfContainer<?> doc) {
        return Convenience.buildOptional(result ->
                Nodes.findFirstDescendantNode(doc.getContentRoot(), "office:text").ifPresent(node ->
                        findFirstParagraphOrTableChild((OfficeTextElement) node).ifPresent(found ->
                                result.value = found)));
    }

    private static Optional<Node> findFirstParagraphOrTableChild(OfficeTextElement content) {
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


    private static Optional<OdfStylableElement> findAncestorParagraphOrTable(Node node) {
        return Nodes.findAscendantNode(node, currentNode -> currentNode instanceof TextParagraphElementBase || currentNode instanceof TableTableElement).map(x -> (OdfStylableElement) x);
    }


    public static Optional<Node> findAncestorParagraphOrTableParent(Node node) {
        Optional<OdfStylableElement> p = OdfNodes.findAncestorParagraphOrTable(node);
        if (p.isPresent()) {
            return Optional.ofNullable(p.get().getParentNode());
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Node> findMainTable(OdsContainer doc, String name) {
        return Convenience.buildOptional(result -> {
            Node content = doc.getContentDom().getFirstChild();
            TreePaths paths = new TreePaths(content, OdfElement.OFFICE_BODY, OdfElement.OFFICE_SPREADSHEET, OdfElement.TABLE);
            for (Node spreadsheet : paths) {
                if (OdfAttribute.TABLE_NAME.hasValue(spreadsheet, name)) {
                    result.value = spreadsheet;
                }
            }
        });
    }

    public static <T extends OdfDocument> Optional<Node> findMainPage(AbstractOdfContainer<T> doc, String name) {
        return Convenience.buildOptional(result -> {
            Node content = doc.getContentDom().getFirstChild();
            TreePaths paths = new TreePaths(content, OdfElement.OFFICE_BODY, OdfElement.OFFICE_DRAWING.or(OdfElement.OFFICE_PRESENTATION), OdfElement.PAGE);
            for (Node page : paths) {
                if (OdfAttribute.DRAW_NAME.hasValue(page, name)) {
                    result.value = page;
                }
            }
        });
    }

    /**
     * returns the office:spreadsheet node for ODS documents
     * returns the office:drawing node for ODG documents
     * returns the office:presentation node for ODP documents
     * returns the office:text node for ODT documents
     */
    public static <T extends OdfDocument> Optional<Node> getMainContentNode(AbstractOdfContainer<T> doc) {
        return Convenience.buildOptional(result -> {
            Node content = doc.getContentDom().getFirstChild();
            TreePaths paths = new TreePaths(
                    content,
                    OdfElement.OFFICE_BODY,
                    OdfElement.OFFICE_DRAWING
                            .or(OdfElement.OFFICE_PRESENTATION)
                            .or(OdfElement.OFFICE_TEXT)
                            .or(OdfElement.OFFICE_SPREADSHEET));
            if (paths.hasNext()) {
                result.value = paths.next();
            }
        });
    }

}
