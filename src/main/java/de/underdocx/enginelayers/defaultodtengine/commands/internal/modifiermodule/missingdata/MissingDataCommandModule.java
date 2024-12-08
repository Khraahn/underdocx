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

package de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import de.underdocx.enginelayers.baseengine.modifiers.stringmodifier.ReplaceWithTextModifier;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataAttributesInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataConfig;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataStrategy;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataSzenario;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.DataPickerResult;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.AbstractCommandModule;
import de.underdocx.enginelayers.defaultodtengine.modifiers.deletearea.DeleteAreaModifier;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.environment.UnderdocxExecutionException;
import de.underdocx.tools.common.Pair;
import org.w3c.dom.Node;

public class MissingDataCommandModule<C extends DocContainer<D>, D, M> extends AbstractCommandModule<C, ParametersPlaceholderData, D, MissingDataCommandModuleResult<M>, MissingDataCommandModuleConfig<M>> {

    private MissingDataAttributesInterpreter interpreter = new MissingDataAttributesInterpreter();

    public MissingDataCommandModule(MissingDataCommandModuleConfig configuration) {
        super(configuration);
    }

    @Override
    protected MissingDataCommandModuleResult<M> execute() {
        DataPickerResult<M> value = configuration.getDataPicker().pickData(
                selection.getModelAccess().orElse(null),
                selection.getPlaceholderData().getJson());

        MissingDataConfig config = interpreter.interpretAttributes(selection.getPlaceholderData().getJson(), configuration.getMissingDataConfig());
        return switch (value.type) {
            case RESOLVED -> handleResolved(config, value);
            case UNRESOLVED_MISSING_ATTR, UNRESOLVED_INVALID_ATTR_VALUE ->
                    react(config, config.getStrategy(MissingDataSzenario.ERROR));
            case UNRESOLVED_MISSING_VALUE -> react(config, config.getStrategy(MissingDataSzenario.NULL));
        };
    }

    private MissingDataCommandModuleResult<M> handleResolved(MissingDataConfig config, DataPickerResult<M> value) {
        if (configuration.getIsEmptyPredicate().test(value.value)) {
            return react(config, config.getStrategy(MissingDataSzenario.EMPTY));
        } else {
            return new MissingDataCommandModuleResult(value.value, value.source);
        }
    }

    private MissingDataCommandModuleResult<M> react(MissingDataConfig config, MissingDataStrategy strategy) {
        switch (strategy) {
            case SKIP ->
                    new MissingDataCommandModuleResult<>(MissingDataCommandModuleResult.MissingDataCommandModuleResultType.SKIP);
            case FAIL -> throw new UnderdocxExecutionException("Failed to resolve: " + selection.getNode());
            case EMPTY -> new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, "");
            case FALLBACK -> tryFallback(config);
            case DELETE_PLACEHOLDER_KEEP_PARAGRAPH -> new DeletePlaceholderModifier<C, ParametersPlaceholderData, D>()
                    .modify(selection, new DeletePlaceholderModifierData.Simple(
                            DeletePlaceholderModifierData.Strategy.KEEP_PARAGRAPH));
            case DELETE_PLACEHOLDER_DELETE_EMPTY_PARAGRAPH ->
                    new DeletePlaceholderModifier<C, ParametersPlaceholderData, D>()
                            .modify(selection, new DeletePlaceholderModifierData.Simple(
                                    DeletePlaceholderModifierData.Strategy.DELETE_BLANK_PARAGRAPH));
            case DELETE_PLACEHOLDER_DELETE_PARAGRAPH -> new DeletePlaceholderModifier<C, ParametersPlaceholderData, D>()
                    .modify(selection, new DeletePlaceholderModifierData.Simple(
                            DeletePlaceholderModifierData.Strategy.DELETE_PARAGRAPH, true));
            case DELETE_PLACEHOLDER_DELETE_AREA -> deleteAreaOdf();
            case KEEP_PLACEHOLDER -> {/* Nothing to do*/}
            default -> throw new UnderdocxExecutionException("Unexpected Strategy: " + strategy);
        }
        return new MissingDataCommandModuleResult<>(MissingDataCommandModuleResult.MissingDataCommandModuleResultType.STRATEGY_EXECUTED);
    }

    private void tryFallback(MissingDataConfig config) {
        String fallback = config.fallback;
        if (fallback == null) {
            throw new UnderdocxExecutionException("Fallback strategy failed, fallback missing or invalid");
        } else {
            new ReplaceWithTextModifier<C, ParametersPlaceholderData, D>().modify(selection, fallback);
        }
    }

    private void deleteAreaOdf() {
        Node endNode = configuration.getAreaEnd();
        DeleteAreaModifier.deleteArea(() -> new Pair<>(selection.getNode(), endNode));

    }
}
