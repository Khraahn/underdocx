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

package org.underdocx.odf.demo.demo_0_5_0;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.environment.UnderdocxEnv;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DemoTest extends AbstractOdtTest {

    @Test
    public void testDemoDoc() throws Exception {
        InputStream is = getInputStream("demo.odt");
        OutputStream os = new FileOutputStream(createFileInTempDir("demo0.5.0out.odt"));

        OdtContainer doc = new OdtContainer(is);
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("header", new LeafDataNode<>(readData("header.odt")));

        engine.run(doc);
        //show(doc);
        doc.save(os);

        if (UnderdocxEnv.isLibreOfficeInstalled()) {
            OutputStream pos = new FileOutputStream(createFileInTempDir("demo0.5.0out.pdf"));
            doc.writePDF(pos);
        }

        assertNoPlaceholders(doc);
    }

}
