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

package org.underdocx.doctypes.commands;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.datapicker.AttributeDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.CustomEvent;

public class ReplaceCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private final static AttributeDataPicker<String> keyAttr = new StringConvertDataPicker().expectedAttr("key");
    private final static AttributeDataPicker<String> valueAttr = new StringConvertDataPicker().expectedAttr("value");

    public ReplaceCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Replace", modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        engineAccess.sendCustomEvent(
                new CustomEvent(MultiCommandHandler.EVENT_KEY_REGISTER_STRING_REPLACEMENT,
                        new Pair<>(getAttr(keyAttr), getAttr(valueAttr))));
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}
