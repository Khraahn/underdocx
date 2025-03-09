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

package org.underdocx.commands.alias;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AliasTest extends AbstractOdtTest {

    @Test
    public void testAlias() {
        String content = """
                ${Alias key:"myDate", replaceKey:"Date", attributes:{outputformat:"dd.MM.yyyy"}, attrReplacements: {of:"outputformat", if:"inputformat", v:"value"} }
                ${Push key:"varFormat", value:"yyyy/MM/dd"}
                A: ${myDate}
                B: ${myDate $of:"varFormat"}
                C: ${myDate v:"2024-12-12"}
                D: ${Date}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
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
    public void testAliasAPI() {
        String content = """
                A: ${myDate}
                B: ${myDate $of:"varFormat"}
                C: ${myDate v:"2024-12-12"}
                D: ${Date}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerAlias("myDate", "${Date outputformat:\"dd.MM.yyyy\"}", new Pair<>("v", "value"), new Pair<>("of", "outputformat"));
        engine.pushLeafVariable("varFormat", "yyyy/MM/dd");
        engine.run();
        String ddmmyyyy = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        String yyyymmdd = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String defaultDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        assertNoPlaceholders(doc);
        assertContains(doc, "A: " + ddmmyyyy);
        assertContains(doc, "B: " + yyyymmdd);
        assertContains(doc, "C: 12.12.2024");
        assertContains(doc, "D: " + defaultDate);
    }
}
