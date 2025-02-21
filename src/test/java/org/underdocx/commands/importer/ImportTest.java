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

package org.underdocx.commands.importer;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.model.simple.LeafDataNode;

import java.io.File;
import java.io.IOException;

public class ImportTest extends AbstractOdtTest {

    @Test
    public void testImportBinary() throws IOException {
        OdtContainer doc = new OdtContainer("""
                Begin
                ${Import $data:"binaryDocument"}
                End""");
        OdtEngine engine = new OdtEngine(doc);
        engine.pushVariable("binaryDocument", new LeafDataNode<>(readData("Source.odt")));
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "Begin", "Lorem ipsum", "1A", "---", "End");
    }

    @Test
    public void testImportUri() throws IOException {
        OdtContainer source = readOdt("Source.odt");
        File tmpFile = source.createTmpFile("test_", true, 20000L);
        String uri = tmpFile.toURI().toString();
        OdtContainer doc = new OdtContainer("" +
                "Begin                        \n" +
                "${Import uri:\"" + uri + "\"}  \n" +
                "End");
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "Begin", "Lorem ipsum", "1A", "---", "End");
    }

    @Test
    public void testImportResource() throws IOException {
        OdtContainer doc = new OdtContainer("""
                Begin
                ${Import $resource:"resourceDoc"}
                End""");
        OdtEngine engine = new OdtEngine(doc);
        engine.pushVariable("resourceDoc", new LeafDataNode<>(readResource("Source.odt")));
        engine.run();
        // show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "Begin", "Lorem ipsum", "1A", "---", "End");
    }
}
