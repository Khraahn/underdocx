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

package de.underdocx.demo.demo_0_6_0;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.environment.UnderdocxEnv;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DemoTest extends AbstractOdtTest {

    @Test
    public void testDemoDoc() throws Exception {
        InputStream is = getResource("demo.odt");
        byte[] fragment = readData("header.odt");
        OutputStream os = new FileOutputStream(createFileInTempDir("demo0.6.0out.odt"));
        String uri1 = createTmpUri(getResource("image1.png"), ".png");
        String uri2 = createTmpUri(getResource("image2.png"), ".png");
        String uri3 = createTmpUri(getResource("image3.png"), ".png");

        String persons = """
                [
                    {firstName: "Hans", lastName:"Müller", birthDate:"1975-10-02", member:false, imageTitle: "Hans", imageName:"hans.png", imageUri:"%s"},
                    {firstName: "Helene", lastName:"Schäfer", birthDate:"2013-02-02", member:true, imageTitle: "Helene", imageName:"helene.png", imageUri:"%s"},
                    {firstName: "Lea", lastName:"Fischer", birthDate:"1999-08-12", member:false, imageTitle: "Lea", imageName:"lea.png", imageUri:"%s"}
                ]
                """.formatted(uri1, uri2, uri3);

        OdtContainer doc = new OdtContainer(is);
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.pushLeafVariable("header", fragment);
        engine.pushJsonVariable("persons", persons);

        engine.run();
        //show(doc);
        doc.save(os);

        if (UnderdocxEnv.isLibreOfficeInstalled()) {
            OutputStream pos = new FileOutputStream(createFileInTempDir("demo0.6.0out.pdf"));
            doc.writePDF(pos);
        }

        assertNoPlaceholders(doc);
    }

}
