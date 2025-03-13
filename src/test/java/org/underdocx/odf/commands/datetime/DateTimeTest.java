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

package org.underdocx.odf.commands.datetime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTest extends AbstractOdtTest {

    @Test
    public void testDate() {
        String content = """
                A ${Date}
                B ${Date outputFormat:"dd.MM.yyyy"}
                C ${Date value:"2022-03-04",  outputFormat:"dd.MM.yyyy"}
                D ${Date value:"04.03.2022",  inputFormat:"dd.MM.yyyy", outputFormat:"dd.MM.yyyy"}                
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
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
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
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

    private LocalDateTime parseTime(String pattern, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime result = LocalDateTime.parse(time, formatter);
        String reString = DateTimeFormatter.ofPattern(pattern).format(result);
        Assertions.assertThat(reString).isEqualTo(time);
        return result;
    }

    private void testReformat(String inPattern, String outPattern, String inTime, String expOutTime) {
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inPattern);
        LocalDateTime result = LocalDateTime.parse(inTime, inFormatter);
        String reString = DateTimeFormatter.ofPattern(outPattern).format(result);
        Assertions.assertThat(reString).isEqualTo(expOutTime);

    }

    @Test
    public void testDateTimeFormatter() {
        parseTime("yyyy-MM-dd HH:mm", "1999-12-02 12:30");
        parseTime("yyyy-MM-dd HH:mm:ss", "1999-12-02 12:30:42");
        // parseTime("HH:mm:ss", "12:30:42");
        // parseTime("yyyy-MM-dd", "1999-12-02");

        testReformat("yyyy-MM-dd HH:mm", "HH:mm", "1999-12-02 12:30", "12:30");
    }


}
