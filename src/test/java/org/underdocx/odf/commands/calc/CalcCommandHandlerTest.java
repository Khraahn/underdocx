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

package org.underdocx.odf.commands.calc;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class CalcCommandHandlerTest extends AbstractOdtTest {

    @Test
    public void testCalcInt() {
        String content = """
                ${Push key:"i1", value:1}
                ${Push key:"i2", value:2}
                ${Push key:"i4", value:4}
                A ${Calc $a:"i1", $b:"i2", operator:"+", key:"result"}${$result}
                B ${Calc $a:"i4", $b:"i2", operator:"-", key:"result"}${$result}
                C ${Calc $a:"i2", $b:"i4", operator:"*", key:"result"}${$result}
                E ${Calc $a:"i4", $b:"i1", operator:"/", key:"result"}${$result}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertOrder(doc, "A 3", "B 2", "C 8", "E 4");
    }

    @Test
    public void testCalcDouble() {
        String content = """
                ${Push key:"i1", value:1.0}
                ${Push key:"i2", value:2.0}
                ${Push key:"i4", value:4.0}
                A ${Calc $a:"i1", $b:"i2", operator:"+", key:"result"}${$result}
                B ${Calc $a:"i4", $b:"i2", operator:"-", key:"result"}${$result}
                C ${Calc $a:"i2", $b:"i4", operator:"*", key:"result"}${$result}
                E ${Calc $a:"i4", $b:"i1", operator:"/", key:"result"}${$result}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertOrder(doc, "A 3.0", "B 2.0", "C 8.0", "E 4.0");
    }

    @Test
    public void testCalcMixed() {
        String content = """
                ${Push key:"i1", value:1.1}
                ${Push key:"i4", value:4}
                A ${Calc $a:"i1", $b:"i4", operator:"+", key:"result"}${$result}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertOrder(doc, "A 4.1");
    }
}
