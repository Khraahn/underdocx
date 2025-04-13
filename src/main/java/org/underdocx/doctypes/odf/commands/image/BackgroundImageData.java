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

import org.odftoolkit.odfdom.dom.element.draw.DrawShapeElementBase;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.util.Optional;

public class BackgroundImageData extends ImageData {

    private final DrawShapeElementBase drawShape;

    protected BackgroundImageData(DrawShapeElementBase drawShape, SvgDescElement svgDesc, ParametersPlaceholderData data) {
        super(svgDesc, data);
        this.drawShape = drawShape;
    }

    public static Optional<BackgroundImageData> createBackgroundImageData(Node node) {
        return Convenience.buildOptional(result -> {
            getBaseData(node).ifPresent(baseData -> {
                Node parentNode = node.getParentNode();
                if (parentNode instanceof DrawShapeElementBase) {
                    DrawShapeElementBase drawCustomShapeElement = (DrawShapeElementBase) parentNode;
                    result.value = new BackgroundImageData(drawCustomShapeElement, baseData.left, baseData.right);
                }
            });
        });
    }

    public DrawShapeElementBase getDrawShapeElement() {
        return drawShape;
    }
}
