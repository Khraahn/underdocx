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

public class IfTest extends AbstractTxtTest {
    @Test
    public void testSingleIfSimpleCondition() {
        String documentStr = """
                P ${If $testTrue:false} INVALID ${EndIf}P
                Test
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.run(doc);
        // show(doc);
        assertContains(doc, "P P");

        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }


    @Test
    public void testIfSimpleConditions() {
        String jsonString = """
                {
                    model:"model"
                }
                """;
        String documentStr = """
                ${Push key:"testString", value:"Test"}
                ${Push key:"testTrue", value:true}
                ${Push key:"testFalse", value:false}
                ${Push key:"testNull", value:null}
                ${Push key:"testNumber", value:42}
                ${Push key:"testEmpty", value:[]}
                A ${If $testString:"Test"}A${EndIf} A
                B ${If $testTrue:true}B${EndIf} B
                C ${If $testFalse:false}C${EndIf} C
                D ${If $testNull:null}D${EndIf} D
                E ${If $testNumber:42}E${EndIf} E
                F ${If $testEmpty:[]}F${EndIf} F
                H ${If not:{$testString:"x"}}H${EndIf} H
                I ${If not:{$testTrue:false}}I${EndIf} I
                J ${If not:{$testFalse:true}}J${EndIf} J
                K ${If not:{$testNull:"test"}}K${EndIf} K
                L ${If not:{$testEmpty:[1]}}L${EndIf} L
                M ${If *model:"model"}M${EndIf} M
                O ${If $testString:"x"} INVALID ${EndIf}O
                P ${If $testTrue:false} INVALID ${EndIf}P
                Q ${If $testFalse:true} INVALID ${EndIf}Q
                R ${If $testNull:"test"} INVALID ${EndIf}R
                S ${If $testNumber:0} INVALID ${EndIf}S
                T ${If $testEmpty:[1]} INVALID ${EndIf}T
                U ${If *model:"x"} INVALID ${EndIf}U
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.setModel(new MapDataNode(jsonString));
        engine.run(doc);
        //show(doc);
        assertContains(doc, "A A A");
        assertContains(doc, "B B B");
        assertContains(doc, "C C C");
        assertContains(doc, "D D D");
        assertContains(doc, "E E E");
        assertContains(doc, "F F F");
        assertContains(doc, "H H H");
        assertContains(doc, "I I I");
        assertContains(doc, "J J J");
        assertContains(doc, "K K K");
        assertContains(doc, "L L L");
        assertContains(doc, "M M M");
        assertContains(doc, "O O");
        assertContains(doc, "P P");
        assertContains(doc, "Q Q");
        assertContains(doc, "R R");
        assertContains(doc, "S S");
        assertContains(doc, "T T");
        assertContains(doc, "U U");

        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testIfParagraph() {
        String documentStr = """
                ${Push key:"testTrue", value:true}
                A
                ${If $testTrue:false}
                X
                ${EndIf}
                B
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.run(doc);
        //show(doc);
        System.out.println(doc.getDocument().getXmlText());
        assertNoPlaceholders(doc);
        assertNotContains(doc, "X");
        assertParagraphsCount(doc, 2);
    }


    @Test
    public void testAnd() {
        String documentStr = """
                ${Push key:"testString", value:"Test"}
                ${Push key:"testTrue", value:true}
                ${If and:[{$testString:"Test"},{$testTrue:true}]} OK ${EndIf}
                ${If and:[{$testString:"Test"},{not:{$testTrue:true}}]} INVALID ${EndIf}
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.run(doc);
        // show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
        assertParagraphsCount(doc, 1);
    }

    @Test
    public void testOr() {
        String documentStr = """
                ${Push key:"testString", value:"Test"}
                ${Push key:"testTrue", value:true}
                ${If or:[{$testString:"Test"},{$testTrue:false}]} OK ${EndIf}
                ${If or:[{$testString:"x"},{$testTrue:false}]} INVALID ${EndIf}
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testIfSimpleConditions2() {
        String documentStr = """
                        ${Push key:"testTrue", value:true}
                        B ${If $testTrue:true}B${EndIf} B
                """;
        TxtContainer doc = new TxtContainer(documentStr);
        TxtEngine engine = new TxtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "B B B");

        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    void testTwoReferences() {
        TxtContainer content = new TxtContainer("""
                ${Push key:"varTrue1", value:true}
                ${Push key:"varTrue2", value:true}
                ${Push key:"varFalse", value:false}
                ${Push key:"varX1", value:"X"}
                ${Push key:"varX2", value:"X"}
                ${Push key:"varY", value:"Y"}
                                
                ${If "$varX1":"$varY"} INVALID ${EndIf}
                ${If "$varTrue1":"$varFalse"} INVALID ${EndIf}
                ${If "$varX1":"$varX2"} Hello ${EndIf}
                ${If "$varTrue1":"$varTrue2"} World ${EndIf}
                """);
        new TxtEngine().run(content);
        // show(content);
        assertNoPlaceholders(content);
        assertNotContains(content, "INVALID");
        assertContains(content, "Hello");
        assertContains(content, "World");
    }
}
