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

package org.underdocx.tools;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.SimpleTreeWalker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

public class SimpleTreeWalkerTest extends AbstractOdtTest {

    @Test
    public void testGetAllNodes() {
        String xml = """
                <root>
                    <p>A<span>B<span>C</span></span></p>
                    <p>D<span>E<span>F</span><span>G</span></span></p>
                    <p>H</p>
                </root>
                """;
        Document doc = readXML(xml);
        SimpleTreeWalker walker;
        List<Node> nodes;

        walker = new SimpleTreeWalker(doc, doc, null, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("p")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(3);

        walker = new SimpleTreeWalker(doc, doc, null, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(5);

        walker = new SimpleTreeWalker(doc, doc, null, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(2);
    }

    @Test
    public void testJump() {
        String xml = """
                <root>
                    <p>A<span>B<span>C</span></span></p>
                    <p>D<span>E<span>F</span><span>G</span></span></p>
                    <p>H</p>
                </root>
                """;
        Document doc = readXML(xml);
        SimpleTreeWalker walker;
        List<Node> nodes;
        Node start;

        start = Nodes.findFirstDescendantNode(doc, node -> node.getNodeName().equals("span") && ("" + node.getTextContent()).contains("E")).get();
        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("p")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(1);

        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(3);

        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(1);
    }

    @Test
    public void testJumpDeep() {
        String xml = """
                <root>
                    <p>A<span>B<span>C</span></span></p>
                    <p>D<span>E<span>F</span><span>G</span></span></p>
                    <p>H</p>
                </root>
                """;
        Document doc = readXML(xml);
        SimpleTreeWalker walker;
        List<Node> nodes;
        Node start;

        start = Nodes.findFirstDescendantNode(doc, node -> node.getNodeName().equals("span") && ("" + node.getTextContent()).equals("F")).get();
        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("p")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(1);

        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(2);

        walker = new SimpleTreeWalker(doc, doc, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), true);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(2);
    }

    @Test
    public void testLimit() {
        String xml = """
                <root>
                    <p>A<span>B<span>C</span></span></p>
                    <p>D<span>E<span>F</span><span>G</span></span></p>
                    <p>H</p>
                </root>
                """;
        Document doc = readXML(xml);
        SimpleTreeWalker walker;
        List<Node> nodes;
        Node start;
        Node limit;

        start = Nodes.findFirstDescendantNode(doc, node -> node.getNodeName().equals("span") && ("" + node.getTextContent()).equals("C")).get();
        limit = Nodes.findFirstDescendantNode(doc, node -> node.getNodeName().equals("p") && ("" + node.getTextContent()).contains("A")).get();

        walker = new SimpleTreeWalker(doc, limit, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(1);

        walker = new SimpleTreeWalker(doc, null, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(4);

        walker = new SimpleTreeWalker(start, null, start, SimpleTreeWalker.buildFilter(node -> node.getNodeName().equals("span")), false);
        Node first = walker.inspectNext().get();
        Assertions.assertThat(first).isEqualTo(start);
        nodes = walker.collect();
        Assertions.assertThat(nodes.size()).isEqualTo(4);
    }


}
