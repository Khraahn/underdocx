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

package org.underdocx.odf.commands.multi;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;

public class MultiCommandTest extends AbstractOdtTest {

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
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
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
