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

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.types.Regex;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.enginelayers.odtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private static PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    private static PredefinedDataPicker<String> outputFormatDataPicker = new StringConvertDataPicker().asPredefined("outputFormat");
    private static PredefinedDataPicker<String> inputFormatDataPicker = new StringConvertDataPicker().asPredefined("inputFormat");

    public DateCommandHandler() {
        super(new Regex("Date"));
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String inFormat = inputFormatDataPicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().orElse("yyyy-MM-dd");
        String outFormat = outputFormatDataPicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().orElse("yyyy-MM-dd");
        String dateStr = valueDataPicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
        LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(inFormat));
        String result = date.format(DateTimeFormatter.ofPattern(outFormat));
        new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, result);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }
}
