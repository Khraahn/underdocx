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
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.datapath.ActivePrefixDataPath;
import org.underdocx.enginelayers.modelengine.datapath.DataPath;

import java.util.Optional;
import java.util.regex.Pattern;

public class ModelCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY = "Model";
    public static final Regex KEYS = new Regex(Pattern.quote(KEY));
    public static final String ATTR_INTERPRET = "interpret";
    public static final String ATTR_PREFIX = "activeModelPathPrefix";
    public static final String ATTR_VALUE = "value";

    public ModelCommandHandler(ModifiersProvider modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        markForEodDeletion();
        return Convenience.build(CommandHandlerResult.EXECUTED_PROCEED, result -> {
            DataPath newPath = Convenience.build(dataAccess.getCurrentModelPath(), p ->
                    resolveStringByAttr(ATTR_VALUE).ifPresentOrElse(
                            value -> p.value = setPath(value),
                            () -> resolveStringByAttr(ATTR_INTERPRET).ifPresent(interpret ->
                                    p.value = interpret(interpret))));
            newPath = prepareModelPathPrefix(newPath);
            dataAccess.setCurrentModelPath(newPath);
        });
    }

    private DataPath prepareModelPathPrefix(DataPath currentDataPath) {
        Optional<String> prefix = resolveStringByAttr(ATTR_PREFIX);
        ActivePrefixDataPath result = new ActivePrefixDataPath(prefix.orElse(null), currentDataPath);
        return result;
    }

    private DataPath interpret(String interpretValue) {
        return Convenience.also(dataAccess.getCurrentModelPath(), result -> result.interpret(interpretValue));
    }

    private DataPath setPath(String path) {
        return new DataPath(path);
    }

}
