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

package org.underdocx.odf.baseengine;

import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.odf.tools.placeholder.OdfPlaceholderStyle;

public class OdfPlaceholderStylesTest extends AbstractOdtTest {

    @Test
    public void testXMLComment() {
        String content = """
                <!--${Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]}--> <!--${Date value:"2022-02-02"}-->
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(new OdfPlaceholderStyle<OdtContainer, OdfTextDocument>().XML_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

    @Test
    public void testCodeComment() {
        String content = """
                /*!Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]!*/ /*!Date value:"2022-02-02"!*/
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(new OdfPlaceholderStyle<OdtContainer, OdfTextDocument>().CODE_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

    @Test
    public void testHashComment() {
        String content = """
                #!Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]!# #!Date value:"2022-02-02"!#
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(new OdfPlaceholderStyle<OdtContainer, OdfTextDocument>().HASH_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

    @Test
    public void testDoubleBrackets() {
        String content = """
                {{Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]}} {{Date value:"2022-02-02"}}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(new OdfPlaceholderStyle<OdtContainer, OdfTextDocument>().DOUBLE_BRACKETS);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }
}
