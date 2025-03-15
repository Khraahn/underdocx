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
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TxtCommandsTest extends AbstractTxtTest {

    @Test
    public void testA2V() {
        String jsonString = """
                {
                }
                """;
        String documentStr = "" +
                "${For value:[\"A\", \"B\", \"C\"], $as:\"element\"}     \n" +
                "${$index} ${$element}                                    \n" +
                "${EndFor}                                               \n";
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine(doc);
        engine.setModel(new MapDataNode(jsonString));
        engine.run();
        //show(doc);
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }


    @Test
    public void testDate() {
        String content = """
                A ${Date}
                B ${Date outputFormat:"dd.MM.yyyy"}
                C ${Date value:"2022-03-04",  outputFormat:"dd.MM.yyyy"}
                D ${Date value:"04.03.2022",  inputFormat:"dd.MM.yyyy", outputFormat:"dd.MM.yyyy"}                
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
        engine.run();
        //show(doc);
        String ddMMyyyy = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now());
        String yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
        assertNoPlaceholders(doc);
        assertContains(doc, "A " + yyyyMMdd);
        assertContains(doc, "B " + ddMMyyyy);
        assertContains(doc, "C 04.03.2022");
        assertContains(doc, "D 04.03.2022");
    }

    @Test
    public void testTime() {
        String content = """
                A ${Time}
                B ${Time outputFormat:"HH.mm.ss"}
                C ${Time value:"2011-12-02 08:42:11", outputFormat:"HH.mm.ss"}
                D ${Time value:"2011-12-02 08.42.11", inputFormat:"yyyy-MM-dd HH.mm.ss", outputFormat:"HH.mm"}                
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
        engine.run();
        //show(doc);
        String defTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now());
        String oTime = DateTimeFormatter.ofPattern("HH.mm.ss").format(LocalDateTime.now());
        assertNoPlaceholders(doc);
        assertContains(doc, "A " + defTime);
        assertContains(doc, "B " + oTime);
        assertContains(doc, "C 08.42.11");
        assertContains(doc, "D 08.42");
    }

    @Test
    public void testJoin() {
        String content = """
                A: ${Join value:["Bibi", "Tina", "Amadeus", "Sabrina"]}
                B: ${Join value:["Bibi", "Tina", "Amadeus", "Sabrina"], lastSeparator:" und "}
                C: ${Join value:["Bibi", "Tina", "Amadeus", "Sabrina"], limit: 2, truncated:" usw."}
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
        engine.run();
        assertNoPlaceholders(doc);
        assertContains(doc, "A: Bibi, Tina, Amadeus, Sabrina");
        assertContains(doc, "B: Bibi, Tina, Amadeus und Sabrina");
        assertContains(doc, "C: Bibi, Tina usw.");
    }

    @Test
    public void testNumber() {
        String content = """
                A: ${Number value:1234567.89, format:"#,###.##", lang:"de-DE"}
                B: ${Number value:1234567.89, format:"#,###.##", lang:"en-US"}
                C: ${Number value:1234567.89, format:"#,###.#", lang:"de-DE"}
                D: ${Number value:1234567.89, format:"000000000.000", lang:"en-US"}
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
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
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "A: !!-12345678,90€", "B: !!-12345670€", "C: !!12345670,00€");
    }

    @Test
    public void testAlias() {
        String content = """
                ${Alias key:"myDate", replaceKey:"Date", attributes:{outputFormat:"dd.MM.yyyy"}, attrReplacements: {of:"outputFormat", if:"inputFormat", v:"value"} }
                ${Push key:"varFormat", value:"yyyy/MM/dd"}
                A: ${myDate}
                B: ${myDate $of:"varFormat"}
                C: ${myDate v:"2024-12-12"}
                D: ${Date}
                """;
        TxtContainer doc = new TxtContainer(content);
        TxtEngine engine = new TxtEngine(doc);
        engine.run();
        //show(doc);
        String ddmmyyyy = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        String yyyymmdd = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        assertNoPlaceholders(doc);
        assertContains(doc, "A: " + ddmmyyyy);
        assertContains(doc, "B: " + yyyymmdd);
        assertContains(doc, "C: 12.12.2024");
        assertContains(doc, "D: " + defaultDate);
    }

    @Test
    public void testReplaceForWithCustom() {
        String jsonString = """
                {
                }
                """;
        String documentStr = "" +
                "${Begin}                 \n" +
                "${$index} ${$element}    \n" +
                "${End}                   \n";
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine(doc);
        engine.registerStringReplacement("Begin", "${For value:[\"A\", \"B\", \"C\"], $as:\"element\"} ");
        engine.registerStringReplacement("End", "${EndFor}");
        engine.setModel(new MapDataNode(jsonString));
        engine.run();
        // show(doc);
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }
}
