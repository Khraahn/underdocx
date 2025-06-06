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

package org.underdocx.doctypes.odf.odt.commands;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.importcommand.AbstractOdfImportCommandHandler;
import org.underdocx.doctypes.odf.modifiers.importmodifier.ImportType;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.w3c.dom.Node;

import java.io.IOException;

public class OdtImportCommandHandler extends AbstractOdfImportCommandHandler<OdtContainer, OdfTextDocument> {

    public OdtImportCommandHandler(ModifiersProvider<OdtContainer, OdfTextDocument> modifiers) {
        super(modifiers);
    }

    @Override
    protected ImportType getImportType() {
        return ImportType.COPY_ODT_CONTENT;
    }

    @Override
    protected String getPageName() {
        return null;
    }

    @Override
    protected Node getRefNode() {
        return selection.getNode();
    }

    @Override
    protected OdtContainer createContainer(Resource resource) throws IOException {
        return new OdtContainer(resource);
    }

    @Override
    protected OdtContainer createContainer(byte[] data) throws IOException {
        return new OdtContainer(data);
    }
}
