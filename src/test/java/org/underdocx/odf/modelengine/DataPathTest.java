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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;
import org.underdocx.enginelayers.modelengine.datapath.ActivePrefixDataPath;
import org.underdocx.enginelayers.modelengine.datapath.DataPath;

import static org.assertj.core.api.Assertions.assertThat;

public class DataPathTest extends AbstractOdtTest {

    @Test
    public void testModelPath() {
        String jsonStr = """
                {
                  "a": {
                    "b": [
                      ["c", "d"],
                      ["e", "f"]
                    ]
                  },
                  "g": "h"
                }
                """;
        DataNode<?> model = new MapDataNode(jsonStr);
        String path = "a.b[1][0]";
        assertThat(DataPath.interpret(path, model).get().getValue()).isEqualTo("e");
    }

    @Test
    public void testModelPathBack() {
        String jsonStr = """
                {
                  "a": {
                    "b": [
                      ["c", "d"],
                      ["e", "f"]
                    ],
                    "x": "Test"
                  },
                  "g": "h"
                }
                """;
        DataNode<?> model = new MapDataNode(jsonStr);
        String path = "a.b[1][0]<<<x";
        assertThat(DataPath.interpret(path, model).get().getValue()).isEqualTo("Test");
    }

    @Test
    public void testModelPathRoot() {
        String jsonStr = """
                {
                  "a": {
                    "b": [
                      ["c", "d"],
                      ["e", "f"]
                    ],
                    "x": "Test"
                  },
                  "g": "h"
                }
                """;
        DataNode<?> model = new MapDataNode(jsonStr);
        String path = "a.b[1][0]^g";
        assertThat(DataPath.interpret(path, model).get().getValue()).isEqualTo("h");
    }

    @Test
    public void testActivePrefixModelPath() {
        Wrapper<String> prefixSupplier = new Wrapper<>("x");
        DataPath path = new ActivePrefixDataPath(prefixSupplier, "a.b");

        DataPath testPath = path.clone();
        testPath.interpret("x.c");
        Assertions.assertThat(testPath.toString()).isEqualTo("a.b.c");

        prefixSupplier.value = null;
        testPath = path.clone();
        testPath.interpret("x.c");
        Assertions.assertThat(testPath.toString()).isEqualTo("a.b.x.c");

        prefixSupplier.value = "x";
        testPath = path.clone();
        testPath.interpret("<d");
        Assertions.assertThat(testPath.toString()).isEqualTo("a.d");
    }

    @Test
    public void testSetModelPrefix() {
        String jsonString = """
                {
                  aList:["A", "B", "C"]
                }
                """;
        String documentStr = """
                ${Model value:"aList", activeModelPathPrefix:"element" }
                ${String *value:"element[1]"}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        assertContains(doc, "B");
        assertNoPlaceholders(doc);
    }

}
