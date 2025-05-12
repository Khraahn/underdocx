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

public class TxtStringAndModelTest extends AbstractTxtTest {


    @Test
    public void testModelString() {
        String jsonString = """
                {
                  "a":{
                    "b":["Test0", "Test1"]
                  },
                  "c":{
                    "d":["Test2", "Test3"]
                  }
                }
                """;
        String documentStr = """
                A ${String *value:"a.b[0]"} A
                ${Model value:"a.b[0]"}
                B ${String} B
                C ${String *value:"^c.d[1]"} C
                D ${String *value:"^no"} D
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        //show(doc);
        assertContains(doc, "A Test0 A");
        assertContains(doc, "B Test0 B");
        assertContains(doc, "C Test3 C");
        assertContains(doc, "D  D");
        assertNoPlaceholders(doc);
    }


    @Test
    public void testVariablesString() {
        String jsonString = """
                {
                  "a":{
                    "b":["Test0", "Test1"]
                  },
                  "c":{
                    "d":["Test2", "Test3"]
                  },
                  varName: "y"
                }
                """;
        String documentStr = "" +
                "${Push key:\"x\", value:\"attX1\"}     \n" +
                "A ${String $value:\"x\"} A             \n" + // A attX1 A

                "${Push key:\"x\", *value:\"a.b[0]\"}   \n" +
                "B ${String $value:\"x\"} B             \n" + // B Test0 B

                "${Pop key:\"x\"}                       \n" +
                "C ${String $value:\"x\"} C             \n" + // C attX1 C

                "${Pop key:\"x\"}                       \n" +
                "D ${String $value:\"x\"} D             \n" + // D  D

                "${Push *key:\"varName\", value:42}     \n" +
                "E ${String $value:\"y\"} E             \n" + // E 42 E

                "";
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        assertContains(doc, "A attX1 A");
        assertContains(doc, "B Test0 B");
        assertContains(doc, "C attX1 C");
        assertContains(doc, "D  D");
        assertContains(doc, "E 42 E");
        assertNoPlaceholders(doc);
    }
}
