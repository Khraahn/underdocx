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

package de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.single;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AbstractAttributeInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeInterpreter;

import java.util.Optional;
import java.util.function.BiFunction;

public class SinglePropertyInterpreter<R> extends AbstractAttributeInterpreter<Optional<R>, String> {

    private final boolean ignoreAccessType;
    private final BiFunction<JsonNode, String, Optional<R>> function;

    SinglePropertyInterpreter(boolean ignoreAccessType, BiFunction<JsonNode, String, Optional<R>> function) {
        this.ignoreAccessType = ignoreAccessType;
        this.function = function;
    }

    SinglePropertyInterpreter(BiFunction<JsonNode, String, Optional<R>> function) {
        this(true, function);
    }

    @Override
    protected Optional<R> interpretAttributes() {
        if (ignoreAccessType) {
            return function.apply(attributes, AccessTypeInterpreter.DEFAULT.interpretAttributes(attributes, configuration).rename(configuration));
        } else {
            return function.apply(attributes, configuration);
        }
    }
}
