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

import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private static final PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    private static final PredefinedDataPicker<String> outputFormatDataPicker = new StringConvertDataPicker().asPredefined("outputFormat");
    private static final PredefinedDataPicker<String> inputFormatDataPicker = new StringConvertDataPicker().asPredefined("inputFormat");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");

    public DateCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(new Regex("Date"), modifiers);
    }

    private String getFormat(PredefinedDataPicker<String> picker) {
        String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";
        return picker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(DEFAULT_FORMAT_DATE);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String inFormat = getFormat(inputFormatDataPicker);
        String outFormat = getFormat(outputFormatDataPicker);
        String dateStr = valueDataPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);

        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
        if (langCode != null) {
            outFormatter = outFormatter.withLocale(Locale.forLanguageTag(langCode));
            inFormatter = inFormatter.withLocale(Locale.forLanguageTag(langCode));
        }
        LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr, inFormatter);
        String replaceString = date.format(outFormatter);
        modifiers.getReplaceWithTextModifier().modify(selection, replaceString);
        handleCell(date);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }

    protected void handleCell(LocalDate date) {
        // subclass can add cell value handling
    }
}
