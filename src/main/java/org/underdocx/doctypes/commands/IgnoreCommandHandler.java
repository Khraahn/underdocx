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
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.ignore.IgnoreInstruction;
import org.underdocx.doctypes.commands.ignore.IgnoreStateHandler;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;

public class IgnoreCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private IgnoreStateHandler ignoreStateHandler = new IgnoreStateHandler();

    @Override
    public void init(C container, EngineAccess<C, D> engineAccess) {
        ignoreStateHandler.setIgnoring(false);
    }

    public IgnoreCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super(new Regex(".*"), modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        Pair<Boolean, IgnoreInstruction> shallExecute = ignoreStateHandler.interpretAndCheckExecution(selection.getPlaceholderData());
        if (shallExecute.left && shallExecute.right != IgnoreInstruction.UNKNOWN) {
            markForEodDeletion();
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
        return ignoreStateHandler.isIgnoring() ? CommandHandlerResult.EXECUTED_PROCEED : CommandHandlerResult.IGNORED;

    }

}
