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
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.datapicker.VarNameDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;

public class CounterCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {
    public CounterCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(new Regex("Counter"), modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        DataNode indexNode = new VarNameDataPicker().pickData(
                "index", dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
        if (indexNode != null && !indexNode.isNull() && indexNode.getType() == DataNode.DataNodeType.LEAF && indexNode.getValue() instanceof Integer) {
            int counter = ((Integer) indexNode.getValue()) + 1;
            modifiers.getReplaceWithTextModifier().modify(selection, String.valueOf(counter));
        } else {
            modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        }
        return CommandHandlerResult.EXECUTED_PROCEED;
    }


}
