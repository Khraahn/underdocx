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

package de.underdocx.commands.image;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import de.underdocx.tools.common.TmpFile;
import de.underdocx.tools.odf.constants.OdfElement;
import de.underdocx.tools.tree.Nodes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.pushVariable("name1", new LeafModelNode<>("Smile1.png"));
        engine.pushVariable("data", new LeafModelNode<>(binaryData));
        engine.pushVariable("name2", new LeafModelNode<>("Smile2.png"));
        engine.pushVariable("uri", new LeafModelNode<>(uri));
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        List<Node> drawImagesNodes = Nodes.findDescendantNodes(doc.getContentDom(), OdfElement.FRAME.getQualifiedName(), true);
        Assertions.assertThat(drawImagesNodes.size()).isEqualTo(2);
        DrawFrameElement element0 = ((DrawFrameElement) drawImagesNodes.get(0));
        DrawFrameElement element1 = ((DrawFrameElement) drawImagesNodes.get(1));
        Assertions.assertThat((element0.getDrawNameAttribute())).isEqualTo("Smile1.png");
        Assertions.assertThat((element1.getDrawNameAttribute())).isEqualTo("Smile2.png");
    }
}
