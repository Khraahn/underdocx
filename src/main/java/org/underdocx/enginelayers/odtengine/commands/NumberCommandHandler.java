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
import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.model.DataNode;
import org.underdocx.enginelayers.odtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.AbstractConvertDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.modifiermodule.stringoutput.StringOutputCommandModule;
import org.underdocx.enginelayers.odtengine.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public class NumberCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY = "Number";

    private static final PredefinedDataPicker<String> formatPicker = new StringConvertDataPicker().asPredefined("format");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");

    public NumberCommandHandler() {
        super(KEY);
    }


    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        StringOutputCommandModule<C, D> module = new StringOutputCommandModule<>(getConfig());
        return module.execute(selection);
    }

    protected StringOutputModuleConfig getConfig() {
        return () -> new NumberPicker().asPredefined("value");
    }

    private class NumberPicker extends AbstractConvertDataPicker<String> {

        @Override
        protected Optional<String> convert(DataNode node) {
            return Convenience.buildOptional(result -> {
                String format = formatPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
                String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue().orElse(null);
                Double doubleValue = null;
                Long longValue = null;
                Object foundValue = node.getValue();
                if (foundValue instanceof Long) {
                    longValue = (Long) foundValue;
                } else if (foundValue instanceof Integer i) {
                    longValue = Long.valueOf(i);
                } else if (foundValue instanceof Double d) {
                    doubleValue = d;
                } else {
                    return;
                }

                String formatToUse = format == null ? "#,###.##" : format;
                Locale local = langCode == null ? Locale.getDefault() : Locale.forLanguageTag(langCode);
                DecimalFormat df = new DecimalFormat(formatToUse, new DecimalFormatSymbols(local));
                if (longValue != null) {
                    result.value = df.format(longValue);
                } else {
                    result.value = df.format(doubleValue);
                }
            });
        }
    }
}
