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

package org.underdocx.doctypes.odf.modifiers.existingimage;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.cache.SelfClearingCache;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.ImageData;
import org.underdocx.doctypes.odf.commands.image.MainImageData;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problems;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class OdfExistingImageModifier<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements SelectionModifier<Selection<C, ImageData, D>, OdfExistingImageModifierData, ModifierResult> {

    @Override
    public ModifierResult modify(Selection<C, ImageData, D> selection, OdfExistingImageModifierData modifierData) {
        MainImageData placeholder = (MainImageData) selection.getPlaceholderData();
        Pair<Double, Double> importImageWidthHeight;
        String newName = modifierData.getFileName();
        Resource resource = modifierData.getResource();
        importImageWidthHeight = getDimension(resource);
        placeholder.exchangeImage(resource, selection.getDocContainer().getDocument());

        UnderdocxEnv.getInstance().logger.trace("new image dimension; " + importImageWidthHeight);
        if (modifierData.getKeepWidth() != null) {
            if (modifierData.getKeepWidth()) {
                String newHeightUnit = placeholder.getWidthUnit();
                UnderdocxEnv.getInstance().logger.trace("template image width to keep: " + placeholder.getWidthValue());
                double height = placeholder.getWidthValue() * importImageWidthHeight.right / importImageWidthHeight.left;
                UnderdocxEnv.getInstance().logger.trace("calculated height: " + height);
                placeholder.setHeight(height, newHeightUnit);
            } else {
                String newWidthUnit = placeholder.getHeightUnit();
                UnderdocxEnv.getInstance().logger.trace("template image height to keep: " + placeholder.getHeightValue());
                double width = placeholder.getHeightValue() * importImageWidthHeight.left / importImageWidthHeight.right;
                UnderdocxEnv.getInstance().logger.trace("calculated width: " + width);
                placeholder.setWidth(width, newWidthUnit);
            }
        }
        String title = modifierData.getNewDesc();
        placeholder.setDesc(title == null ? newName : title);
        return ModifierResult.SUCCESS;
    }

    Pair<Double, Double> getDimension(Resource data) {
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

    static SelfClearingCache<String, Pair<Double, Double>> dimensionCache = new SelfClearingCache<>(1000);
}
