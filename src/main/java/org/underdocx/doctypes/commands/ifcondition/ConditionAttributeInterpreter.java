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

package org.underdocx.doctypes.commands.ifcondition;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.tools.attrinterpreter.AttributesInterpreter;

import java.util.function.Function;

/**
 * A {@link AttributesInterpreter} that uses the {@link ConditionASTAttributeInterpreter}
 * to build an object structure and uses
 * a valueProvider function to evaluate the condition
 */
public class ConditionAttributeInterpreter implements AttributesInterpreter<Boolean, Function<Pair<String, Object>, Integer>> {
    private ConditionASTAttributeInterpreter astAttributeInterpreter = new ConditionASTAttributeInterpreter();

    @Override
    public Boolean interpretAttributes(JsonNode attributes, Function<Pair<String, Object>, Integer> comparator) {
        return astAttributeInterpreter.interpretAttributes(attributes, null).eval(comparator);
    }
}
