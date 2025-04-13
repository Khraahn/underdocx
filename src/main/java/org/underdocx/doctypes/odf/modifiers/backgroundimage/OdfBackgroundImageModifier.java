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

package org.underdocx.doctypes.odf.modifiers.backgroundimage;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFillImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawShapeElementBase;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.NewBackgroundImageData;
import org.underdocx.doctypes.odf.commands.image.NewImageData;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;

import java.net.URI;

public class OdfBackgroundImageModifier<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements SelectionModifier<Selection<C, NewImageData, D>, OdfBackgroundImageModifierData, ModifierResult> {

    private String removePathPrefix(String name) {
        String result = name;
        if (name.startsWith("Pictures/")) {
            result = name.substring("Pictures/".length());
            if (result.contains(".")) {
                result = result.substring(0, result.lastIndexOf("."));
            }
        }
        return result;
    }

    @Override
    public ModifierResult modify(Selection<C, NewImageData, D> selection, OdfBackgroundImageModifierData modifierData) {
        NewBackgroundImageData placeholder = (NewBackgroundImageData) selection.getPlaceholderData();
        DrawShapeElementBase drawShape = placeholder.getDrawShapeElement();
        String newName = modifierData.getFileName();
        StyleStyleElement style = drawShape.getOrCreateUnqiueAutomaticStyle();
        if (style != null) {
            StyleGraphicPropertiesElement graphicProperties = (StyleGraphicPropertiesElement) style.getOrCreatePropertiesElement(OdfStylePropertiesSet.GraphicProperties);
            if (graphicProperties != null) {
                String path = null;
                if (modifierData.getResource().getURI().isPresent()) {
                    path = placeholder.newImage(modifierData.getResource().getURI().get(), selection.getDocContainer().getContentDom());
                } else {
                    URI uri = placeholder.createUri(modifierData.getResource(), newName);
                    path = placeholder.newImage(uri, selection.getDocContainer().getContentDom());
                }
                graphicProperties.setDrawFillAttribute("bitmap");
                graphicProperties.setDrawFillImageNameAttribute(removePathPrefix(path));

                // remove redundant graphic properties of paragraph style
                drawShape.removeAttribute("draw:text-style-name");
                // TODO: If required, don't remove but update graphic-properties of referenced style of "text-style-name"

                DrawFillImageElement fillImage = selection.getDocContainer().getStylesDom().getOfficeStyles().newDrawFillImageElement(removePathPrefix(path), path, "simple");
                fillImage.setXlinkActuateAttribute("onLoad");
                fillImage.setXlinkShowAttribute("embed");

                String title = modifierData.getNewDesc();
                placeholder.setDesc(title == null ? newName : title);

                return ModifierResult.SUCCESS;
            }
        }
        return ModifierResult.IGNORED;
    }

    /*
                    StyleBackgroundImageElement background = graphicProperties.newStyleBackgroundImageElement();
                background.setStylePositionAttribute("center");
                background.setStyleRepeatAttribute("no-repeat");
                background.setXlinkHrefAttribute(path);
                background.setXlinkTypeAttribute("simple");
                background.setXlinkShowAttribute("embed");
                background.setXlinkActuateAttribute("onLoad");
     */
}
