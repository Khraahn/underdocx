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

package de.underdocx.enginelayers.defaultodtengine.commands;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.modifiers.existingimage.ExistingImageModifier;
import de.underdocx.enginelayers.baseengine.modifiers.existingimage.ExistingImageModifierData;
import de.underdocx.enginelayers.defaultodtengine.commands.ifcondition.ConditionAttributeInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.image.ImagePlaceholderData;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.*;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.resource.ResourceCommandModule;
import de.underdocx.enginelayers.defaultodtengine.modifiers.ifmodifier.IfModifier;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Resource;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

/**
 * Implements the ${If} command. Is responsible for key "If" and "EndIf".
 * It uses the {@link ConditionAttributeInterpreter} to evaluate the condition
 * It uses the {@link IfModifier} to manipulate the DOM
 */
public class ImageCommandHandler extends AbstractCommandHandler<OdtContainer, ImagePlaceholderData, OdfTextDocument> {
    public static final String KEY = "Image";
    public static final String URI_ATTR = "uri";
    public static final String DATA_ATTR = "data";
    public static final String RESOURCE_ATTR = "resource";
    public static final String NAME_ATTR = "name";
    public static final String TITLE_ATTR = "title";
    public static final String KEEP_WIDTH_ATTR = "keepWidth";

    private static PredefinedDataPicker<String> namePicker = new StringConvertDataPicker().asPredefined(NAME_ATTR);
    private static PredefinedDataPicker<String> titlePicker = new StringConvertDataPicker().asPredefined(TITLE_ATTR);
    private static PredefinedDataPicker<String> uriPicker = new StringConvertDataPicker().asPredefined(URI_ATTR);
    private static PredefinedDataPicker<Resource> resourcePicker = new ResourceDataPicker().asPredefined(RESOURCE_ATTR);
    private static PredefinedDataPicker<byte[]> binaryPicker = new BinaryDataPicker().asPredefined(DATA_ATTR);
    private static PredefinedDataPicker<Boolean> keepWidthPicker = new BooleanDataPicker().asPredefined(KEEP_WIDTH_ATTR);


    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED, result -> {
            String name = namePicker.expect(modelAccess, placeholderData.getJson());
            String title = titlePicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
            Boolean keepWidth = keepWidthPicker.expect(modelAccess, placeholderData.getJson());
            Resource resource = new ResourceCommandModule<OdtContainer, ImagePlaceholderData, OdfTextDocument>(placeholderData.getJson()).execute(selection);
            ExistingImageModifierData modifierData = new ExistingImageModifierData.Simple(keepWidth, resource, name, title);
            new ExistingImageModifier<OdtContainer, ImagePlaceholderData, OdfTextDocument>().modify(selection, modifierData);
        });
    }


}
