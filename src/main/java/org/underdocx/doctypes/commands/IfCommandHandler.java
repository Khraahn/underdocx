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

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.ifcondition.ConditionAttributeInterpreter;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.commands.internal.AreaTools;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.ifmodifier.IfModifier;
import org.underdocx.doctypes.modifiers.ifmodifier.IfModifierData;
import org.underdocx.doctypes.tools.datapicker.ExtendedDataPicker;
import org.underdocx.doctypes.tools.datapicker.NameDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

import java.util.Collection;

/**
 * Implements the ${If} command. Is responsible for key "If" and "EndIf".
 * It uses the {@link ConditionAttributeInterpreter} to evaluate the condition
 * It uses the {@link IfModifier} to manipulate the DOM
 */
public class IfCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {
    public static final String BEGIN_KEY = "If";
    public static final String END_KEY = "EndIf";
    public static final Regex KEYS = new Regex(BEGIN_KEY + "|" + END_KEY);

    private static final ExtendedDataPicker<DataNode<?>> dataPicker = new NameDataPicker();
    private static final ConditionAttributeInterpreter conditionInterpreter = new ConditionAttributeInterpreter();

    public IfCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {

        JsonNode attributes = placeholderData.getJson();
        Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>> area =
                Problems.INVALID_PLACEHOLDER_STRUCTURE.get(
                        AreaTools.findArea(selection.getEngineAccess().lookAhead(null), selection, END_KEY), END_KEY);
        boolean match = conditionInterpreter.interpretAttributes(attributes, valueResolver -> {
            String property = valueResolver.left;
            DataNode<?> foundNode = dataPicker.pickData(property, dataAccess, attributes).optional().orElse(null);
            Object toCompareWith = valueResolver.right;
            return compare(foundNode, toCompareWith);
        });
        ModifierNodeResult modiferResult = new IfModifier<>(modifiers).modify(selection,
                new IfModifierData.DefaultIfModifierData(
                        new Pair<>(area.left.getNode(), area.right.getNode()), match));

        return CommandHandlerResult.FACTORY.convert(modiferResult);
    }

    private int compare(DataNode<?> foundNode, Object toCompareWith) {
        // ${if $toCheck:null}
        if (toCompareWith == null) {
            return (foundNode == null || foundNode.isNull()) ? 0 : -1;
        }
        if (foundNode == null || foundNode.isNull()) {
            return -1;
        }
        // ${if $toCheck:[]}
        if (toCompareWith instanceof Collection<?> collection) {
            return ((collection.size() == 0 && foundNode.getType() == DataNode.DataNodeType.LIST && foundNode.getSize() == 0)) ? 0 : -1;
        }
        if (foundNode.getType() != DataNode.DataNodeType.LEAF) {
            return -1;
        }
        // ${if $toCheck:2}
        // ${if $toCheck:"unknown"}
        // ${if $toCheck:true}
        Object foundNodeValue = foundNode.getValue();
        if (foundNodeValue.getClass() == toCompareWith.getClass() && foundNodeValue instanceof Comparable<?>) {
            return ((Comparable) foundNodeValue).compareTo(toCompareWith);
        }
        return toCompareWith.equals(foundNodeValue) ? 0 : -1;
    }
}
