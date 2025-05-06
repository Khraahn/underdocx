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
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.commands.image.MainImageData;
import org.underdocx.doctypes.odf.commands.image.types.*;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class CreateImageTest extends AbstractOdtTest {

    private MainImageData findImage(OdtContainer doc) {
        DrawFrameElement frame = (DrawFrameElement) Nodes.findFirstDescendantNode(doc.getContentRoot(), node -> node instanceof DrawFrameElement).get();
        return MainImageData.createExistingMainImageDataFromFrame(frame).get();
    }

    @Test
    public void testCreateCharImage() {
        String content = """
                ABC${CreateImage $resource:"resource", anchor:"as-char", horizontalPos:"from-left", horizontalRel:"page", verticalPos:"middle", verticalRel:"text", width:12, unit:"pt", name:"Test", desc:"Image Test"}DEF
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        Resource resource = new Resource.ClassResource(ImageTest.class, "smile.png");
        engine.pushLeafVariable("resource", resource);
        engine.run(doc);
        //show(doc);

        MainImageData imageData = findImage(doc);
        Assertions.assertThat(imageData.getAnchor()).isSameAs(Anchor.AS_CHAR);
        Assertions.assertThat(imageData.getWrap()).isNull();
        Assertions.assertThat(imageData.getHeightUnit()).isSameAs(OdfLengthUnit.PT);
        Assertions.assertThat(imageData.getHeightValue()).isEqualTo(12.0);
        Assertions.assertThat(imageData.getWidthUnit()).isSameAs(OdfLengthUnit.PT);
        Assertions.assertThat(imageData.getWidthValue()).isEqualTo(12.0);
        Assertions.assertThat(imageData.getXValue()).isNull();
        Assertions.assertThat(imageData.getYValue()).isNull();
        Assertions.assertThat(imageData.getHorizontalPos()).isSameAs(HorizontalPos.FROM_LEFT);
        Assertions.assertThat(imageData.getHorizontalRel()).isSameAs(HorizontalRel.PAGE);
        Assertions.assertThat(imageData.getVerticalPos()).isSameAs(VerticalPos.MIDDLE);
        Assertions.assertThat(imageData.getVerticalRel()).isSameAs(VerticalRel.TEXT);
        Assertions.assertThat(imageData.getName()).isEqualTo("Test");
        Assertions.assertThat(imageData.getDesc()).isEqualTo("Image Test");
    }
}
