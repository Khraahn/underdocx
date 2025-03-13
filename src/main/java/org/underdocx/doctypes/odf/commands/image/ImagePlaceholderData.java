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

import com.fasterxml.jackson.databind.JsonNode;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tools.TmpFile;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.common.types.Tripple;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.doctypes.odf.placeholdersprovider.dollar.image.BasicImagePlaceholderData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Optional;

public class ImagePlaceholderData implements BasicImagePlaceholderData {
    private final static Regex number = new Regex("[0-9]+(\\.[0-9]+)?");

    private final DrawImageElement drawImageElement;
    private final ParametersPlaceholderData parameterizedPlaceholder;
    private final DrawFrameElement drawFrameElement;
    private final OdfContentDom dom;
    private final SvgDescElement svgDesc;

    public ImagePlaceholderData(OdfContentDom dom, DrawFrameElement drawFrameElement, DrawImageElement drawImageElement, SvgDescElement svgDesc, ParametersPlaceholderData parameterizedPlaceholder) {
        this.drawImageElement = drawImageElement;
        this.drawFrameElement = drawFrameElement;
        this.svgDesc = svgDesc;
        this.parameterizedPlaceholder = parameterizedPlaceholder;
        this.dom = dom;
    }


    private static Optional<DrawFrameElement> getFrame(Node node) {
        return Optional.ofNullable(node instanceof DrawFrameElement ? (DrawFrameElement) node : null);
    }

    private static Optional<DrawImageElement> getImage(DrawFrameElement node) {
        Optional<Node> child = Nodes.findFirstDescendantNode(node, OdfElement.IMAGE.getQualifiedName());
        return child.map(c -> (DrawImageElement) c);
    }

    private static Optional<SvgDescElement> getSvgDesc(DrawFrameElement node) {
        Optional<Node> child = Nodes.findFirstDescendantNode(node, OdfElement.DESC.getQualifiedName());
        return child.map(c -> (SvgDescElement) c);
    }

    private static Optional<Tripple<DrawFrameElement, DrawImageElement, SvgDescElement>> getImageElements(Node node) {
        return Optional.ofNullable(Convenience.build(trippleWrapper -> {
            getFrame(node).ifPresent(frame -> {
                getImage(frame).ifPresent(image -> {
                    getSvgDesc(frame).ifPresent(desc -> {
                        trippleWrapper.value = new Tripple<>(frame, image, desc);
                    });
                });
            });
        }));
    }

    private static Optional<ParametersPlaceholderData> parse(String imageName) {
        return ParametersPlaceholderCodec.INSTANCE.tryParse(imageName);
    }

    private static String getDescrStatic(SvgDescElement svgDesc) {
        String content = svgDesc.getTextContent();
        return content == null ? "" : content.trim();
    }

    public static Optional<ImagePlaceholderData> createPlaceholder(Node node, AbstractOdfContainer doc) {
        if (!(node instanceof DrawFrameElement)) return Optional.empty(); // fast check
        return Convenience.buildOptional(w ->
                getImageElements(node).ifPresent(elements -> parse(getDescrStatic(elements.right)).ifPresent(phd ->
                        w.value = new ImagePlaceholderData(doc.getContentDom(), elements.left, elements.middle, elements.right, phd))));

    }

    public String getKey() {
        return parameterizedPlaceholder.getKey();
    }

    public JsonNode getJson() {
        return parameterizedPlaceholder.getJson();
    }

    // ---------- Image Fields access --------------


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

    public String getDesc() {
        return getDescrStatic(svgDesc);
    }

    public void setDesc(String name) {
        svgDesc.setTextContent(name);
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

    public void exchangeImage(URI imageUri) {
        OdfDrawImage image = new OdfDrawImage(dom);
        try {
            String packagePath = image.newImage(imageUri);
            drawImageElement.setXlinkHrefAttribute(packagePath);
            setDesc(packagePath);
            // TODO set mimetype? String mimeType = OdfFileEntry.getMediaTypeString(imageUri.toString());
        } catch (Exception e) {
            Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    public void exchangeImage(Resource resource, String imageSuffix) {
        URI uri = null;
        try {
            File tmpFile = TmpFile.createTmpFile("image_", imageSuffix, true, 10000L);
            byte[] data = resource.getData();
            new FileOutputStream(tmpFile).write(data);
            uri = tmpFile.toURI();
        } catch (Exception e) {
            Problems.IO_EXCEPTION.fire(e);
        }
        exchangeImage(uri);
    }

}
