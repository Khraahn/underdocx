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

package org.underdocx.odf.modelengine;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;

public class ModelVariableTest extends AbstractOdtTest {

    @Test
    public void testSetVariableWithSubModel() {
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
        String documentStr = "" +
                "${Push key:\"x\", *value:\"a\"}   \n" +
                "${Push key:\"y\", $value:\"x.b[0]\"}   \n" +
                "B ${$y} B                              \n" + // B Test0 B
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        assertContains(doc, "B Test0 B");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testCopyModelToVar() {
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
        String documentStr = "" +
                "${Push key:\"model\", *value:\"\"}   \n" +
                "B ${$model.a.b[0]} B                 \n" + // B Test0 B
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        assertContains(doc, "B Test0 B");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testVariablesOnly() {
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
        String documentStr = "" +
                "B ${$model.a.b[0]} B                 \n" + // B Test0 B
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("model", new MapDataNode(jsonString));
        engine.run(doc);
        assertContains(doc, "B Test0 B");
        assertNoPlaceholders(doc);
    }
}
