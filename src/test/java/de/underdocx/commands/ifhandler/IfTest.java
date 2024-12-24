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

package de.underdocx.commands.ifhandler;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import org.junit.jupiter.api.Test;

public class IfTest extends AbstractOdtTest {
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
                M ${If @model:"model"}M${EndIf} M
                O ${If $testString:"x"} INVALID ${EndIf}O
                P ${If $testTrue:false} INVALID ${EndIf}P
                Q ${If $testFalse:true} INVALID ${EndIf}Q 
                R ${If $testNull:"test"} INVALID ${EndIf}R
                S ${If $testNumber:0} INVALID ${EndIf}S
                T ${If $testEmpty:[1]} INVALID ${EndIf}T
                U ${If @model:"x"} INVALID ${EndIf}U        
                """;
        OdtContainer doc = new OdtContainer(documentStr);
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.setModel(new MapModelNode(jsonString));
        engine.run();
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.run();
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.run();
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.run();
        //show(doc);
        assertContains(doc, "OK");
        assertNotContains(doc, "INVALID");
        assertNoPlaceholders(doc);
    }

}
