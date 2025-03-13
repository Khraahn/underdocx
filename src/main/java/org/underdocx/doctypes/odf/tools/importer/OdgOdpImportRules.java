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

package org.underdocx.doctypes.odf.tools.importer;

import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.tools.importer.rules.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Default Rules which nodes have to be copied and which attribute values have to be renamed
 */
public class OdgOdpImportRules extends AbstractImportRules {
    private static final TagDescr DOCUMENT_STYLES = t("office:document-styles");
    private static final TagDescr DOCUMENT_CONTENT = t("office:document-content");
    private static final TagDescr OFFICE_BODY = t("office:body");
    private static final TagDescr OFFICE_STYLES = t("office:styles");
    private static final TagDescr STYLE_STYLE = t("style:style");
    private static final TagDescr OFFICE_AUTOMATIC_STYLES = t("office:automatic-styles");
    private static final TagDescr OFFICE_DRAWING = t("office:drawing");
    private static final TagDescr OFFICE_PRESENTATION = t("office:presentation");
    private static final TagDescr DRAW_PAGE = t("draw:page");
    private static final AttrDescr DRAW_PAGE_NAME = a("draw:name");

    private static final NodeFilter DrawingOrPresentation =
            (node -> OFFICE_DRAWING.test(node) || OFFICE_PRESENTATION.test(node));


    private final String pageName;
    private NodeFilter copyContentNodeFilter = NodeFilter.ACCEPT_ALL;

    private OdgOdpImportRules(String pageName) {
        this.pageName = pageName;
    }

    protected void createRules() {
        providerDescrs.addAll(Arrays.asList(
                new ProviderDescr(
                        new TagPathDescr(true,
                                DOCUMENT_CONTENT, OFFICE_AUTOMATIC_STYLES, STYLE_STYLE
                        ), new AttrDescr("style:name")),
                new ProviderDescr(
                        new TagPathDescr(false,
                                DOCUMENT_STYLES, OFFICE_STYLES, STYLE_STYLE
                        ), new AttrDescr("style:name"))));

        consumerDescrs.add(new ConsumerDescr(new AttrDescr("style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("parent-style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("text-style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("marker-style-name")));

        copyRules.add(new TagPathDescr(false, DOCUMENT_STYLES, OFFICE_STYLES, STYLE_STYLE));
        copyRules.add(new TagPathDescr(false, DOCUMENT_STYLES, OFFICE_AUTOMATIC_STYLES, NodeFilter.ACCEPT_ALL));
        copyRules.add(new TagPathDescr(true, DOCUMENT_CONTENT, OFFICE_AUTOMATIC_STYLES, NodeFilter.ACCEPT_ALL));

        mainCopyRule = new TagPathDescr(true, DOCUMENT_CONTENT, OFFICE_BODY, DrawingOrPresentation, getPageWithName(pageName), copyContentNodeFilter);
        mainCopyExecutor = new OdgOdpCopyExecutor(mainCopyRule);
    }

    private NodeFilter getPageWithName(String name) {
        return node -> DRAW_PAGE.test(node) && name.equals(DRAW_PAGE_NAME.getValue(node).orElse(null));
    }

    public static OdgOdpImportRules createRules(String pageName, NodeFilter copyContentNodeFilter) {
        OdgOdpImportRules result = new OdgOdpImportRules(pageName);
        result.copyContentNodeFilter = copyContentNodeFilter;
        result.init();
        return result;
    }

    public static class OdgOdpCopyExecutor implements MainCopyExecutor {
        private final TagPathDescr mainCopyRule;

        public OdgOdpCopyExecutor(TagPathDescr mainCopyRule) {
            this.mainCopyRule = mainCopyRule;
        }

        @Override
        public void copy(AbstractOdfContainer<?> source, Node targetRefInsertAfter) {
            Node insertAfterElement = Nodes.findAncestorChild(targetRefInsertAfter, DRAW_PAGE).get();
            Document targetOwnerDocument = targetRefInsertAfter.getOwnerDocument();
            Node siblingRef = insertAfterElement.getNextSibling();
            Node targetParent = insertAfterElement.getParentNode();
            List<Node> allToCopy = mainCopyRule.findAll(source);
            allToCopy.forEach(toCopy -> {
                Node clone = targetOwnerDocument.importNode(toCopy, true);
                if (siblingRef != null) {
                    targetParent.insertBefore(clone, siblingRef);
                } else {
                    targetParent.appendChild(clone);
                }
            });
        }
    }


}
