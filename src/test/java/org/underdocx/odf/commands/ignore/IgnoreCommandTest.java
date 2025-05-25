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

package org.underdocx.odf.commands.ignore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.environment.err.Problem;

import java.util.Optional;

public class IgnoreCommandTest extends AbstractOdtTest {

    @Test
    public void testIgnore() {
        String content = """
                ${String value: "Hallo"}
                ${Ignore}
                ${String $value:"x"}
                ${EndIgnore}
                ${String value:"Welt"}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("x", "INVALID");
        Optional<Problem> problems = engine.run(doc);
        //show(doc);
        Assertions.assertThat(problems).isEmpty();
        assertContains(doc, "$value:\"x\"");
        assertNotContains(doc, "$value:\"Welt\"");
        assertOrder(doc, "Hallo", "Welt");
    }

    @Test
    public void testExit() {
        String content = """
                ${String value: "Hallo"}
                ${Exit}
                ${String $value:"x"}
                ${EndIgnore}
                ${String value:"Welt"}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("x", "INVALID");
        Optional<Problem> problems = engine.run(doc);
        //show(doc);
        Assertions.assertThat(problems).isEmpty();
        assertContains(doc, "$value:\"x\"");
        assertContains(doc, "value:\"Welt\"");
        assertNotContains(doc, "Exit");
        assertOrder(doc, "Hallo", "Welt");
    }

    @Test
    public void testAliasAfterIgnore() {
        String content = """
                ${Alias key:"x", replaceKey:"Date"}
                ${Alias key:"y", replaceKey:"Date"}
                ${Ignore}
                ${x}
                ${EndIgnore}
                ${y}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "${x}");
        assertNotContains(doc, "${y}");
    }

    @Test
    public void testReplaceAfterIgnore() {
        String content = """
                ${Replace key:"x", value:"${Date}"}
                ${Replace key:"y", value:"${Date}"}
                ${Ignore}
                ${x}
                ${EndIgnore}
                ${y}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        assertContains(doc, "${x}");
        assertNotContains(doc, "${y}");
    }
}
