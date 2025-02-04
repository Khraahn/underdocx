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

package org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.stringoutput;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.modifiers.stringmodifier.MarkupTextModifier;
import org.underdocx.enginelayers.baseengine.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BooleanDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.AbstractCommandModule;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModule;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleConfig;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class StringOutputCommandModule<C extends DocContainer<D>, D> extends AbstractCommandModule<C, ParametersPlaceholderData, D, CommandHandler.CommandHandlerResult, MissingDataCommandModuleConfig<String>> {
    public static final String RESCAN = "rescan";
    public static final String MARKUP = "markup";
    private static PredefinedDataPicker<Boolean> rescanDataPicker = new BooleanDataPicker().asPredefined(RESCAN);
    private static PredefinedDataPicker<Boolean> markupDataPicker = new BooleanDataPicker().asPredefined(MARKUP);

    public StringOutputCommandModule(MissingDataCommandModuleConfig<String> configuration) {
        super(configuration);
    }

    @Override
    protected CommandHandler.CommandHandlerResult execute() {
        boolean shouldRescan = rescanDataPicker.pickData(selection.getModelAccess().get(), selection.getPlaceholderData().getJson()).getOptionalValue().orElse(false);
        boolean useMarkup = markupDataPicker.pickData(selection.getModelAccess().get(), selection.getPlaceholderData().getJson()).getOptionalValue().orElse(false);
        MissingDataCommandModule<C, D, String> module = new MissingDataCommandModule<>(configuration);
        MissingDataCommandModuleResult<String> moduleResult = module.execute(selection);
        return switch (moduleResult.resultType) {
            case VALUE_RECEIVED -> {
                if (useMarkup) {
                    new MarkupTextModifier().modify(selection, moduleResult.value);
                } else {
                    new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, moduleResult.value);
                }
                yield shouldRescan ? CommandHandler.CommandHandlerResult.EXECUTED_RESCAN_REQUIRED : CommandHandler.CommandHandlerResult.EXECUTED;
            }
            case STRATEGY_EXECUTED -> CommandHandler.CommandHandlerResult.EXECUTED;
            case SKIP -> CommandHandler.CommandHandlerResult.IGNORED;
        };
    }


}
