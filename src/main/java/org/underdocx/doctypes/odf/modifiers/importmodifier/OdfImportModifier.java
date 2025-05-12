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

package org.underdocx.doctypes.odf.modifiers.importmodifier;

import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.odf.tools.importer.Importer;
import org.underdocx.doctypes.odf.tools.importer.OdgOdpPagesImportRules;
import org.underdocx.doctypes.odf.tools.importer.OdgOdpSinglePageImportRules;
import org.underdocx.doctypes.odf.tools.importer.OdtImportRules;
import org.underdocx.doctypes.odf.tools.importer.rules.NodeFilter;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyleWriter;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.Optional;

public class OdfImportModifier {

    public void modify(OdfImportModifierData data) {
        if (data.filterInitialPageStyle()) {
            filterInitialPageStyle(data.getSourceDoc());
        }
        Optional<String> pageName = data.getSourcePageName();
        switch (data.getImportType()) {
            case COPY_ODT_CONTENT -> new Importer(OdtImportRules.DEFAULT)
                    .importDoc(data.getSourceIdentifier(), data.getSourceDoc(), data.getTargetDoc(), data.getRefNode());
            case COPY_PAGE ->
                    new Importer(OdgOdpSinglePageImportRules.createRules(pageName.get(), NodeFilter.ACCEPT_ALL))
                            .importDoc(data.getSourceIdentifier(), data.getSourceDoc(), data.getTargetDoc(), data.getRefNode());
            case COPY_PAGES -> {
                String currentMasterPageName = getCurrentMasterPageName(data.getRefNode());
                new Importer(OdgOdpPagesImportRules.createRules(currentMasterPageName))
                        .importDoc(data.getSourceIdentifier(), data.getSourceDoc(), data.getTargetDoc(), data.getRefNode());
            }
        }
    }

    private String getCurrentMasterPageName(Node refNode) {
        if (refNode instanceof DrawPageElement drawPageElement) {
            return drawPageElement.getDrawMasterPageNameAttribute();
        }
        DrawPageElement page = (DrawPageElement) Problems.CANT_FIND_DOM_ELEMENT.get(Nodes.findAscendantNode(refNode, node -> node instanceof DrawPageElement), "parent page");
        return page.getDrawMasterPageNameAttribute();
    }

    private void filterInitialPageStyle(AbstractOdfContainer<?> sourceDoc) {
        Optional<Node> firstStyleableNode = OdfNodes.findFirstParagraphOrTableChild(sourceDoc);
        if (firstStyleableNode.isPresent())
            PageStyleWriter.writePageStyle(firstStyleableNode.get(), new PageStyle(
                    new Wrapper<>(null),
                    new Wrapper<>(null),
                    new Wrapper<>(null),
                    null
            ), true);
    }
}
