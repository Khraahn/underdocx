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

package org.underdocx.doctypes.odf.commands;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.commands.ifcondition.ConditionAttributeInterpreter;
import org.underdocx.doctypes.commands.internal.AbstractCommandHandler;
import org.underdocx.doctypes.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.ifmodifier.IfModifier;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.ImageData;
import org.underdocx.doctypes.odf.commands.image.MainImageData;
import org.underdocx.doctypes.odf.modifiers.images.backgroundimage.OdfBackgroundImageModifier;
import org.underdocx.doctypes.odf.modifiers.images.backgroundimage.OdfBackgroundImageModifierData;
import org.underdocx.doctypes.odf.modifiers.images.existingimage.OdfExistingImageModifier;
import org.underdocx.doctypes.odf.modifiers.images.existingimage.OdfExistingImageModifierData;
import org.underdocx.doctypes.tools.datapicker.AttributeDataPicker;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;


/**
 * Implements the ${If} command. Is responsible for key "If" and "EndIf".
 * It uses the {@link ConditionAttributeInterpreter} to evaluate the condition
 * It uses the {@link IfModifier} to manipulate the DOM
 */
public class ImageCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractCommandHandler<C, ImageData, D> {
    public static final String NAME_ATTR = "name";
    public static final String DESC_ATTR = "desc";
    public static final String KEEP_WIDTH_ATTR = "keepWidth";

    private static final AttributeDataPicker<String> namePicker = new StringConvertDataPicker().optionalAttr(NAME_ATTR);
    private static final AttributeDataPicker<String> descPicker = new StringConvertDataPicker().optionalAttr(DESC_ATTR);
    private static final AttributeDataPicker<Boolean> keepWidthPicker = new BooleanDataPicker().optionalAttr(KEEP_WIDTH_ATTR);

    private final OdfExistingImageModifier<C, D> existingImageModifier = new OdfExistingImageModifier<>();
    private final OdfBackgroundImageModifier<C, D> backgroundImageModifier = new OdfBackgroundImageModifier<>();

    public ImageCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        String name = namePicker.get(dataAccess, placeholderData.getJson());
        String newDesc = descPicker.get(dataAccess, placeholderData.getJson());
        Resource resource = new ResourceCommandModule<C, ImageData, D>(placeholderData.getJson()).execute(selection);
        if (placeholderData instanceof MainImageData) {
            handleMainImage(name, newDesc, resource);
        } else {
            handleBackgroundImage(name, newDesc, resource);
        }
        return CommandHandlerResult.EXECUTED_PROCEED;
    }


    private void handleMainImage(String name, String newDesc, Resource resource) {
        Boolean keepWidth = keepWidthPicker.get(dataAccess, placeholderData.getJson());
        OdfExistingImageModifierData modifierData = new OdfExistingImageModifierData.Simple(keepWidth, resource, name, newDesc);
        existingImageModifier.modify(selection, modifierData);
    }


    private void handleBackgroundImage(String name, String newDesc, Resource resource) {
        OdfBackgroundImageModifierData modifierData = new OdfBackgroundImageModifierData.Simple(resource, name, newDesc);
        backgroundImageModifier.modify(selection, modifierData);
    }

}
