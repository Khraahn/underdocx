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
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {


    private static String DEFAULT_IN_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    private static String DEFAULT_OUT_FORMAT_TIME = "HH:mm:ss";

    private static PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    private static PredefinedDataPicker<String> outputFormatDataPicker = new StringConvertDataPicker().asPredefined("outputFormat");
    private static PredefinedDataPicker<String> inputFormatDataPicker = new StringConvertDataPicker().asPredefined("inputFormat");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");
    private static final PredefinedDataPicker<String> templateCellPicker = new StringConvertDataPicker().asPredefined("templateCell");

    public TimeCommandHandler(ModifiersProvider modifiers) {
        super(new Regex("Time"), modifiers);
    }

    private String getFormat(boolean isIn, PredefinedDataPicker<String> picker) {
        if (isIn) {
            return picker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(DEFAULT_IN_FORMAT_TIME);
        } else {
            return picker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(DEFAULT_OUT_FORMAT_TIME);
        }
    }


    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String inFormat = getFormat(true, inputFormatDataPicker);
        String outFormat = getFormat(false, outputFormatDataPicker);
        String dateStr = valueDataPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
        if (langCode != null) {
            outFormatter = outFormatter.withLocale(Locale.forLanguageTag(langCode));
            inFormatter = inFormatter.withLocale(Locale.forLanguageTag(langCode));
        }
        LocalDateTime time = dateStr == null ? LocalDateTime.now() : LocalDateTime.parse(dateStr, inFormatter);
        String replaceString = time.format(outFormatter);
        modifiers.getReplaceWithTextModifier().modify(selection, replaceString);
        handleCell(time);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }

    protected void handleCell(LocalDateTime time) {
    }
}
