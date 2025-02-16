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

package org.underdocx.enginelayers.defaultodtengine.commands;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Regex;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.modifiers.EngineListener;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.modelengine.modelpath.ActivePrefixModelPath;
import org.underdocx.enginelayers.modelengine.modelpath.ModelPath;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.util.Optional;
import java.util.regex.Pattern;

public class ModelCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D>
        implements EngineListener<C, D> {

    public static final String KEY = "Model";
    public static final Regex KEYS = new Regex(Pattern.quote(KEY));
    public static final String ATTR_INTERPRET = "interpret";
    public static final String ATTR_PREFIX = "activeModelPathPrefix";
    public static final String ATTR_VALUE = "value";


    public ModelCommandHandler() {
        super(KEYS);
    }

    public static ParametersPlaceholderData createPlaceholderData(String modelPath, String prefix) {
        ParametersPlaceholderData placeholder = new ParametersPlaceholderData.Simple(KEY);
        placeholder.addStringAttribute(ATTR_VALUE, modelPath);
        if (prefix != null) {
            placeholder.addStringAttribute(ATTR_PREFIX, prefix);
        }
        return placeholder;
    }


    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED_PROCEED, result -> {
            engineAccess.addListener(this);
            ModelPath newPath = Convenience.build(modelAccess.getCurrentModelPath(), p ->
                    resolveStringByAttr(ATTR_VALUE).ifPresentOrElse(
                            value -> p.value = setPath(value),
                            () -> resolveStringByAttr(ATTR_INTERPRET).ifPresent(interpret ->
                                    p.value = interpret(interpret))));
            newPath = prepareModelPathPrefix(newPath);
            modelAccess.setCurrentModelPath(newPath);
        });
    }

    private ModelPath prepareModelPathPrefix(ModelPath currentModelPath) {
        Optional<String> prefix = resolveStringByAttr(ATTR_PREFIX);
        ActivePrefixModelPath result = new ActivePrefixModelPath(prefix.orElse(null), currentModelPath);
        return result;
    }

    private ModelPath interpret(String interpretValue) {
        return Convenience.also(modelAccess.getCurrentModelPath(), result -> result.interpret(interpretValue));
    }

    private ModelPath setPath(String path) {
        return new ModelPath(path);
    }


    @Override
    public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
        engineAccess.lookBack(node -> {
            Optional<ParametersPlaceholderData> placeholderData = examineNode(node);
            return placeholderData.isPresent() && (placeholderData.get()).getKey().equals(KEY);
        }).forEach(placeholderNode -> DeletePlaceholderModifier.modify(placeholderNode, DeletePlaceholderModifierData.DEFAULT));
    }


}
