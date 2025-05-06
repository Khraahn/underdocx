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
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.commands.image.types.*;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;
import org.underdocx.doctypes.odf.modifiers.images.createimage.CreateImageModifier;
import org.underdocx.doctypes.odf.modifiers.images.createimage.CreateImageModifierData;
import org.underdocx.doctypes.tools.datapicker.AttributeDataPicker;
import org.underdocx.doctypes.tools.datapicker.EnumDataPicker;
import org.underdocx.doctypes.tools.datapicker.IntegerDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class CreateImageCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractTextualCommandHandler<C, D> {

    private static final AttributeDataPicker<String> namePicker = new StringConvertDataPicker().optionalAttr("name");
    private static final AttributeDataPicker<String> descPicker = new StringConvertDataPicker().optionalAttr("desc");
    private static final AttributeDataPicker<Wrap> wrapPicker = new EnumDataPicker<>(Wrap.class).optionalAttr("wrap");
    private static final AttributeDataPicker<Anchor> anchorPicker = new EnumDataPicker<>(Anchor.class).expectedAttr("anchor");
    private static final AttributeDataPicker<HorizontalPos> horizontalPosPicker = new EnumDataPicker<>(HorizontalPos.class).optionalAttr("horizontalPos");
    private static final AttributeDataPicker<HorizontalRel> horizontalRelPicker = new EnumDataPicker<>(HorizontalRel.class).optionalAttr("horizontalRel");
    private static final AttributeDataPicker<VerticalPos> verticalPosPicker = new EnumDataPicker<>(VerticalPos.class).optionalAttr("verticalPos");
    private static final AttributeDataPicker<VerticalRel> verticalRelPicker = new EnumDataPicker<>(VerticalRel.class).optionalAttr("verticalRel");
    private static final AttributeDataPicker<OdfLengthUnit> unitPicker = new EnumDataPicker<>(OdfLengthUnit.class).optionalAttr("unit");
    private static final AttributeDataPicker<Integer> xPicker = new IntegerDataPicker().optionalAttr("x");
    private static final AttributeDataPicker<Integer> yPicker = new IntegerDataPicker().optionalAttr("y");
    private static final AttributeDataPicker<Integer> widthPicker = new IntegerDataPicker().optionalAttr("width");
    private static final AttributeDataPicker<Integer> heightPicker = new IntegerDataPicker().optionalAttr("height");

    private final CreateImageModifier<C, D> modifier;

    public CreateImageCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("CreateImage", modifiersProvider);
        modifier = new CreateImageModifier<>(modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        Resource resource = new ResourceCommandModule<C, ParametersPlaceholderData, D>(placeholderData.getJson()).execute(selection);
        CreateImageModifierData data = new CreateImageModifierData.Simple(
                resource,
                getAttr(namePicker),
                getAttr(descPicker),
                getAttr(wrapPicker),
                getAttr(anchorPicker),
                getAttr(horizontalPosPicker),
                getAttr(horizontalRelPicker),
                getAttr(verticalPosPicker),
                getAttr(verticalRelPicker),
                getAttr(unitPicker),
                getAttr(xPicker),
                getAttr(yPicker),
                getAttr(widthPicker),
                getAttr(heightPicker));
        return CommandHandlerResult.FACTORY.convert(modifier.modify(selection, data));
    }
}
