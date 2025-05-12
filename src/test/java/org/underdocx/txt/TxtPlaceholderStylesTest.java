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

package org.underdocx.txt;

import org.junit.jupiter.api.Test;
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.TxtEngine;
import org.underdocx.doctypes.txt.placeholders.TxtPlaceholderStyle;
import org.underdocx.enginelayers.modelengine.data.simple.DataTreeBuilder;

public class TxtPlaceholderStylesTest extends AbstractTxtTest {

    @Test
    public void testXMLComment() {
        String content = """
                <!--${Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]}--> <!--${Date value:"2022-02-02"}-->
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.XML_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

    @Test
    public void testCodeComment() {
        String content = """
                /*!Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]!*/ /*!Date value:"2022-02-02"!*/
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.CODE_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

    @Test
    public void testHashComment() {
        String content = """
                #!Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]!# #!Date value:"2022-02-02"!#
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.HASH_COMMENT);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }


    @Test
    void testXMLTemplate() {
        String content = """
                <html><body>
                  <ul>
                    <!--${For value:["Hans", "Otto", "Gerda"], $as:"item"}-->
                      <li><!--${$item}--></li>
                    <!--${EndFor}-->
                  </ul>
                </body></html>
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.XML_COMMENT);
        engine.run(doc);
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "Hans", "Otto", "Gerda");
    }

    @Test
    void testJavaTemplate() {
        String content = """                
                public class Test {
                    /*!For $value:"entries", $as:"item"!*/
                    /*!$item!*/
                    /*!EndFor!*/
                }
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.CODE_COMMENT);
        engine.pushVariable("entries", DataTreeBuilder
                .beginList()
                .add("public static final String x1 = \"X\";")
                .add("public static final String x2 = \"Y\";")
                .end().build());
        engine.run(doc);
        //show(doc);
        assertNoPlaceholders(doc);
    }

    @Test
    void testDoubleBrackets() {
        String content = """
                {{Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]}} {{Date value:"2022-02-02"}}
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(TxtPlaceholderStyle.DOUBLE_BRACKETS);
        engine.run(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Bibi, Tina, Amadeus, Sabrina 2022-02-02");
    }

}
