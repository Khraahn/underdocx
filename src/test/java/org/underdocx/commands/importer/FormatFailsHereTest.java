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

import java.io.IOException;

public class FormatFailsHereTest extends AbstractOdtTest {

    @Test
    public void testFailingFormat() throws IOException {
        OdtContainer doc = new OdtContainer("""
                ${Import $resource:"toImport"}""");
        OdtEngine engine = new OdtEngine(doc);
        engine.pushLeafVariable("toImport", readResource("underdocx_.odt"));
        engine.run();
        //show(doc);

        /**
         * Layout fails imported nodes use "family"-styles from target document. This might
         * change used fonts etc and lead to strange behaviour.
         * To solve the problem, family styles of source document must be transferred into new
         * style nodes, and all nodes using the family must derive from these style nodes.
         */
    }

    @Test
    public void testNotFailingSameDocumentBase() throws IOException {
        OdtContainer doc = new OdtContainer(readResource("underdocxempty.odt"));
        OdtEngine engine = new OdtEngine(doc);
        engine.pushLeafVariable("toImport", readResource("underdocx_.odt"));
        engine.run();
        //show(doc);
    }
}
