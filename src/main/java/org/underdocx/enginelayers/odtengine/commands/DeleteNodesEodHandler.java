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

package org.underdocx.enginelayers.odtengine.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.modifiers.EngineListener;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.odtengine.commands.internal.AbstractCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import org.w3c.dom.Node;

import java.util.Optional;

public class DeleteNodesEodHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> implements EngineListener<C, D> {

    private static final String DELETE_ON_EOD_ATTR = "deleteOnEod";
    private static PredefinedAttributesInterpreter<Optional<Boolean>> attr = AttributeInterpreterFactory.createBooleanAttributeInterpreter(false).asPredefined(DELETE_ON_EOD_ATTR);

    @Override
    public void init(C container, EngineAccess engineAccess) {
        engineAccess.addListener(this);
    }

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        return CommandHandlerResult.IGNORED;
    }

    public static boolean isMarkedForEodDeletion(ParametersPlaceholderData data) {
        return attr.interpretAttributes(data.getJson()).orElse(false);
    }

    public static void markForEodDeletion(ParametersPlaceholderData data) {
        ((ObjectNode) data.getJson()).put(DELETE_ON_EOD_ATTR, true);
    }

    private Optional<ParametersPlaceholderData> examineNode(Node node) {
        return Convenience.buildOptional(w -> {
            if (node.getParentNode() != null && node.getOwnerDocument() != null) {
                String content = node.getTextContent();
                if (content != null) {
                    ParametersPlaceholderCodec.INSTANCE.tryParse(content).ifPresent(data -> w.value = data);
                }
            }
        });
    }

    @Override
    public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
        engineAccess.lookBack(node -> {
            Optional<ParametersPlaceholderData> placeholderData = examineNode(node);
            if (placeholderData.isEmpty()) return false;
            return isMarkedForEodDeletion(placeholderData.get());
        }).forEach(placeholderNode -> DeletePlaceholderModifier.modify(placeholderNode, DeletePlaceholderModifierData.DEFAULT));
    }

}
