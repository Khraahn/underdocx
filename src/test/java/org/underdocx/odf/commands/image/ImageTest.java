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

package org.underdocx.odf.commands.image;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tools.TmpFile;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ImageTest extends AbstractOdtTest {

    @Test
    void testImage() throws IOException {
        byte[] binaryData = readData("smile.png");
        File tmpFile = TmpFile.createTmpFile("Test_", ".png", true, 10000L);
        Files.write(tmpFile.toPath(), binaryData);
        String uri = tmpFile.toURI().toString();

        OdtContainer doc = readOdt("ImageTest.odt");
        OdtEngine engine = new OdtEngine(doc);
        engine.pushVariable("name1", new LeafDataNode<>("Smile1.png"));
        engine.pushVariable("data", new LeafDataNode<>(binaryData));
        engine.pushVariable("name2", new LeafDataNode<>("Smile2.png"));
        engine.pushVariable("uri", new LeafDataNode<>(uri));
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        List<Node> descNodes = Nodes.findDescendantNodes(doc.getContentDom(), OdfElement.DESC.getQualifiedName(), true);
        Assertions.assertThat(descNodes.size()).isEqualTo(2);
        SvgDescElement element0 = ((SvgDescElement) descNodes.get(0));
        SvgDescElement element1 = ((SvgDescElement) descNodes.get(1));
        Assertions.assertThat((element0.getTextContent())).isEqualTo("Smile1.png");
        Assertions.assertThat((element1.getTextContent())).isEqualTo("Smile2.png");
    }
}
