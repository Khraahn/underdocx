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

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleResult;
import org.underdocx.doctypes.commands.internal.modifiermodule.stringoutput.StringOutputCommandModule;
import org.underdocx.doctypes.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.*;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

public class NumberCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY = "Number";

    private static final PredefinedDataPicker<String> formatPicker = new StringConvertDataPicker().asPredefined("format");
    private static final PredefinedDataPicker<String> intFormatPicker = new StringConvertDataPicker().asPredefined("intFormat");
    private static final PredefinedDataPicker<String> langPicker = new StringConvertDataPicker().asPredefined("lang");

    private static final PredefinedDataPicker<String> prefixPicker = new StringConvertDataPicker().asPredefined("prefix");
    private static final PredefinedDataPicker<String> suffixPicker = new StringConvertDataPicker().asPredefined("suffix");
    private static final PredefinedDataPicker<Number> multiplierPicker = new NumberDataPicker().asPredefined("multiplier");
    private static final PredefinedDataPicker<Number> summandPicker = new NumberDataPicker().asPredefined("summand");
    private static final PredefinedDataPicker<Boolean> useModifiedForCellPicker = new BooleanDataPicker().asPredefined("useModified");

    public static final String DEFAULT_FORMAT = "#,###.##";

    public NumberCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEY, modifiers);
    }


    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        StringOutputCommandModule<C, D> module = new StringOutputCommandModule<>(getConfig());
        Pair<CommandHandlerResult, MissingDataCommandModuleResult.MissingDataCommandModuleResultType> result = module.execute(selection);
        handleCell(result);
        return result.left;
    }

    protected void handleCell(Pair<CommandHandlerResult, MissingDataCommandModuleResult.MissingDataCommandModuleResultType> result) {
        // to handle by Odf subclass
    }

    protected StringOutputModuleConfig<C, D> getConfig() {
        return new StringOutputModuleConfig<>() {
            @Override
            public PredefinedDataPicker<String> getDataPicker() {
                return new NumberStringPicker().asPredefined("value");
            }

            @Override
            public ModifiersProvider<C, D> getModifiers() {
                return modifiers;
            }
        };
    }

    private class NumberStringPicker extends AbstractConvertDataPicker<String> {

        @Override
        protected Optional<String> convert(DataNode<?> node) {
            return Convenience.buildOptional(result -> new NumberPicker().convert(node).ifPresent(pair -> result.value = pair.left));
        }
    }

    public class NumberPicker extends AbstractConvertDataPicker<Pair<String, Number>> {

        @Override
        protected Optional<Pair<String, Number>> convert(DataNode<?> node) {
            return Convenience.buildOptional(result -> {
                String format = formatPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
                String intFormat = intFormatPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
                String langCode = langPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
                Number multiplier = multiplierPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
                Number summand = summandPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
                String prefix = prefixPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse("");
                String suffix = suffixPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse("");
                boolean useModified = useModifiedForCellPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(false);

                Number numberValue = null;
                Object foundValue = node.getValue();
                if (foundValue instanceof Long) {
                    numberValue = (Long) foundValue;
                } else if (foundValue instanceof Integer i) {
                    numberValue = Long.valueOf(i);
                } else if (foundValue instanceof Double d) {
                    numberValue = d;
                } else {
                    return;
                }

                if (multiplier != null) {
                    if (numberValue instanceof Double) {
                        numberValue = numberValue.doubleValue() * multiplier.doubleValue();
                    } else {
                        if (multiplier instanceof Double) {
                            numberValue = numberValue.doubleValue() * multiplier.doubleValue();
                        } else {
                            numberValue = numberValue.longValue() * multiplier.longValue();
                        }
                    }
                }

                if (summand != null) {
                    if (numberValue instanceof Double) {
                        numberValue = numberValue.doubleValue() + summand.doubleValue();
                    } else {
                        if (summand instanceof Double) {
                            numberValue = numberValue.doubleValue() + summand.doubleValue();
                        } else {
                            numberValue = numberValue.longValue() + summand.longValue();
                        }
                    }
                }

                String formatToUse = format == null ? DEFAULT_FORMAT : format;
                if (!(numberValue instanceof Double) && intFormat != null) {
                    formatToUse = intFormat;
                }
                Locale local = langCode == null ? Locale.getDefault() : Locale.forLanguageTag(langCode);
                DecimalFormat df = new DecimalFormat(formatToUse, new DecimalFormatSymbols(local));
                if (useModified) {
                    result.value = new Pair<>(prefix + df.format(numberValue) + suffix, numberValue);
                } else {
                    result.value = new Pair<>(prefix + df.format(numberValue) + suffix, (Number) foundValue);
                }
            });
        }
    }
}
