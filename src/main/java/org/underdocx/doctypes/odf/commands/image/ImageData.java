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
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderCodec;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.Optional;

public abstract class ImageData {

    protected static Optional<ParametersPlaceholderData> parse(String descr) {
        if (descr != null) {
            return ParametersPlaceholderCodec.INSTANCE.tryParse(descr);
        } else {
            return Optional.empty();
        }
    }

    protected static String getDescrStatic(Node svgDesc) {
        if (svgDesc != null) {
            String content = svgDesc.getTextContent();
            return content == null ? "" : content.trim();
        } else {
            return null;
        }
    }

    protected static Optional<Pair<SvgDescElement, ParametersPlaceholderData>> getBaseData(Node node) {
        if (OdfElement.DESC.is(node)) {
            String content = getDescrStatic(node);
            if (content != null && !content.isEmpty()) {
                Optional<ParametersPlaceholderData> paramPlaceholderData = parse(content);
                if (paramPlaceholderData.isPresent()) {
                    return Optional.of(new Pair<>((SvgDescElement) node, paramPlaceholderData.get()));
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<? extends ImageData> create(Node node) {
        Optional<? extends ImageData> data = MainImageData.createExistingMainImageDataFromDesc(node);
        if (data.isPresent()) {
            return data;
        } else {
            return BackgroundImageData.createBackgroundImageData(node);
        }
    }

    protected final ParametersPlaceholderData parameterizedPlaceholder;
    protected final SvgDescElement svgDesc;


    protected ImageData(SvgDescElement svgDesc, ParametersPlaceholderData data) {
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

    private String getFile(String path) {
        String result = path;
        int index = result.lastIndexOf("\\");
        if (index >= 0 && index != result.length() - 1) {
            result = result.substring(index + 1);
        }
        index = result.lastIndexOf("/");
        if (index >= 0 && result.length() - 1 > index) {
            result = result.substring(index + 1);
        }
        index = result.lastIndexOf(":");
        if (index >= 0 && result.length() - 1 > index) {
            result = result.substring(index + 1);
        }
        return result;
    }

    public Pair<String, String> store(Resource resource, OdfDocument doc) {
        String fileName = getFile(resource.getIdentifier());
        String packageName = "Pictures/" + fileName;
        String mimeType = Problems.RESOURCE_WITHOUT_REQUIRED_MIMETYPE.get(resource.getMimeType(), "MIME Type");
        InputStream data = Problems.IO_EXCEPTION.exec(resource::openStream);
        OdfPackage pack = doc.getPackage();
        if (pack.contains(packageName)) {
            UnderdocxEnv.getInstance().logger.trace("Image " + fileName + " found in " + packageName);
        } else {
            Problems.IO_EXCEPTION.run(() -> pack.insert(data, packageName, mimeType));
        }
        return new Pair<>(fileName, packageName);
    }
}
