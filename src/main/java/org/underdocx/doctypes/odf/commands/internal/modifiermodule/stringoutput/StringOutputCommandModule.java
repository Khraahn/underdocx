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

package org.underdocx.doctypes.odf.commands.internal.modifiermodule.stringoutput;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.AbstractCommandModule;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.missingdata.MissingDataCommandModule;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleConfig;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleResult;
import org.underdocx.doctypes.tools.datapicker.*;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class StringOutputCommandModule<C extends DocContainer<D>, D> extends AbstractCommandModule<C, ParametersPlaceholderData, D, Pair<CommandHandlerResult, MissingDataCommandModuleResult.MissingDataCommandModuleResultType>, MissingDataCommandModuleConfig<C, D, String>> {
    public static final String RESCAN = "rescan";
    public static final String MARKUP = "markup";
    private static final PredefinedDataPicker<Boolean> rescanDataPicker = new BooleanDataPicker().asPredefined(RESCAN);
    private static final PredefinedDataPicker<Boolean> markupDataPicker = new BooleanDataPicker().asPredefined(MARKUP);
    private static final PredefinedDataPicker<String> alignPicker = new OneOfDataPicker<>("left", "right").asPredefined("align");
    private static final PredefinedDataPicker<Integer> paddingPicker = new IntegerDataPicker().asPredefined("padding");
    private static final PredefinedDataPicker<String> truncatedPicker = new StringConvertDataPicker().asPredefined("truncated");


    public StringOutputCommandModule(MissingDataCommandModuleConfig<C, D, String> configuration) {
        super(configuration);
    }

    @Override
    protected Pair<CommandHandlerResult, MissingDataCommandModuleResult.MissingDataCommandModuleResultType> execute() {
        boolean shouldRescan = rescanDataPicker.pickData(dataAccess, placholderData.getJson()).getOptionalValue().orElse(false);
        boolean useMarkup = markupDataPicker.pickData(dataAccess, placholderData.getJson()).getOptionalValue().orElse(false);
        MissingDataCommandModule<C, D, String> module = new MissingDataCommandModule<>(configuration);
        MissingDataCommandModuleResult<String> moduleResult = module.execute(selection);
        return switch (moduleResult.resultType) {
            case VALUE_RECEIVED -> {
                if (useMarkup) {
                    configuration.getModifiers().getMarkupTextModifier().modify(selection, moduleResult.value);
                } else {
                    String output = handlePadding(moduleResult.value);
                    configuration.getModifiers().getReplaceWithTextModifier().modify(selection, output);
                }
                yield shouldRescan
                        ? new Pair<>(CommandHandlerResult.FACTORY.startAtNode(selection.getNode()), moduleResult.resultType)
                        : new Pair<>(CommandHandlerResult.EXECUTED_PROCEED, moduleResult.resultType);
            }
            case STRATEGY_EXECUTED -> new Pair<>(CommandHandlerResult.EXECUTED_PROCEED, moduleResult.resultType);
            case SKIP -> new Pair<>(CommandHandlerResult.IGNORED, moduleResult.resultType);
        };
    }

    private String handlePadding(String input) {
        String output = input;
        Integer padding = paddingPicker.pickData(dataAccess, placholderData.getJson()).getOptionalValue().orElse(null);
        if (padding != null) {
            String align = alignPicker.pickData(dataAccess, placholderData.getJson()).getOptionalValue().orElse("left");
            if (align.equals("left")) {
                output = String.format("%-" + padding + "s", output);
            } else {
                output = String.format("%-" + padding + "s", output);
            }
            String truncated = truncatedPicker.pickData(dataAccess, placholderData.getJson()).getOptionalValue().orElse(null);
            if (truncated != null && output.length() > padding) {
                int maxLength = padding - truncated.length();
                output = output.substring(0, maxLength) + truncated;
            }
        }
        return output;
    }

}
