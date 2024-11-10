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

package de.underdocx.enginelayers.defaultodtengine.commands;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.ReplaceWithTextModifier;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.modelengine.MSelection;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Regex;

import java.util.Optional;

public class ShortModelStringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public final static Regex KEYS = new Regex("@\\S*");

    public ShortModelStringCommandHandler() {
        super(KEYS);
    }

    protected CommandHandlerResult tryExecuteTextualCommand() {
        String pathStr = placeholderData.getKey().substring(1);
        Pair<String, Optional<ModelNode>> modelData = modelAccess.interpret(pathStr, false);
        Optional<ModelNode> optionalModelNode = modelData.right;
        writeModelNode(selection, optionalModelNode);
        return CommandHandlerResult.EXECUTED;
    }

    public static <C extends DocContainer<D>, D> void writeModelNode(MSelection<C, ParametersPlaceholderData, D> selection, Optional<ModelNode> optionalModelNode) {
        if (optionalModelNode.isEmpty()) {
            DeletePlaceholderModifier.modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        } else {
            ModelNode modelNode = optionalModelNode.get();
            if (modelNode.getType() == ModelNode.ModelNodeType.LEAF) {
                new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, String.valueOf(modelNode.getValue()));
            } else {
                DeletePlaceholderModifier.modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
            }
        }
    }

}
