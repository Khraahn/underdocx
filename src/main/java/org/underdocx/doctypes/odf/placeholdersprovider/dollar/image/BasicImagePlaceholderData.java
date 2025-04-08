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

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Resource;
import org.underdocx.environment.err.Problems;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

public interface BasicImagePlaceholderData {

    String getWidthUnit();

    String getWidthAttr();

    String getHeightAttr();

    String getDesc();

    double getWidthValue();

    String getHeightUnit();

    double getHeightValue();

    void setWidth(double value, String unit);

    void setHeight(double value, String unit);

    void exchangeImage(URI imageUrl, OdfContentDom dom);

    void exchangeImage(Resource resource, String suffix, OdfContentDom dom);

    static Pair<Double, Double> getDimension(URI uri) {
        try {
            BufferedImage bufferedImage = ImageIO.read(uri.toURL());
            return new Pair<>((double) bufferedImage.getWidth(), (double) bufferedImage.getHeight());
        } catch (IOException e) {
            return Problems.IO_EXCEPTION.fire(e);
        }
    }

    static Pair<Double, Double> getDimension(Resource data) {
        try {
            BufferedImage bufferedImage = ImageIO.read(data.openStream());
            return new Pair<>((double) bufferedImage.getWidth(), (double) bufferedImage.getHeight());
        } catch (IOException e) {
            return Problems.IO_EXCEPTION.fire(e);
        }
    }

    void setDesc(String name);
}
