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
import org.underdocx.doctypes.odf.commands.image.NewImageData;
import org.underdocx.doctypes.odf.commands.image.NewMainImageData;
import org.underdocx.doctypes.odf.modifiers.backgroundimage.OdfBackgroundImageModifier;
import org.underdocx.doctypes.odf.modifiers.backgroundimage.OdfBackgroundImageModifierData;
import org.underdocx.doctypes.odf.modifiers.existingimage.OdfExistingImageModifier;
import org.underdocx.doctypes.odf.modifiers.existingimage.OdfExistingImageModifierData;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;


/**
 * Implements the ${If} command. Is responsible for key "If" and "EndIf".
 * It uses the {@link ConditionAttributeInterpreter} to evaluate the condition
 * It uses the {@link IfModifier} to manipulate the DOM
 */
public class ImageCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractCommandHandler<C, NewImageData, D> {
    public static final String NAME_ATTR = "name";
    public static final String DESC_ATTR = "desc";
    public static final String KEEP_WIDTH_ATTR = "keepWidth";

    private static final PredefinedDataPicker<String> namePicker = new StringConvertDataPicker().asPredefined(NAME_ATTR);
    private static final PredefinedDataPicker<String> descPicker = new StringConvertDataPicker().asPredefined(DESC_ATTR);
    private static final PredefinedDataPicker<Boolean> keepWidthPicker = new BooleanDataPicker().asPredefined(KEEP_WIDTH_ATTR);

    public ImageCommandHandler(ModifiersProvider modifiers) {
        super(modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        String name = namePicker.expect(dataAccess, placeholderData.getJson());
        String newDesc = descPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        Resource resource = new ResourceCommandModule<C, NewImageData, D>(placeholderData.getJson()).execute(selection);
        if (placeholderData instanceof NewMainImageData) {
            handleMainImage(name, newDesc, resource);
        } else {
            handleBackgroundImage(name, newDesc, resource);
        }
        return CommandHandlerResult.EXECUTED_PROCEED;
    }


    private void handleMainImage(String name, String newDesc, Resource resource) {
        Boolean keepWidth = keepWidthPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        OdfExistingImageModifierData modifierData = new OdfExistingImageModifierData.Simple(keepWidth, resource, name, newDesc);
        new OdfExistingImageModifier<C, D>().modify(selection, modifierData);
    }


    private void handleBackgroundImage(String name, String newDesc, Resource resource) {
        OdfBackgroundImageModifierData modifierData = new OdfBackgroundImageModifierData.Simple(resource, name, newDesc);
        new OdfBackgroundImageModifier<C, D>().modify(selection, modifierData);
    }

}
