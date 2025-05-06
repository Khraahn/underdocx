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

package org.underdocx.doctypes.odf.modifiers.images.createimage;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreeSplitter;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.MainImageData;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.doctypes.odf.modifiers.images.tools.CachedImageSize;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

public class CreateImageModifier<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements SelectionModifier<Selection<C, ParametersPlaceholderData, D>, CreateImageModifierData, ModifierNodeResult> {
    private final ModifiersProvider<C, D> modifiers;

    public CreateImageModifier(ModifiersProvider<C, D> modifiersProvider) {
        this.modifiers = modifiersProvider;
    }

    @Override
    public ModifierNodeResult modify(Selection<C, ParametersPlaceholderData, D> selection, CreateImageModifierData modifierData) {
        Node placeholder = selection.getNode();
        ParametersPlaceholderData placeholderData = selection.getPlaceholderData();
        TextParagraphElementBase commonAncestor = (TextParagraphElementBase) Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.findAscendantParagraph(placeholder, false), "paragraph of placeholder");
        TreeSplitter.split(placeholder, commonAncestor, OdfTextNodeInterpreter.INSTANCE);
        Node placeholderAncestor = Problems.CANT_FIND_DOM_ELEMENT.get(Nodes.findAncestorChild(placeholder, commonAncestor), "placeholder ancestor node");
        MainImageData imageData = MainImageData.createNewImage(commonAncestor, placeholderData);

        Problems.MISSING_VALUE.check(modifierData.getHeight() != null || modifierData.getWidth() != null, "height", null);
        Problems.MISSING_VALUE.notNull(modifierData.getResource(), "resource");
        OdfLengthUnit unit = modifierData.getUnit();
        if (unit == null) {
            if (modifierData.getHeight() != null) {
                unit = imageData.getHeightUnit();
            } else {
                unit = imageData.getWidthUnit();
            }
        }

        if (modifierData.getHeight() != null && modifierData.getWidth() != null) {
            imageData.setHeight(modifierData.getHeight(), unit);
            imageData.setWidth(modifierData.getWidth(), unit);
        } else {
            Pair<Double, Double> dimension = CachedImageSize.getDimension(modifierData.getResource());
            if (modifierData.getWidth() != null) {
                double height = modifierData.getWidth() * dimension.right / dimension.left;
                imageData.setHeight(height, unit);
                imageData.setWidth(modifierData.getWidth(), unit);
            } else {
                double width = modifierData.getHeight() * dimension.left / dimension.right;
                imageData.setWidth(width, unit);
                imageData.setHeight(modifierData.getHeight(), unit);
            }
        }

        if (modifierData.getAnchor() != null) {
            imageData.setAnchor(modifierData.getAnchor());
        }
        if (modifierData.getX() != null) {
            imageData.setX(modifierData.getX(), unit);
        }
        if (modifierData.getY() != null) {
            imageData.setY(modifierData.getY(), unit);
        }
        if (modifierData.getName() != null) {
            imageData.setName(modifierData.getName());
        }
        if (modifierData.getHorizontalRel() != null) {
            imageData.setHorizontalRel(modifierData.getHorizontalRel());
        }
        if (modifierData.getHorizontalPos() != null) {
            imageData.setHorizontalPos(modifierData.getHorizontalPos());
        }
        if (modifierData.getVerticalRel() != null) {
            imageData.setVerticalRel(modifierData.getVerticalRel());
        }
        if (modifierData.getVerticalPos() != null) {
            imageData.setVerticalPos(modifierData.getVerticalPos());
        }
        if (modifierData.getWrap() != null) {
            imageData.setWrap(modifierData.getWrap());
        }

        imageData.exchangeImage(modifierData.getResource(), selection.getDocContainer().getDocument());

        if (modifierData.getDescr() != null) {
            imageData.setDesc(modifierData.getDescr());
        }

        Nodes.insertAfter(placeholderAncestor, imageData.getMainNode());
        Node previousNode = Problems.CANT_FIND_DOM_ELEMENT.get(Nodes.findPreviousNode(placeholderAncestor), "previous node");
        Nodes.deleteNode(placeholderAncestor);
        return ModifierNodeResult.FACTORY.success(previousNode, true);
    }
}
