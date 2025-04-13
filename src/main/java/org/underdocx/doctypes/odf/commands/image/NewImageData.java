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
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.underdocx.common.tools.TmpFile;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderCodec;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Optional;

public abstract class NewImageData {

    protected static Optional<ParametersPlaceholderData> parse(String descr) {
        return ParametersPlaceholderCodec.INSTANCE.tryParse(descr);
    }

    protected static String getDescrStatic(Node svgDesc) {
        String content = svgDesc.getTextContent();
        return content == null ? "" : content.trim();
    }

    protected static Optional<Pair<SvgDescElement, ParametersPlaceholderData>> getBaseData(Node node) {
        if (OdfElement.DESC.is(node)) {
            String content = getDescrStatic(node);
            if (content != null && content.length() > 0) {
                Optional<ParametersPlaceholderData> paramPlaceholderData = parse(content);
                if (paramPlaceholderData.isPresent()) {
                    return Optional.of(new Pair<>((SvgDescElement) node, paramPlaceholderData.get()));
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<? extends NewImageData> create(Node node) {
        Optional<? extends NewImageData> data = NewMainImageData.createMainImageData(node);
        if (data.isPresent()) {
            return data;
        } else {
            return NewBackgroundImageData.createBackgroundImageData(node);
        }
    }

    protected final ParametersPlaceholderData parameterizedPlaceholder;
    protected final SvgDescElement svgDesc;


    protected NewImageData(SvgDescElement svgDesc, ParametersPlaceholderData data) {
        this.svgDesc = svgDesc;
        this.parameterizedPlaceholder = data;
    }

    public String getKey() {
        return parameterizedPlaceholder.getKey();
    }

    public JsonNode getJson() {
        return parameterizedPlaceholder.getJson();
    }


    public String getDesc() {
        return getDescrStatic(svgDesc);
    }

    public void setDesc(String name) {
        svgDesc.setTextContent(name);
    }

    public String newImage(URI imageUri, OdfContentDom dom) {
        OdfDrawImage image = new OdfDrawImage(dom);
        try {
            return image.newImage(imageUri);
        } catch (Exception e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    public URI createUri(Resource resource, String imageSuffix) {
        URI uri = null;
        try {
            File tmpFile = TmpFile.createTmpFile("image_", imageSuffix, true, 10000L);
            byte[] data = resource.getData();
            new FileOutputStream(tmpFile).write(data);
            uri = tmpFile.toURI();
        } catch (Exception e) {
            Problems.IO_EXCEPTION.fire(e);
        }
        return uri;
    }
}
