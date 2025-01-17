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

package de.underdocx.commands.export;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExportTest extends AbstractOdtTest {

    @Test
    public void testWithPlaceholder() throws IOException {
        OdtContainer targetDoc = new OdtContainer("""
                AAA
                ${ExportTarget name:"target"}
                BBB
                """);
        String targetURI = targetDoc.createURI(10000L);
        OdtContainer sourceDoc = new OdtContainer("""
                ${Export uri:"%s", name:"target"}
                ZZZ
                """.formatted(targetURI));
        DefaultODTEngine engine = new DefaultODTEngine(sourceDoc);
        engine.run();
        //show(sourceDoc);
        assertNoPlaceholders(sourceDoc);
        assertOrder(sourceDoc, "AAA", "ZZZ", "BBB");
    }


    @Test
    public void testWithoutPlaceholder() throws IOException {
        OdtContainer targetDoc = new OdtContainer("""
                AAA
                BBB
                """);
        String targetURI = targetDoc.createURI(10000L);
        OdtContainer sourceDoc = new OdtContainer("""
                ${Export uri:"%s"}
                ZZZ
                """.formatted(targetURI));
        DefaultODTEngine engine = new DefaultODTEngine(sourceDoc);
        engine.run();
        assertNoPlaceholders(sourceDoc);
        assertOrder(sourceDoc, "AAA", "BBB", "ZZZ");
    }
}
