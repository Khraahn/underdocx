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
import org.underdocx.enginelayers.odtengine.modifiers.tablecell.TableCellModifier;
import org.underdocx.enginelayers.odtengine.modifiers.tablecell.TableCellModifierData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    private static String DEFAULT_FORMAT_DATE = "yyyy-MM-dd";

    private static PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    private static PredefinedDataPicker<String> outputFormatDataPicker = new StringConvertDataPicker().asPredefined("outputFormat");
    private static PredefinedDataPicker<String> inputFormatDataPicker = new StringConvertDataPicker().asPredefined("inputFormat");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");
    private static final PredefinedDataPicker<String> templateCellPicker = new StringConvertDataPicker().asPredefined("templateCell");

    public DateCommandHandler() {
        super(new Regex("Date"));
    }

    private String getFormat(PredefinedDataPicker<String> picker) {
        return picker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(DEFAULT_FORMAT_DATE);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String inFormat = getFormat(inputFormatDataPicker);
        String outFormat = getFormat(outputFormatDataPicker);
        String dateStr = valueDataPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
        String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);

        DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(outFormat);
        DateTimeFormatter inFormatter = DateTimeFormatter.ofPattern(inFormat);
        if (langCode != null) {
            outFormatter = outFormatter.withLocale(Locale.forLanguageTag(langCode));
            inFormatter = inFormatter.withLocale(Locale.forLanguageTag(langCode));
        }
        LocalDate date = dateStr == null ? LocalDate.now() : LocalDate.parse(dateStr, inFormatter);
        String replaceString = date.format(outFormatter);
        new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, replaceString);
        templateCellPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().ifPresent(templateCell -> {
            new TableCellModifier<C, D>().modify(selection, new TableCellModifierData.Simple(date, templateCell));
        });
        return CommandHandlerResult.EXECUTED_PROCEED;
    }
}
