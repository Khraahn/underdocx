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
import org.underdocx.doctypes.commands.internal.modifiermodule.missingdata.MissingDataCommandModule;
import org.underdocx.doctypes.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleConfig;
import org.underdocx.doctypes.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleResult;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.IntegerDataPicker;
import org.underdocx.doctypes.tools.datapicker.ListDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class JoinCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public final static Regex KEYS = new Regex(Pattern.quote("Join"));

    private static final PredefinedDataPicker<String> separatorPicker = new StringConvertDataPicker().asPredefined("separator");
    private static final PredefinedDataPicker<String> lastSeparatorPicker = new StringConvertDataPicker().asPredefined("lastSeparator");
    private static final PredefinedDataPicker<Integer> limitPicker = new IntegerDataPicker().asPredefined("limit");
    private static final PredefinedDataPicker<String> truncatedPicker = new StringConvertDataPicker().asPredefined("truncated");


    private final MissingDataCommandModule<C, D, DataNode<?>> module = new MissingDataCommandModule<>(new Config());

    public JoinCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        MissingDataCommandModuleResult<DataNode<?>> moduleResult = module.execute(selection);
        return switch (moduleResult.resultType) {
            case VALUE_RECEIVED ->
                    CommandHandlerResult.FACTORY.convert(modifiers.getReplaceWithTextModifier().modify(selection, getText(moduleResult.value)));
            case STRATEGY_EXECUTED -> CommandHandlerResult.EXECUTED_PROCEED;
            case SKIP -> CommandHandlerResult.IGNORED;
        };
    }

    private String getText(DataNode<?> value) {
        String separator = separatorPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(", ");
        String lastSeparator = lastSeparatorPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(separator);
        int limit = limitPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(-1);
        String truncated = truncatedPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse("...");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < value.getSize(); i++) {
            DataNode<?> item = value.getProperty(i);
            if (item != null && !item.isNull()) {
                stringList.add(item.getValue().toString());
            }
        }

        StringBuilder b = new StringBuilder();
        String delim = "";
        String postfix = "";
        int max = limit > 0 ? Math.min(limit, stringList.size()) : stringList.size();
        if (max < stringList.size()) {
            postfix = truncated;
        }
        for (int i = 0; i < max; i++) {
            b.append(delim);
            b.append(stringList.get(i));
            delim = separator;
            if (i == stringList.size() - 2) {
                delim = lastSeparator;
            }
        }
        b.append(postfix);
        return b.toString();
    }


    private class Config implements MissingDataCommandModuleConfig<C, D, DataNode<?>> {
        @Override
        public PredefinedDataPicker<DataNode<?>> getDataPicker() {
            return new ListDataPicker().asPredefined("value");
        }

        @Override
        public Predicate<DataNode<?>> getIsEmptyPredicate() {
            return (DataNode<?> node) -> node.getSize() == 0;
        }

        @Override
        public ModifiersProvider<C, D> getModifiers() {
            return modifiers;
        }
    }
}


