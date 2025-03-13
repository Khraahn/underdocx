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

package org.underdocx.doctypes.odf.placeholdersprovider.dollar.image;

import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.common.types.Tripple;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.environment.err.Problems;

import java.net.URI;

public class SimpleDollarImagePlaceholderDataOdf implements SimpleDollarImagePlaceholderData {


    private final AbstractOdfContainer<?> doc;
    private Tripple<DrawFrameElement, DrawImageElement, SvgDescElement> elements = null;
    private final static Regex number = new Regex("[0-9]+(\\.[0-9]+)?");

    public SimpleDollarImagePlaceholderDataOdf(AbstractOdfContainer<?> doc, Tripple<DrawFrameElement, DrawImageElement, SvgDescElement> odfElements) {
        this.doc = doc;
        this.elements = odfElements;
    }

    public String getWidthUnit() {
        return OdfLengthUnit.parseValue(getWidthAttr()).get().toString();
    }

    public String getWidthAttr() {
        return elements.left.getSvgWidthAttribute();
    }

    public String getHeightAttr() {
        return elements.left.getSvgHeightAttribute();
    }

    private void setWidthAttr(String value) {
        elements.left.setSvgWidthAttribute(value);
    }

    private void setHeightAttr(String value) {
        elements.left.setSvgHeightAttribute(value);
    }

    public String getDesc() {
        return elements.right.getTextContent();
    }

    public String getVariableName() {
        String name = getDesc().trim();
        return name.startsWith("$") ? name.substring(1) : name;
    }

    public void setDesc(String name) {
        elements.right.setTextContent(name);
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
        OdfDrawImage image = new OdfDrawImage(doc.getContentDom());
        try {
            String packagePath = image.newImage(imageUri);
            elements.middle.setXlinkHrefAttribute(packagePath);
            setDesc(packagePath);
            // TODO set mimetype? String mimeType = OdfFileEntry.getMediaTypeString(imageUri.toString());
        } catch (Exception e) {
            Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    @Override
    public void exchangeImage(Resource data, String suffix) {
        throw new UnsupportedOperationException("This type does not support binary image data");
    }
}
