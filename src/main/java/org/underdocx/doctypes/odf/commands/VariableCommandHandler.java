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

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;


public class VariableCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY_POP = "Pop";
    public static final String KEY_PUSH = "Push";
    public static final Regex KEYS = new Regex(KEY_POP + "|" + KEY_PUSH);

    public static final String KEY_ATTR = "key";
    public static final String VALUE_ATTR = "value";

    public VariableCommandHandler() {
        super(KEYS);
    }

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
        return Convenience.build(CommandHandlerResult.EXECUTED_PROCEED, result ->
                resolveStringByAttr(KEY_ATTR).ifPresent(key ->
                        resolveNodeByAttr(VALUE_ATTR).ifPresent(modelValue ->
                                dataAccess.pushVariable(key, modelValue))));
    }

    private CommandHandlerResult handlePopCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED_PROCEED, result ->
                resolveStringByAttr(KEY_ATTR).ifPresent(key ->
                        dataAccess.popVariable(key)));
    }
}
