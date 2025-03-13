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
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.ImagePlaceholderData;
import org.underdocx.doctypes.odf.commands.internal.AbstractCommandHandler;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.odf.modifiers.existingimage.ExistingImageModifier;
import org.underdocx.doctypes.odf.modifiers.existingimage.ExistingImageModifierData;
import org.underdocx.doctypes.odf.modifiers.ifmodifier.IfModifier;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;


/**
 * Implements the ${If} command. Is responsible for key "If" and "EndIf".
 * It uses the {@link org.underdocx.doctypes.odf.commands.ifcondition.ConditionAttributeInterpreter} to evaluate the condition
 * It uses the {@link IfModifier} to manipulate the DOM
 */
public class ImageCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractCommandHandler<C, ImagePlaceholderData, D> {
    public static final String NAME_ATTR = "name";
    public static final String DESC_ATTR = "desc";
    public static final String KEEP_WIDTH_ATTR = "keepWidth";

    private static final PredefinedDataPicker<String> namePicker = new StringConvertDataPicker().asPredefined(NAME_ATTR);
    private static final PredefinedDataPicker<String> descPicker = new StringConvertDataPicker().asPredefined(DESC_ATTR);
    private static final PredefinedDataPicker<Boolean> keepWidthPicker = new BooleanDataPicker().asPredefined(KEEP_WIDTH_ATTR);


    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED_PROCEED, result -> {
            String name = namePicker.expect(dataAccess, placeholderData.getJson());
            String newDesc = descPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
            Boolean keepWidth = keepWidthPicker.expect(dataAccess, placeholderData.getJson());
            Resource resource = new ResourceCommandModule<C, ImagePlaceholderData, D>(placeholderData.getJson()).execute(selection);
            ExistingImageModifierData modifierData = new ExistingImageModifierData.Simple(keepWidth, resource, name, newDesc);
            new ExistingImageModifier<C, ImagePlaceholderData, D>().modify(selection, modifierData);
        });
    }


}
