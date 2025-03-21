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

import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.debug.TreePrinter;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.commands.SimpleReplaceFunctionCommand;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.odf.placeholdersprovider.dollar.OdfSimpleDollarPlaceholderFactory;
import org.underdocx.enginelayers.baseengine.BaseEngine;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseEngineTest extends AbstractOdtTest {

    private static class TestSimpleDollarProviderFactory extends OdfSimpleDollarPlaceholderFactory<OdtContainer, OdfTextDocument> {

        @Override
        public TextNodeInterpreter getTextNodeInterpreter() {
            return testTextNodeInterpreter;
        }
    }

    @Test
    public void testHelloWorldDefaultOdtEngine() {
        OdtContainer doc = new OdtContainer("Hello $name");
        OdtEngine engine = new OdtEngine(doc);
        engine.registerSimpleDollarReplacement("name", "World");
        engine.run();
        assertContains(doc, "Hello World");
        assertNotContains(doc, "$");
    }

    @Test
    public void testHelloWorldBaseEngine() {
        OdtContainer doc = new OdtContainer("Hello $name");
        BaseEngine<OdtContainer, OdfTextDocument> baseEngine = new BaseEngine<>(doc);
        baseEngine.registerCommandHandler(
                new OdfSimpleDollarPlaceholderFactory<OdtContainer, OdfTextDocument>().createProvider(doc),
                new SimpleReplaceFunctionCommand<>(foundString -> Optional.ofNullable(foundString.equals("name") ? "World" : null))
        );
        baseEngine.run();
        assertContains(doc, "Hello World");
        assertNotContains(doc, "$");
    }

    @Test
    public void testSimpleDollarPlaceholderProviderEncapsulated() {
        OdtContainer doc = new OdtContainer("Hello $name");
        PlaceholdersProvider<OdtContainer, String, OdfTextDocument> provider = new OdfSimpleDollarPlaceholderFactory<OdtContainer, OdfTextDocument>().createProvider(doc);
        Node node = provider.getPlaceholders().next();
        assertThat(node.getFirstChild().getNodeValue()).isEqualTo("$name");
    }

    @Test
    public void testSimpleDollarExtractor() {
        Document xml = readXML("<root><p>Hallo $name :-)</p></root>");
        Node paragraph = Nodes.findFirstDescendantNode(xml, "p").get();
        EncapsulatedNodesExtractor extractor = new TestSimpleDollarProviderFactory().getExtractor();
        List<Node> nodes = extractor.extractNodes(paragraph, null);
        assertThat(nodes.get(0).getFirstChild().getNodeValue()).isEqualTo("$name");
        String xmlTree = new TreePrinter(Nodes.findFirstDescendantNode(xml, "root").get(), true, true).toString();
        assertThat(xmlTree).isEqualTo("<root><p>Hallo <span>$name</span> :-)</p></root>");
    }

    @Test
    public void testSimpleImage() throws Exception {
        OdtContainer doc = new OdtContainer(getInputStream("ContainsImagePlaceholder.odt"));
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isPresent());
        String imageURI = createTmpUri(getInputStream("image3.jpg"), "jpg");
        OdtEngine engine = new OdtEngine(doc);
        engine.registerSimpleDollarImageReplacement("image", imageURI, true);
        engine.run();
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isEmpty());
        //show(doc);

    }

    @Test
    public void testSimpleImageAndText() throws Exception {
        OdtContainer doc = new OdtContainer(getInputStream("ContainsImagePlaceholder.odt"));
        doc.getDocument().addText("$name");
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isPresent());
        assertContains(doc, "$name");
        //show(doc);
        String imageURI = createTmpUri(getInputStream("image3.jpg"), "jpg");
        OdtEngine engine = new OdtEngine(doc);
        engine.registerSimpleDollarImageReplacement("image", imageURI, true);
        engine.registerSimpleDollarReplacement("name", "Test");
        engine.run();
        //show(doc);
        assertThat(Nodes.findFirstDescendantNode(doc.getContentDom(), node ->
                (Nodes.attributes(node).getOrDefault("draw:name", "").startsWith("$")
                )).isEmpty());
        assertContains(doc, "Test");
        assertNotContains(doc, "$");
    }

    @Test
    public void testSimpleDollarExtractorNoTextNodeContained() {
        Document xml = readXML("<root><p><x></x>Hallo $name :-)</p></root>");
        Node paragraph = Nodes.findFirstDescendantNode(xml, "p").get();
        EncapsulatedNodesExtractor extractor = new TestSimpleDollarProviderFactory().getExtractor();
        List<Node> nodes = extractor.extractNodes(paragraph, null);
        assertThat(nodes.get(0).getFirstChild().getNodeValue()).isEqualTo("$name");
        String xmlTree = new TreePrinter(Nodes.findFirstDescendantNode(xml, "root").get(), true, true).toString();
        assertThat(xmlTree).isEqualTo("<root><p><x></x>Hallo <span>$name</span> :-)</p></root>");
    }


    @Test
    public void testHeaderFooter() throws IOException {
        InputStream is = getInputStream("HeaderFooter.odt");


        OdtContainer doc = new OdtContainer(is);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerSimpleDollarReplacement("header", "A");
        engine.registerSimpleDollarReplacement("footer", "B");
        engine.registerSimpleDollarReplacement("content", "C");
        engine.run();

        assertContains(doc, "A");
        assertContains(doc, "B");
        assertContains(doc, "C");
        assertNotContains(doc, "$");
    }

    @Test
    public void testSoftLineBreakPlaceholder() throws IOException {
        InputStream is = getInputStream("SoftLineBreakPlaceholder.odt");

        OdtContainer doc = new OdtContainer(is);
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        assertContains(doc, "A Test B");
        assertNoPlaceholders(doc);

    }
}
