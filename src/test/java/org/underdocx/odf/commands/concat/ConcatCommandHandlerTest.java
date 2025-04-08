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

package org.underdocx.odf.commands.concat;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class ConcatCommandHandlerTest extends AbstractOdtTest {

    @Test
    public void testConcatToString() {
        String content = """
                ${Push key:"i", value:2}
                ${Concat a:"A[", $b:"i", c:"]", type:"string", key:"result"}
                ${$result}                
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "A[2]");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testConcatToList() {
        String content = """
                ${Push key:"i", value:2}
                ${Concat a:"A", $b:"i", c:"B", type:"list", key:"result"}
                ${Join $value:"result"}          
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "A, 2, B");
        assertNoPlaceholders(doc);
    }

}
