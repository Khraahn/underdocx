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

package org.underdocx.enginelayers.odtengine.commands.internal;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.types.Regex;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.model.DataNode;
import org.underdocx.enginelayers.odtengine.commands.DeleteNodesEodHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.AttributeNodeDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problems;

import java.util.Optional;
import java.util.regex.Pattern;

public abstract class AbstractTextualCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    protected Regex allowedKeys;
    protected TextualPlaceholderToolkit<ParametersPlaceholderData> placeholderToolkit = null;

    protected AbstractTextualCommandHandler(Regex keys) {
        this.allowedKeys = keys;
    }

    protected AbstractTextualCommandHandler(String key) {
        this(new Regex(Pattern.compile(Pattern.quote(key))));
    }

    protected Optional<String> resolveStringByAttr(String attrName) {
        return new StringConvertDataPicker().pickData(attrName, dataAccess, placeholderData.getJson()).getOptionalValue();
    }

    protected Optional<DataNode> resolveNodeByAttr(String attrName) {
        return new AttributeNodeDataPicker().pickData(attrName, dataAccess, placeholderData.getJson()).getOptionalValue();
    }


    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        this.placeholderToolkit = Problems.MISSING_VALUE.get(selection.getPlaceholderToolkit(), "placeholderToolkit");
        if (allowedKeys == null || allowedKeys.matches(placeholderData.getKey())) {
            UnderdocxEnv.getInstance().logger.trace("CommandHandler " + this + " received node: " + selection.getNode());
            return tryExecuteTextualCommand();
        } else {
            return CommandHandlerResult.IGNORED;
        }
    }


    protected abstract CommandHandlerResult tryExecuteTextualCommand();
    
    protected void markForEodDeletion() {
        if (!DeleteNodesEodHandler.isMarkedForEodDeletion(placeholderData)) {
            DeleteNodesEodHandler.markForEodDeletion(placeholderData);
            selection.getPlaceholderToolkit().get().setPlaceholder(selection.getNode(), placeholderData);
        }
    }
}
