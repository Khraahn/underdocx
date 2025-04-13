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

package org.underdocx.doctypes.odf.commands.image;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.util.Optional;

public class MainImageData extends ImageData {
    private final static Regex number = new Regex("[0-9]+(\\.[0-9]+)?");

    private final DrawImageElement drawImageElement;
    private final DrawFrameElement drawFrameElement;

    protected MainImageData(SvgDescElement svgDesc, DrawFrameElement drawFrameElement, DrawImageElement drawImageElement, ParametersPlaceholderData data) {
        super(svgDesc, data);
        this.drawImageElement = drawImageElement;
        this.drawFrameElement = drawFrameElement;
    }

    public static Optional<MainImageData> createMainImageData(Node node) {
        return Convenience.buildOptional(result -> {
            getBaseData(node).ifPresent(baseData -> {
                Node parentNode = node.getParentNode();
                if (OdfElement.FRAME.is(parentNode)) {
                    DrawFrameElement drawFrameElement = (DrawFrameElement) parentNode;
                    Nodes.getChildren(drawFrameElement, OdfElement.IMAGE).tryNext().ifPresent(imageNode -> {
                        DrawImageElement drawImageElement = (DrawImageElement) imageNode;
                        result.value = new MainImageData(baseData.left, drawFrameElement, drawImageElement, baseData.right);
                    });
                }
            });
        });
    }

    public String getWidthUnit() {
        return OdfLengthUnit.parseValue(getWidthAttr()).get().toString();
    }

    public String getWidthAttr() {
        return drawFrameElement.getSvgWidthAttribute();
    }

    public String getHeightAttr() {
        return drawFrameElement.getSvgHeightAttribute();
    }

    private void setWidthAttr(String value) {
        drawFrameElement.setSvgWidthAttribute(value);
    }

    private void setHeightAttr(String value) {
        drawFrameElement.setSvgHeightAttribute(value);
    }

    public double getWidthValue() {
        return Double.parseDouble(number.findFirstAsString(getWidthAttr()).get());
    }

    public String getHeightUnit() {
        return OdfLengthUnit.parseValue(getHeightAttr()).get().toString();
    }

    public double getHeightValue() {
        return Double.parseDouble(number.findFirstAsString(getHeightAttr()).get());
    }

    public void setWidth(double value, String unit) {
        setWidthAttr(value + unit);
    }

    public void setHeight(double value, String unit) {
        setHeightAttr(value + unit);
    }


    public void exchangeImage(Resource resource, OdfDocument doc) {
        Pair<String, String> fileNameAndPath = store(resource, doc);
        drawImageElement.setXlinkHrefAttribute(fileNameAndPath.right);
        setDesc(fileNameAndPath.right);
    }
}
