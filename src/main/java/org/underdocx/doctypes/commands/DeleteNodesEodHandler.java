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

import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractCommandHandler;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.EngineListener;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.Optional;

public class DeleteNodesEodHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> implements EngineListener<C, D> {

    public DeleteNodesEodHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    public void init(C container, EngineAccess<C, D> engineAccess) {
        engineAccess.addListener(this);
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        return CommandHandlerResult.IGNORED;
    }

    @Override
    public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
        engineAccess.getToolkit(StringCommandHandler.class).ifPresent(uToolkit -> {
            TextualPlaceholderToolkit<ParametersPlaceholderData> toolkit = (TextualPlaceholderToolkit<ParametersPlaceholderData>) uToolkit;
            engineAccess.lookBack(node -> {
                Optional<ParametersPlaceholderData> placeholderData = toolkit.examineNode(node);
                if (placeholderData.isEmpty()) return false;
                return AbstractTextualCommandHandler.isMarkedForEodDeletion(placeholderData.get());
            }).forEach(placeholderNode -> modifiers.getDeletePlaceholderModifier().modify(placeholderNode, DeletePlaceholderModifierData.DEFAULT));
        });

    }

}
