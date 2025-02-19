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

package org.underdocx.enginelayers.defaultodtengine.commands;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.types.Regex;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.modelengine.internal.Node2MSelection;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MultiCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    private List<MCommandHandler<C, ParametersPlaceholderData, D>> subCommandHandlerRegistry = new ArrayList<>();

    public void registerSubCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> handler) {
        subCommandHandlerRegistry.add(handler);
    }

    public void registerStringReplacement(Regex key, String replacement, boolean forceRescan) {
        registerSubCommandHandler(new SimpleKey2StringCommandHandler<>(key, replacement, forceRescan));
    }

    public void registerStringReplacement(String key, String replacement) {
        registerSubCommandHandler(new SimpleKey2StringCommandHandler<>(new Regex(Pattern.quote(key)), replacement, true));
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        CommandHandlerResult result = CommandHandlerResult.IGNORED;
        if (trySelection(selection) != CommandHandlerResult.IGNORED) {
            List<SelectedNode<?>> allWaiting = selection.getEngineAccess().lookAhead(null);
            for (SelectedNode<?> waiting : allWaiting) {
                if (waiting.getPlaceholderData() instanceof ParametersPlaceholderData) {
                    MSelection<C, ParametersPlaceholderData, D> newSelection = Node2MSelection.createMSelection(selection, (SelectedNode<ParametersPlaceholderData>) waiting);
                    CommandHandlerResult localResult = trySelection(newSelection);
                    result = combineResults(result, localResult);
                }
            }
        }
        return result;
    }

    private CommandHandlerResult combineResults(CommandHandlerResult last, CommandHandlerResult current) {
        return switch (current.getResultType()) {
            case IGNORED, EXECUTED_PROCEED -> last;
            case EXECUTED_FULL_RESCAN -> current;
            case EXECUTED_RESTART_AT_NODE -> switch (last.getResultType()) {
                case IGNORED, EXECUTED_PROCEED -> current;
                case EXECUTED_FULL_RESCAN -> last;
                case EXECUTED_RESTART_AT_NODE, EXECUTED_END_OF_DOC -> CommandHandlerResult.EXECUTED_FULL_RESCAN;
            };
            case EXECUTED_END_OF_DOC -> CommandHandlerResult.EXECUTED_FULL_RESCAN;
        };
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

    public class SimpleKey2StringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

        private final boolean forceRescan;
        private final String replacement;

        protected SimpleKey2StringCommandHandler(Regex keys, String replacement, boolean forceRescan) {
            super(keys);
            this.replacement = replacement;
            this.forceRescan = forceRescan;
        }

        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            selection.getPlaceholderToolkit().ifPresent(toolkit -> {
                toolkit.replacePlaceholderWithText(selection.getNode(), replacement);
            });
            if (forceRescan) return CommandHandlerResult.EXECUTED_FULL_RESCAN;
            else return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }
}
