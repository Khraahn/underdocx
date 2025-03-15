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

import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.internal.AbstractCommandHandler;
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.modelengine.internal.Node2MSelection;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MultiCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    public MultiCommandHandler(ModifiersProvider modifiers) {
        super(modifiers);
    }

    private List<MCommandHandler<C, ParametersPlaceholderData, D>> subCommandHandlerRegistry = new ArrayList<>();

    private void registerSubCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> handler) {
        subCommandHandlerRegistry.add(handler);
    }

    public void registerStringReplacement(String key, String replacement) {
        registerSubCommandHandler(new SimpleKey2StringCommandHandler<>(new Regex(Pattern.quote(key)), replacement, modifiers));
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        CommandHandlerResult result = trySelection(selection);
        if (result != CommandHandlerResult.IGNORED) {
            List<SelectedNode<?>> allWaiting = selection.getEngineAccess().lookAhead(null);
            for (SelectedNode<?> waiting : allWaiting) {
                if (waiting.getPlaceholderData() instanceof ParametersPlaceholderData) {
                    MSelection<C, ParametersPlaceholderData, D> newSelection = Node2MSelection.createMSelection(selection, (SelectedNode<ParametersPlaceholderData>) waiting);
                    trySelection(newSelection);
                }
            }
        }
        return result;
    }


    protected CommandHandlerResult trySelection(MSelection selection) {
        for (MCommandHandler<C, ParametersPlaceholderData, D> handler : subCommandHandlerRegistry) {
            CommandHandlerResult result = handler.tryExecuteCommand(selection);
            if (result != CommandHandlerResult.IGNORED) {
                return result;
            }
        }
        return CommandHandlerResult.IGNORED;
    }

    public static class SimpleKey2StringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

        private final String replacement;

        protected SimpleKey2StringCommandHandler(Regex keys, String replacement, ModifiersProvider modifiers) {
            super(keys, modifiers);
            this.replacement = replacement;
        }

        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            selection.getPlaceholderToolkit().ifPresent(toolkit -> {
                toolkit.replacePlaceholderWithText(selection.getNode(), replacement);
            });
            return CommandHandlerResult.FACTORY.startAtNode(selection.getNode());
        }
    }
}
