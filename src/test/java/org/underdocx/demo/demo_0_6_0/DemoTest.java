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

package org.underdocx.demo.demo_0_6_0;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.model.DataNode;
import org.underdocx.enginelayers.modelengine.model.simple.DataTreeBuilder;
import org.underdocx.environment.UnderdocxEnv;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DemoTest extends AbstractOdtTest {

    @Test
    public void testDemoDoc() throws Exception {
        InputStream is = getInputStream("template.odt");
        OutputStream os = new FileOutputStream(createFileInTempDir("demo0.6.0out.odt"));

        // Prepare document and engine
        OdtContainer doc = new OdtContainer(is);
        OdtEngine engine = new OdtEngine(doc);

        // Alias placeholders
        engine.registerStringReplacement("addHeaderAndFooter", "${Export $resource:\"master\"} ");
        engine.registerStringReplacement("membersTable", "${Import $resource:\"membersTable\"} ");

        // Variables / Data
        engine.pushLeafVariable("membersTable", readResource("membertable.odt"));
        engine.pushLeafVariable("master", readResource("master.odt"));
        engine.pushLeafVariable("signatureImage", readResource("signature.png"));
        engine.pushVariable("persons", createPersonsData());
        engine.pushVariable("address", "Mr. Peter Silie\nKochstrasse 42\n38106 Braunschweig");
        engine.pushVariable("contact", "Mr. Silie");
        engine.pushVariable("signature", "Jon Sutton");

        // Execute the engine
        engine.run();
        doc.save(os);
        //show(doc);


        if (UnderdocxEnv.isLibreOfficeInstalled()) {
            OutputStream pos = new FileOutputStream(createFileInTempDir("demo0.6.0out.pdf"));
            doc.writePDF(pos);
        }

        assertNoPlaceholders(doc);
    }

    private DataNode createPersonsData() throws IOException {
        DataNode data = DataTreeBuilder
                /*  */.beginList()

                /*    */.beginMap()
                /*      */.add("firstName", "Hans")
                /*      */.add("lastName", "Müller")
                /*      */.add("birthDate", "1975-02-01")
                /*      */.add("imageTitle", "Hans")
                /*      */.add("imageName", "hans.png")
                /*      */.addObj("imageResource", readResource("image1.png"))
                /*    */.end()

                /*    */.beginMap()
                /*      */.add("firstName", "Helene")
                /*      */.add("lastName", "Schäfer")
                /*      */.add("birthDate", "2009-05-03")
                /*      */.add("imageTitle", "Helene")
                /*      */.add("imageName", "helene.png")
                /*      */.addObj("imageResource", readResource("image2.png"))
                /*    */.end()

                /*    */.beginMap()
                /*      */.add("firstName", "Lea")
                /*      */.add("lastName", "Fischer")
                /*      */.add("birthDate", "1999-12-03")
                /*      */.add("imageTitle", "Lea")
                /*      */.add("imageName", "lea.png")
                /*      */.addObj("imageResource", readResource("image3.png"))
                /*    */.end()

                /*  */.end()
                /*  */.build();

        return data;
    }

}
