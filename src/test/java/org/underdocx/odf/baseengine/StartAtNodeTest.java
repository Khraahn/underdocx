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

package org.underdocx.odf.baseengine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class StartAtNodeTest extends AbstractOdtTest {

    private class IncVarPlaceholder extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
        protected IncVarPlaceholder() {
            super("IncVar");
        }

        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            int counter = (Integer) (selection.getDataAccess().get().getVariable("testcounter").orElse(new LeafDataNode<>(0))).getValue();
            counter++;
            selection.getDataAccess().get().pushVariable("testcounter", new LeafDataNode<>(counter));
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }

    private class ReplaceWithIncVarAndProceesAtNextNode extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
        private int replaceCounter = 0;

        protected ReplaceWithIncVarAndProceesAtNextNode() {
            super("ReplaceWithIncVar");
        }

        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            selection.getNode().setTextContent("${IncVar id:" + replaceCounter + "}");
            replaceCounter++;
            return CommandHandlerResult.FACTORY.startAtNextNode(selection.getNode());
        }
    }

    @Test
    public void testStartAt() {
        String docContent = """
                Test ${IncVar id:"origin"}
                ${ReplaceWithIncVar id:"a"} a ${ReplaceWithIncVar id:"b"} b ${ReplaceWithIncVar id:"c"} c
                Result: ${$testcounter}
                """;
        OdtContainer doc = new OdtContainer(docContent);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(new IncVarPlaceholder());
        engine.registerParametersCommandHandler(new ReplaceWithIncVarAndProceesAtNextNode());
        engine.run();
        //show(doc);
        assertNotContains(doc, "ReplaceWithIncVar");
        assertContains(doc, "IncVar");
        assertContains(doc, "Result: 1");
    }

    @Test
    public void testDeleteStaticPlaceholderAtEndOfDocumentWhenStartNodePlaceholdersAreUsed1() {
        String docContent = """
                ${Model value:"^"}
                Test ${IncVar id:"origin"}
                ${ReplaceWithIncVar id:"a"} a ${ReplaceWithIncVar id:"b"} b ${ReplaceWithIncVar id:"c"} c
                Result: ${$testcounter}
                """;
        OdtContainer doc = new OdtContainer(docContent);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(new IncVarPlaceholder());
        engine.registerParametersCommandHandler(new ReplaceWithIncVarAndProceesAtNextNode());
        engine.run();
        //show(doc);
        assertNotContains(doc, "ReplaceWithIncVar");
        assertNotContains(doc, "Model");
        assertContains(doc, "IncVar");
        assertNotContains(doc, "push");
        assertContains(doc, "Result: 1");
    }


    @Test
    public void testDeleteStaticPlaceholderAtEndOfDocumentWhenStartNodePlaceholdersAreUsed2() {
        String docContent = """
                ${Push key:"var", value:true}
                ${Model value:"^"}
                Test ${IncVar id:"origin"}
                ${ReplaceWithIncVar id:"a"} a ${ReplaceWithIncVar id:"b"} b ${ReplaceWithIncVar id:"c"} c
                ${If $var:true}visible${EndIf comment:"first"}
                ${If $var:false}invisible${EndIf comment:"second"}
                Result: ${$testcounter}
                """;
        OdtContainer doc = new OdtContainer(docContent);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(new IncVarPlaceholder());
        engine.registerParametersCommandHandler(new ReplaceWithIncVarAndProceesAtNextNode());
        engine.run();
        //show(doc);
        assertNotContains(doc, "ReplaceWithIncVar");
        assertNotContains(doc, "Model");
        assertContains(doc, "IncVar");
        assertContains(doc, "visible");
        assertNotContains(doc, "invisible");
        assertNotContains(doc, "Push");
        assertContains(doc, "Result: 1");
    }


    @Test
    public void testValidNodeTreeWalker() {
        String xmlContent = """
                <root>
                    <a>
                        <b></b>
                        <c></c>
                        <d></d>
                    </a>
                    <e>
                        <f></f>
                    </e>
                </root>
                """.replaceAll("\\s+", "");
        Document document = readXML(xmlContent);
        Node c = Nodes.findFirstDescendantNode(document, "c").get();
        StringBuilder buffer = new StringBuilder();
        new TreeNodeCollector(document, document, c, new ArrayList<>()).collect().forEach(node -> buffer.append(node.getNodeName()));
        Assertions.assertThat(buffer.toString()).isEqualTo("cdef");
    }
}
