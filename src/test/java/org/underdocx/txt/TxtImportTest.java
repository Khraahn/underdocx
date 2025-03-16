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

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TxtImportTest extends AbstractTxtTest {

    @Test
    public void importTest() {

        String toImport = """
                BBBBBBB
                CCCCCCC
                """;
        toImport = Base64.getEncoder().encodeToString(toImport.getBytes(StandardCharsets.UTF_8));
        TxtContainer doc = new TxtContainer("""
                A
                ${Import $base64:"toImport"}
                D
                """
        );
        TxtEngine engine = new TxtEngine(doc);
        engine.pushLeafVariable("toImport", toImport);
        engine.run();
        //show(doc);
        assertOrder(doc, "A", "BBBBBBB", "CCCCCCC", "D");
        assertNoPlaceholders(doc);
    }

    @Test
    public void importTestFragment() {

        String toImport = """
                XXX
                  begin
                BBBBBBB
                CCCCCCC
                  end
                XXX
                """;
        toImport = Base64.getEncoder().encodeToString(toImport.getBytes(StandardCharsets.UTF_8));
        TxtContainer doc = new TxtContainer("""
                A
                ${Import $base64:"toImport", beginFragmentRegex:"begin", endFragmentRegex:"end"}
                D
                """
        );
        TxtEngine engine = new TxtEngine(doc);
        engine.pushLeafVariable("toImport", toImport);
        engine.run();
        //show(doc);
        assertOrder(doc, "A", "BBBBBBB", "CCCCCCC", "D");
        assertNoPlaceholders(doc);
        assertNotContains(doc, "XXX");
    }
}
