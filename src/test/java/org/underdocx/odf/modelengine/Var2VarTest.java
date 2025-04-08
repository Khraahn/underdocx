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

package org.underdocx.odf.modelengine;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class Var2VarTest extends AbstractOdtTest {

    @Test
    public void testVar2Var() {
        String content = """
                ${Push key:"out", value:"Hello World"}
                ${Push key:"reference", value:"out"}
                ${String $$value:"reference"}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Hello World");
    }
}
