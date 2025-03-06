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

package org.underdocx.doctypes.odf.ods;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problems;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class OdsContainer extends AbstractOdfContainer<OdfSpreadsheetDocument> {

    public OdsContainer() {
        super();
    }

    public OdsContainer(InputStream is) throws IOException {
        super(is);
    }

    public OdsContainer(Resource resource) throws IOException {
        super(resource);
    }

    public OdsContainer(byte[] data) throws IOException {
        super(data);
    }

    public OdsContainer(URI uri) throws IOException {
        super(uri);
    }

    public OdsContainer(File file) throws IOException {
        super(file);
    }

    public OdsContainer(OdfSpreadsheetDocument doc) {
        super(doc);
    }

    @Override
    protected OdfSpreadsheetDocument createEmptyDoc() {
        try {
            return OdfSpreadsheetDocument.newSpreadsheetDocument();
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.error(e);
        }
        return null;
    }

    public OfficeSpreadsheetElement getContentRoot() {
        try {
            return getDocument().getContentRoot();
        } catch (Exception e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    @Override
    public String getFileExtension() {
        return "odp";
    }


    @Override
    public void load(InputStream is) throws IOException {
        try {
            setDocument(OdfSpreadsheetDocument.loadDocument(is));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }


    @Override
    public void save(OutputStream os) throws IOException {
        try {
            getDocument().save(os);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void appendText(String text) {
        // not yet implemented
    }
}