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

package org.underdocx.odf.commands.number;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class NumberTest extends AbstractOdtTest {

    @Test
    public void testNumber() {
        String content = """
                A: ${Number value:1234567.89, format:"#,###.##", lang:"de-DE"}
                B: ${Number value:1234567.89, format:"#,###.##", lang:"en-US"}
                C: ${Number value:1234567.89, format:"#,###.#", lang:"de-DE"}
                D: ${Number value:1234567.89, format:"000000000.000", lang:"en-US"}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "A: 1.234.567,89", "B: 1,234,567.89", "C: 1.234.567,9", "D: 001234567.890");
    }

    @Test
    public void testNumberPrefixSuffixMultiplier() {
        String content = """
                A: ${Number value:1234567.89, format:"#.00", lang:"de-DE", prefix:"!!", suffix:"€", multiplier:-10}
                B: ${Number value:1234567, format:"#", lang:"de-DE", prefix:"!!", suffix:"€", multiplier:-10}
                C: ${Number value:1234567, format:"#.00", lang:"de-DE", prefix:"!!", suffix:"€", multiplier:10.0}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "A: !!-12345678,90€", "B: !!-12345670€", "C: !!12345670,00€");
    }
}
