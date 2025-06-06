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

package org.underdocx.doctypes.txt;

import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.AbstractDocContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TxtContainer extends AbstractDocContainer<TxtXml> {

    public TxtContainer() {
        doc = createEmptyDoc();
    }

    public TxtContainer(String content) {
        doc = new TxtXml(content);
    }

    public TxtContainer(Resource content) throws IOException {
        super(content);
    }

    public TxtContainer(InputStream is) throws IOException {
        super(is);
    }

    public TxtContainer(byte[] data) throws IOException {
        super(data);
    }


    @Override
    protected TxtXml createEmptyDoc() {
        return new TxtXml();
    }

    @Override
    public void load(InputStream is) throws IOException {
        doc = new TxtXml(is);
    }

    @Override
    public void save(OutputStream os) throws IOException {
        doc.save(os);
    }

    public String getPlainText() {
        return doc.getPlainText();
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    public void appendText(String content) {
        doc.appendText(content);
    }

    public String toString() {
        return getPlainText();
    }
}
