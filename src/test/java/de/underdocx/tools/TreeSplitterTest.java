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

package de.underdocx.tools;

import de.underdocx.AbstractOdtTest;
import de.underdocx.tools.tree.Nodes;
import de.underdocx.tools.tree.TreeSplitter;
import de.underdocx.tools.tree.TreeWalker;
import de.underdocx.tools.tree.nodepath.TreeNodeCollector;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TreeSplitterTest extends AbstractOdtTest {

    @Test
    public void testSplitter() {
        String xmlStr = """
                <root>
                    <p>
                        don't change this
                    </p>
                    <p>
                        Left
                        <span>
                            placeholder
                        </span>
                        <span>
                            Right
                        </span>
                    </p>
                    <p>
                        <span>
                            also don't change this
                        </span>
                    </p> 
                </root>
                    """;

        String expectationXMLStr = """
                <root>
                    <p>
                        don't change this
                    </p>
                    <p>
                        Left
                    </p>
                    <p>
                        <span>
                            placeholder
                        </span>
                    </p>
                    <p>
                        <span>
                            Right
                        </span>
                    </p>
                    <p>
                        <span>
                            also don't change this
                        </span>
                    </p> 
                </root>
                """;
        Document initialXML = readXML(xmlStr);

        Node placeholderSpan = Nodes.findFirstDescendantNode(initialXML, check ->
                check.getNodeType() == Node.TEXT_NODE && check.getNodeValue().contains("placeholder")
        ).get().getParentNode();
        Node commonAncestor = Nodes.findOldestAncestorNode(placeholderSpan, check ->
                check.getNodeName().equals("root")
        ).get();
        TreeSplitter.split(placeholderSpan, commonAncestor, testTextNodeInterpreter);

        Document expectXML = readXML(expectationXMLStr);
        Predicate<TreeWalker.VisitState> filter = visitState ->
                visitState.isBeginVisit() && visitState.getNode().getNodeType() != Node.TEXT_NODE;
        List<Node> changedNodes = new TreeNodeCollector(initialXML, initialXML, new ArrayList<>(), filter).collect();
        List<Node> expectedNodes = new TreeNodeCollector(expectXML, expectXML, new ArrayList<>(), filter).collect();

        Assertions.assertThat(changedNodes.size()).isGreaterThan(5);
        Assertions.assertThat(changedNodes.size()).isEqualTo(expectedNodes.size());
        for (int i = 0; i < changedNodes.size(); i++) {
            Assertions.assertThat(changedNodes.get(i).getNodeName()).isEqualTo(expectedNodes.get(i).getNodeName());
        }
    }
}
