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

package org.underdocx.doctypes.odf.modifiers.images.tools;

import org.underdocx.common.cache.SelfClearingCache;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Resource;
import org.underdocx.environment.err.Problems;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CachedImageSize {

    public static Pair<Double, Double> getDimension(Resource data) {
        String resourceIdentifier = data.getIdentifier();
        Pair<Double, Double> result = dimensionCache.getOrCache(resourceIdentifier, () -> {
            try {
                BufferedImage bufferedImage = ImageIO.read(data.openStream());
                return new Pair<>((double) bufferedImage.getWidth(), (double) bufferedImage.getHeight());
            } catch (IOException e) {
                return Problems.IO_EXCEPTION.fire(e);
            }
        });
        return result;
    }

    private static final SelfClearingCache<String, Pair<Double, Double>> dimensionCache = new SelfClearingCache<>(1000);
}
