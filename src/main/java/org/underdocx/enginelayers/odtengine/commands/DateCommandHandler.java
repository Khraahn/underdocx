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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private static String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
    private static String DEFAULT_IN_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    private static String DEFAULT_OUT_FORMAT_TIME = "HH:mm:ss";

    private static PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    private static PredefinedDataPicker<String> outputFormatDataPicker = new StringConvertDataPicker().asPredefined("outputFormat");
    private static PredefinedDataPicker<String> inputFormatDataPicker = new StringConvertDataPicker().asPredefined("inputFormat");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");

    public DateCommandHandler() {
        super(new Regex("Date|Time"));
    }

    private String getFormat(boolean isTime, boolean isIn, PredefinedDataPicker<String> picker) {
        if (isTime) {
            if (isIn) {
                return picker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(DEFAULT_IN_FORMAT_TIME);
            } else {
                return picker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(DEFAULT_OUT_FORMAT_TIME);
            }
        }
        return picker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(DEFAULT_FORMAT_DATE);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        boolean isTime = placeholderData.getKey().equals("Time");
        String inFormat = getFormat(isTime, true, inputFormatDataPicker);
        String outFormat = getFormat(isTime, false, outputFormatDataPicker);
        String dateStr = valueDataPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
        String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
        if (langCode != null) {
            outFormatter = outFormatter.withLocale(Locale.forLanguageTag(langCode));
            inFormatter = inFormatter.withLocale(Locale.forLanguageTag(langCode));
        }
        String replaceString;
        if (!isTime) {
            LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr, inFormatter);
            replaceString = date.format(outFormatter);
        } else {
            LocalDateTime time = dateStr == null ? LocalDateTime.now() : LocalDateTime.parse(dateStr, inFormatter);
            replaceString = time.format(outFormatter);
        }
        new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, replaceString);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }
}
