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

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.datapicker.AttributeNodeDataPicker;
import org.underdocx.doctypes.tools.datapicker.OneOfDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.AbstractDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.DataTreeBuilder;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.environment.err.Problems;

import java.util.ArrayList;
import java.util.List;

public class ConcatCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private static final PredefinedDataPicker<DataNode<?>> value1DataPicker = new AttributeNodeDataPicker().asPredefined("a");
    private static final PredefinedDataPicker<DataNode<?>> value2DataPicker = new AttributeNodeDataPicker().asPredefined("b");
    private static final PredefinedDataPicker<DataNode<?>> value3DataPicker = new AttributeNodeDataPicker().asPredefined("c");
    private static final PredefinedDataPicker<DataNode<?>> value4DataPicker = new AttributeNodeDataPicker().asPredefined("d");
    private static final PredefinedDataPicker<DataNode<?>> value5DataPicker = new AttributeNodeDataPicker().asPredefined("e");
    private static final PredefinedDataPicker<String> keyDataPicker = new StringConvertDataPicker().asPredefined("key");
    private static final PredefinedDataPicker<String> typeDataPicker = new OneOfDataPicker<>("list", "string").asPredefined("type");


    public ConcatCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Concat", modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        List<DataNode<?>> collectedNodes = new ArrayList<>();
        value1DataPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(collectedNodes::add);
        value2DataPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(collectedNodes::add);
        value3DataPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(collectedNodes::add);
        value4DataPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(collectedNodes::add);
        value5DataPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(collectedNodes::add);
        String keyName = Problems.MISSING_VALUE.get(keyDataPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "key");
        String type = typeDataPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse("list");
        if (type.equals("list")) {
            DataTreeBuilder.DataNodeWrapper list = DataTreeBuilder.beginList();
            collectedNodes.forEach(listElement -> list.addNode(AbstractDataNode.ensureAbstractDataNode(listElement)));
            dataAccess.pushVariable(keyName, list.end().build());
        } else {
            StringBuilder b = new StringBuilder();
            collectedNodes.forEach(listElement -> b.append(listElement.getValue()));
            dataAccess.pushVariable(keyName, new LeafDataNode<>(b.toString()));
        }
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}
