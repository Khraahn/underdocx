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

package org.underdocx.odf.commands.ifhandler;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;

public class IfTest extends AbstractOdtTest {
    @Test
    public void testSingleIfSimpleCondition() {
        String documentStr = """
                P ${If $testTrue:false} INVALID ${EndIf}P
                Test
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        // show(doc);
        assertContains(doc, "P P");

        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testSingleIfContainsPlaceholders() {
        String documentStr = """
                ${If $testTrue:true}${String value:"Hello World"}${EndIf}
                ${String value:"Test"}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("testTrue", new LeafDataNode<>(true));
        engine.run(doc);
        //show(doc);
        assertContains(doc, "Hello World");
        assertContains(doc, "Test");
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
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
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
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
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
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testOr() {
        String documentStr = """
                ${Push key:"testString", value:"Test"}
                ${Push key:"testTrue", value:true}
                ${If or:[{$testString:"Test"},{$testTrue:false}]} OK ${EndIf}
                ${If or:[{$testString:"x"},{$testTrue:false}]} INVALID ${EndIf}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testLess() {
        String documentStr = """
                ${Push key:"testValue", value:3}
                ${If less:{$testValue:4}} OK ${EndIf}
                ${If less:{$testValue:3}} INVALID ${EndIf}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testGreater() {
        String documentStr = """
                ${Push key:"testValue", value:4}
                ${If greater:{$testValue:3}} OK ${EndIf}
                ${If greater:{$testValue:4}} INVALID ${EndIf}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testLessOrEqual() {
        String documentStr = """
                ${Push key:"testValue", value:3}
                ${If lessOrEqual:{$testValue:3}} OK ${EndIf}
                ${If lessOrEqual:{$testValue:2}} INVALID ${EndIf}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testGreaterOrEqual() {
        String documentStr = """
                ${Push key:"testValue", value:4}
                ${If greaterOrEqual:{$testValue:4}} OK ${EndIf}
                ${If greaterOrEqual:{$testValue:5}} INVALID ${EndIf}
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine();
        engine.run(doc);
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }


}
