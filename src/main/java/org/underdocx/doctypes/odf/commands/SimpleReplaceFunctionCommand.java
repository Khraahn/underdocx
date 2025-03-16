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

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.Selection;

import java.util.Optional;
import java.util.function.Function;

import static org.underdocx.common.tools.Convenience.build;

public class SimpleReplaceFunctionCommand<C extends DocContainer<D>, D> implements CommandHandler<C, String, D> {

    private final Function<String, Optional<String>> replaceFunction;

    public SimpleReplaceFunctionCommand(Function<String, Optional<String>> replaceFunction) {
        this.replaceFunction = replaceFunction;
    }

    @Override
    public CommandHandlerResult tryExecuteCommand(Selection<C, String, D> selection) {
        return build(CommandHandlerResult.IGNORED, result ->
                replaceFunction.apply(selection.getPlaceholderData()).ifPresent(replacement ->
                        result.value = CommandHandlerResult.FACTORY.convert(new ReplaceWithTextModifier().modify(selection, replacement))
                ));
    }
}
