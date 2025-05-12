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

import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.AttributeNodeDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;


public class VariableCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY_POP = "Pop";
    public static final String KEY_PUSH = "Push";
    public static final Regex KEYS = new Regex(KEY_POP + "|" + KEY_PUSH);

    public static final String KEY_ATTR = "key";
    public static final String VALUE_ATTR = "value";

    public VariableCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    private static final PredefinedDataPicker<String> keyAttr = new StringConvertDataPicker().asPredefined(KEY_ATTR);
    private static final PredefinedDataPicker<DataNode<?>> valueAttr = new AttributeNodeDataPicker().asPredefined(VALUE_ATTR);

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        markForEodDeletion();
        return switch (placeholderData.getKey()) {
            case "Pop" -> handlePopCommand();
            case "Push" -> handlePushCommand();
            default -> CommandHandlerResult.IGNORED;
        };
    }

    private CommandHandlerResult handlePushCommand() {
        String key = keyAttr.expect(dataAccess, placeholderData.getJson());
        DataNode<?> data = valueAttr.expect(dataAccess, placeholderData.getJson());
        dataAccess.pushVariable(key, data);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }

    private CommandHandlerResult handlePopCommand() {
        String key = keyAttr.expect(dataAccess, placeholderData.getJson());
        dataAccess.popVariable(key);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }
}
