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

package org.underdocx.enginelayers.odtengine.modifiers.importmodifier;

import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.odt.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.odt.pagestyle.PageStyleWriter;
import org.underdocx.doctypes.odf.odt.tools.importer.Importer;
import org.underdocx.doctypes.odf.odt.tools.importer.OdgOdpImportRules;
import org.underdocx.doctypes.odf.odt.tools.importer.OdtImportRules;
import org.underdocx.doctypes.odf.odt.tools.importer.rules.NodeFilter;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.w3c.dom.Node;

import java.util.Optional;

public class ImportModifier {

    public void modify(ImportModifierData data) {
        if (data.filterInitialPageStyle()) {
            filterInitialPageStyle(data.getSourceDoc());
        }
        Optional<String> pageName = data.getSourcePageName();
        if (pageName.isPresent()) {
            new Importer(OdgOdpImportRules.createRules(pageName.get(), NodeFilter.ACCEPT_ALL))
                    .importDoc(data.getSourceIdentifier(), data.getSourceDoc(), data.getTargetDoc(), data.getRefNode());
        } else {
            new Importer(OdtImportRules.DEFAULT)
                    .importDoc(data.getSourceIdentifier(), data.getSourceDoc(), data.getTargetDoc(), data.getRefNode());
        }
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
