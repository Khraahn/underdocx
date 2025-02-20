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

package org.underdocx.commands.forloop;

import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class ForTest extends AbstractOdtTest {

    /*
    ${For value:["1", "2", "3"], $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", value:"1"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", value:"2"}
    ${Push key:"index", value:1}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", value:"3"}
    ${Push key:"index", value:2}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */
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
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
        //show(doc);
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }

    /*
    ${For $value:"aList", $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", $value:"aList[0]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", $value:"aList[1]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", $value:"aList[2]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */

    @Test
    public void testV2V() {
        String jsonString = """
                {
                }
                """;
        String documentStr = "" +
                "${For $value:\"aList\", $as:\"element\"}     \n" +
                "${$index} ${$element}                                    \n" +
                "${EndFor}                                               \n";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.pushVariable("aList", Arrays.asList("A", "B", "C"));
        engine.run();
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }


    /*

    ${For *value:"aList", $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", *value:"aList[0]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", *value:"aList[1]"}
    ${Push key:"index", value:1}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", *value:"aList[2]"}
    ${Push key:"index", value:2}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */
    @Test
    public void testM2V() {
        String jsonString = """
                {
                  aList:["A", "B", "C"]
                }
                """;
        String documentStr = "" +
                "${For *value:\"aList\", $as:\"element\"}     \n" +
                "${$index} ${$element}                                    \n" +
                "${EndFor}                                               \n";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }


    /*

    ${For *value:"aList", *as:"element"}
      ${*element}
    ${EndFor}

    =>

    ${Model value:"current.path.aList[0]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:0}
      ${*element}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[1]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:1}
      ${*element}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[2]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:2}
      ${*element}
    ${Pop key:"index"}
    ${Model value:"current.path"}
     */
    @Test
    public void testM2M() {
        String jsonString = """
                {
                  aList:["A", "B", "C"]
                }
                """;
        String documentStr = "" +
                "${For *value:\"aList\", *as:\"element\"}     \n" +
                "${$index} ${*element}                                    \n" +
                "${EndFor}                                               \n";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }


    /*

    ${For *value:"aList"}
      ${*}
    ${EndFor}

    =>

    ${Model value:"current.path.aList[0]"}
    ${Push key:"index", value:0}
      ${*}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[1]"}
    ${Push key:"index", value:1}
      ${*}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[2]"}
    ${Push key:"index", value:2}
      ${*}
    ${Pop key:"index"}
    ${Model value:"current.path"}
     */
    @Test
    public void testM2N() {
        String jsonString = """
                {
                  aList:["A", "B", "C"]
                }
                """;
        String documentStr = "" +
                "${For *value:\"aList\"}     \n" +
                "${$index} ${*}                                    \n" +
                "${EndFor}                                               \n";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
        assertContains(doc, "0 A");
        assertContains(doc, "1 B");
        assertContains(doc, "2 C");
        assertOrder(doc, "A", "B", "C");
        assertNoPlaceholders(doc);
    }
}
