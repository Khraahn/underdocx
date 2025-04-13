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
import java.util.Base64;
import java.util.Optional;

public abstract class ImageData {

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

    public static Optional<? extends ImageData> create(Node node) {
        Optional<? extends ImageData> data = MainImageData.createMainImageData(node);
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

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index >= 0 && filename.length() > index) {
            return filename.substring(index + 1).toLowerCase();
        } else {
            return filename.toLowerCase();
        }
    }

    private String getFile(String path) {
        String result = path;
        int index = result.lastIndexOf("\\");
        if (index >= 0 && result.length() > index) {
            result = result.substring(index + 1);
        }
        index = result.lastIndexOf("/");
        if (index >= 0 && result.length() > index) {
            result = result.substring(index + 1);
        }
        return result;
    }

    public String getMimeType(String imageName) {
        String extension = getExtension(imageName);
        return switch (extension) {
            case "png" -> "image/png";
            case "jpeg", "jpg" -> "image/jpeg";
            case "svg" -> "image/svg+xml";
            case "tif", "tiff" -> "image/tiff";
            case "webp" -> "image/webp";
            case "gif" -> "image/gif";
            default -> "image/bmp";
        };
    }

    public String getSafeIdentifier(Resource resource) {
        return Base64.getEncoder().encodeToString(resource.getIdentifier().getBytes());
    }

    public Pair<String, String> store(Resource resource, OdfDocument doc) {
        String fileName = getFile(resource.getIdentifier());
        String packageName = "Pictures/" + fileName;
        String mimeType = getMimeType(fileName);
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
