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

package org.underdocx.doctypes.commands.internal.modifiermodule.missingdata;

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.modifiermodule.AbstractCommandModule;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.attrinterpreter.missingdata.MissingDataAttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.missingdata.MissingDataConfig;
import org.underdocx.doctypes.tools.attrinterpreter.missingdata.MissingDataStrategy;
import org.underdocx.doctypes.tools.attrinterpreter.missingdata.MissingDataSzenario;
import org.underdocx.doctypes.tools.datapicker.DataPickerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

public class MissingDataCommandModule<C extends DocContainer<D>, D, M> extends AbstractCommandModule<C, ParametersPlaceholderData, D, MissingDataCommandModuleResult<M>, MissingDataCommandModuleConfig<C, D, M>> {

    private final MissingDataAttributesInterpreter interpreter = new MissingDataAttributesInterpreter();

    public MissingDataCommandModule(MissingDataCommandModuleConfig<C, D, M> configuration) {
        super(configuration);
    }

    @Override
    protected MissingDataCommandModuleResult<M> execute() {
        DataPickerResult<M> value = configuration.getDataPicker().pickData(
                selection.getDataAccess().orElse(null),
                selection.getPlaceholderData().getJson());

        MissingDataConfig config = interpreter.interpretAttributes(selection.getPlaceholderData().getJson(), configuration.getMissingDataConfig());
        return switch (value.type) {
            case RESOLVED -> handleResolved(config, value);
            case UNRESOLVED_MISSING_ATTR, UNRESOLVED_INVALID_VALUE ->
                    react(config, config.getStrategy(MissingDataSzenario.ERROR));
            case UNRESOLVED_MISSING_VALUE -> react(config, config.getStrategy(MissingDataSzenario.NULL));
        };
    }

    private MissingDataCommandModuleResult<M> handleResolved(MissingDataConfig config, DataPickerResult<M> value) {
        if (value.value == null) {
            return react(config, config.getStrategy(MissingDataSzenario.NULL));
        }
        if (configuration.getIsEmptyPredicate().test(value.value)) {
            return react(config, config.getStrategy(MissingDataSzenario.EMPTY));
        } else {
            return new MissingDataCommandModuleResult<>(value.value, value.source);
        }
    }

    private MissingDataCommandModuleResult<M> react(MissingDataConfig config, MissingDataStrategy strategy) {
        switch (strategy) {
            case SKIP ->
                    new MissingDataCommandModuleResult<>(MissingDataCommandModuleResult.MissingDataCommandModuleResultType.SKIP);
            case FAIL -> Problems.MISSING_VALUE.fireProperty("" + selection.getNode());
            case EMPTY -> configuration.getModifiers().getReplaceWithTextModifier().modify(selection, "");
            case FALLBACK -> tryFallback(config);
            case DELETE_PLACEHOLDER_KEEP_PARAGRAPH -> configuration.getModifiers().getDeletePlaceholderModifier()
                    .modify(selection.getNode(), new DeletePlaceholderModifierData.Simple(
                            DeletePlaceholderModifierData.Strategy.KEEP_PARAGRAPH));
            case DELETE_PLACEHOLDER_DELETE_EMPTY_PARAGRAPH ->
                    configuration.getModifiers().getDeletePlaceholderModifier()
                            .modify(selection.getNode(), new DeletePlaceholderModifierData.Simple(
                                    DeletePlaceholderModifierData.Strategy.DELETE_BLANK_PARAGRAPH));
            case DELETE_PLACEHOLDER_DELETE_PARAGRAPH -> configuration.getModifiers().getDeletePlaceholderModifier()
                    .modify(selection.getNode(), new DeletePlaceholderModifierData.Simple(
                            DeletePlaceholderModifierData.Strategy.DELETE_PARAGRAPH, true));
            case KEEP_PLACEHOLDER -> {/* Nothing to do*/}
            default -> Problems.UNEXPECTED_TYPE_DETECTED.fireValue(strategy.name());
        }
        return new MissingDataCommandModuleResult<>(MissingDataCommandModuleResult.MissingDataCommandModuleResultType.STRATEGY_EXECUTED);
    }

    private void tryFallback(MissingDataConfig config) {
        String fallback = Problems.MISSING_VALUE.notNull(config.fallback, "fallback");
        configuration.getModifiers().getReplaceWithTextModifier().modify(selection, fallback);
    }

}
