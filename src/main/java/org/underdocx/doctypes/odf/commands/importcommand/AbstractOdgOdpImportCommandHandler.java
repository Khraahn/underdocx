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

package org.underdocx.doctypes.odf.commands.importcommand;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.modifiers.importmodifier.ImportType;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

public abstract class AbstractOdgOdpImportCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractOdfImportCommandHandler<C, D> {

    private static final PredefinedDataPicker<String> toPageNameAttr = new StringConvertDataPicker().asPredefined("toPage");
    private static final PredefinedDataPicker<String> pageNameAttr = new StringConvertDataPicker().asPredefined("page");

    public AbstractOdgOdpImportCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected Node getRefNode() {
        String toPageName = toPageNameAttr.getData(dataAccess, placeholderData.getJson()).orElse(null);
        if (toPageName != null) {
            return Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.findMainPage(selection.getDocContainer(), toPageName), toPageName);
        } else {
            return selection.getNode();
        }
    }

    @Override
    protected String getPageName() {
        return pageNameAttr.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
    }

    @Override
    protected ImportType getImportType() {
        return getPageName() == null ? ImportType.COPY_PAGES : ImportType.COPY_PAGE;
    }
}
