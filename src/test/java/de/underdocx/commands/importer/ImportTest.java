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

package de.underdocx.commands.importer;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ImportTest extends AbstractOdtTest {

    @Test
    public void testImportBinary() throws IOException {
        OdtContainer doc = new OdtContainer("""
                Begin
                ${Import $data:"binaryDocument"}
                End""");
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.pushVariable("binaryDocument", new LeafModelNode<>(readData("Source.odt")));
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "Begin", "Lorem ipsum", "1A", "---", "End");
    }
}
