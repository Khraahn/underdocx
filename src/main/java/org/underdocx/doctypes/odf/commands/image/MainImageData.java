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
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.commands.image.types.*;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.doctypes.odf.constants.OdfNameSpace;
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

    public static MainImageData createNewImage(TextParagraphElementBase paragraph, ParametersPlaceholderData data) {
        DrawFrameElement localDrawFrameElement = paragraph.newDrawFrameElement();
        DrawImageElement localDrawImageElement = localDrawFrameElement.newDrawImageElement();
        SvgDescElement descElement = localDrawFrameElement.newSvgDescElement();
        descElement.setTextContent("");
        localDrawFrameElement.setTextAnchorTypeAttribute(Anchor.PARAGRAPH.toString());
        localDrawFrameElement.setDrawZIndexAttribute(0);
        localDrawImageElement.setXlinkTypeAttribute("simple");
        localDrawImageElement.setXlinkShowAttribute("embed");
        localDrawImageElement.setXlinkActuateAttribute("onLoad");
        StyleGraphicPropertiesElement style = getStyleStatic(localDrawFrameElement);
        style.setStyleRunThroughAttribute("foreground");
        style.setAttributeNS(OdfNameSpace.STYLE.getUri(), "style:number-wrapped-paragraphs", "no-limit");
        style.setStyleWrapContourAttribute(false);
        style.setStyleMirrorAttribute("none");
        style.setFoClipAttribute("rect(0cm, 0cm, 0cm, 0cm)");
        style.setDrawLuminanceAttribute("0%");
        style.setDrawContrastAttribute("0%");
        style.setDrawRedAttribute("0%");
        style.setDrawGreenAttribute("0%");
        style.setDrawBlueAttribute("0%");
        style.setDrawGammaAttribute("100%");
        style.setDrawColorInversionAttribute(false);
        style.setDrawImageOpacityAttribute("100%");
        style.setDrawColorModeAttribute("standard");

        return new MainImageData(descElement, localDrawFrameElement, localDrawImageElement, data);
    }

    public static Optional<MainImageData> createExistingMainImageDataFromDesc(Node node) {
        return Convenience.buildOptional(result ->
                getBaseData(node).ifPresent(baseData -> {
                    Node parentNode = node.getParentNode();
                    if (OdfElement.FRAME.is(parentNode)) {
                        DrawFrameElement drawFrameElement = (DrawFrameElement) parentNode;
                        Nodes.getChildren(drawFrameElement, OdfElement.IMAGE).tryNext().ifPresent(imageNode -> {
                            DrawImageElement drawImageElement = (DrawImageElement) imageNode;
                            result.value = new MainImageData(baseData.left, drawFrameElement, drawImageElement, baseData.right);
                        });
                    }
                }));
    }

    public static Optional<MainImageData> createExistingMainImageDataFromFrame(Node node) {
        return Convenience.buildOptional(result -> {
            if (node instanceof DrawFrameElement drawFrameElement) {
                SvgDescElement desc = null;
                DrawImageElement image = null;
                ParametersPlaceholderData placeholderData = null;
                for (Node child : Nodes.getChildren(drawFrameElement)) {
                    if (child instanceof SvgDescElement svgDescElement) {
                        desc = svgDescElement;
                    }
                    if (child instanceof DrawImageElement drawImageElement) {
                        image = drawImageElement;
                    }
                }
                if (desc != null) {
                    placeholderData = parse(getDescrStatic(desc)).orElse(null);
                }
                if (image != null) {
                    result.value = new MainImageData(desc, drawFrameElement, image, placeholderData);
                }
            }
        });

    }

    public Node getMainNode() {
        return drawFrameElement;
    }

    // --- SVG width

    public OdfLengthUnit getWidthUnit() {
        return Convenience.nullCheck(getWidthAttr(), widthAttr -> OdfLengthUnit.parseValue(widthAttr).get());
    }

    public String getWidthAttr() {
        return drawFrameElement.getSvgWidthAttribute();
    }


    private void setWidthAttr(String value) {
        drawFrameElement.setSvgWidthAttribute(value);
    }

    public Double getWidthValue() {
        return Convenience.nullCheck(getWidthAttr(), widthAttr -> Double.parseDouble(number.findFirstAsString(widthAttr).get()));
    }


    public void setWidth(double value, OdfLengthUnit unit) {
        setWidthAttr(value + unit.toString());
    }

    // --- SVG height

    public String getHeightAttr() {
        return drawFrameElement.getSvgHeightAttribute();
    }

    public Double getHeightValue() {
        return Convenience.nullCheck(getHeightAttr(), heightAttr -> Double.parseDouble(number.findFirstAsString(heightAttr).get()));
    }

    public OdfLengthUnit getHeightUnit() {
        return Convenience.nullCheck(getHeightAttr(), heightAttr -> OdfLengthUnit.parseValue(heightAttr).get());
    }


    public void setHeight(double value, OdfLengthUnit unit) {
        setHeightAttr(value + unit.toString());
    }

    private void setHeightAttr(String value) {
        drawFrameElement.setSvgHeightAttribute(value);
    }

    // --- SVG X

    public void setX(double value, OdfLengthUnit unit) {
        setXAttr(value + unit.toString());
    }

    private void setXAttr(String value) {
        drawFrameElement.setSvgXAttribute(value);
    }

    public String getXAttr() {
        return drawFrameElement.getSvgXAttribute();
    }

    public Double getXValue() {
        return Convenience.nullCheck(getXAttr(), xAttr -> Double.parseDouble(number.findFirstAsString(xAttr).get()));
    }

    public OdfLengthUnit getXUnit() {
        return Convenience.nullCheck(getXAttr(), xAttr -> OdfLengthUnit.parseValue(xAttr).get());
    }

    // --- SVG Y

    public void setY(double value, OdfLengthUnit unit) {
        setYAttr(value + unit.toString());
    }

    private void setYAttr(String value) {
        drawFrameElement.setSvgYAttribute(value);
    }

    public String getYAttr() {
        return drawFrameElement.getSvgYAttribute();
    }

    public Double getYValue() {
        return Convenience.nullCheck(getYAttr(), yAttr -> Double.parseDouble(number.findFirstAsString(yAttr).get()));
    }

    public OdfLengthUnit getYUnit() {
        return Convenience.nullCheck(getYAttr(), yAttr -> OdfLengthUnit.parseValue(yAttr).get());
    }

    // Anchor

    public void setAnchor(Anchor anchor) {
        drawFrameElement.setTextAnchorTypeAttribute(anchor.toString());
    }

    public Anchor getAnchor() {
        return Anchor.fromString(drawFrameElement.getTextAnchorTypeAttribute()).orElse(null);
    }

    // styles

    protected static StyleGraphicPropertiesElement getStyleStatic(DrawFrameElement drawFrameElement) {
        StyleStyleElement style = drawFrameElement.getOrCreateUnqiueAutomaticStyle();
        StyleGraphicPropertiesElement graphicProperties = (StyleGraphicPropertiesElement) style.getOrCreatePropertiesElement(OdfStylePropertiesSet.GraphicProperties);
        return graphicProperties;
    }

    protected StyleGraphicPropertiesElement getStyle() {
        return getStyleStatic(drawFrameElement);
    }

    public void setVerticalPos(VerticalPos verticalPos) {
        getStyle().setStyleVerticalPosAttribute(verticalPos.toString());
    }

    public VerticalPos getVerticalPos() {
        return VerticalPos.fromString(getStyle().getStyleVerticalPosAttribute()).orElse(null);
    }

    public void setVerticalRel(VerticalRel verticalRel) {
        getStyle().setStyleVerticalRelAttribute(verticalRel.toString());
    }

    public VerticalRel getVerticalRel() {
        return VerticalRel.fromString(getStyle().getStyleVerticalRelAttribute()).orElse(null);
    }

    public void setHorizontalPos(HorizontalPos horizontalPos) {
        getStyle().setStyleHorizontalPosAttribute(horizontalPos.toString());
    }

    public HorizontalPos getHorizontalPos() {
        return HorizontalPos.fromString(getStyle().getStyleHorizontalPosAttribute()).orElse(null);
    }

    public void setHorizontalRel(HorizontalRel horizontalRel) {
        getStyle().setStyleHorizontalRelAttribute(horizontalRel.toString());
    }

    public HorizontalRel getHorizontalRel() {
        return HorizontalRel.fromString(getStyle().getStyleHorizontalRelAttribute()).orElse(null);
    }

    public void setWrap(Wrap wrap) {
        getStyle().setStyleWrapAttribute(wrap.toString());
    }

    public Wrap getWrap() {
        return Wrap.fromString(getStyle().getStyleWrapAttribute()).orElse(null);
    }

    // --------------------


    public void exchangeImage(Resource resource, OdfDocument doc) {
        Pair<String, String> fileNameAndPath = store(resource, doc);
        String mimeType = getMimeType(fileNameAndPath.left);
        drawImageElement.setXlinkHrefAttribute(fileNameAndPath.right);
        drawImageElement.setAttributeNS(OdfNameSpace.DRAW.getUri(), "draw:mime-type", mimeType);
        String currentName = drawFrameElement.getDrawNameAttribute();
        if (currentName == null || currentName.isBlank()) {
            drawFrameElement.setDrawNameAttribute(fileNameAndPath.left);
        }
        setDesc(fileNameAndPath.right);
    }

    public void setName(String name) {
        drawFrameElement.setDrawNameAttribute(name);
    }

    public String getName() {
        return drawFrameElement.getDrawNameAttribute();
    }
}
