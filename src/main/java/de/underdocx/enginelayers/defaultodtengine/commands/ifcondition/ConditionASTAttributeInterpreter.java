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

package de.underdocx.enginelayers.defaultodtengine.commands.ifcondition;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.codec.JsonCodec;
import de.underdocx.enginelayers.defaultodtengine.commands.ifcondition.ast.*;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Convenience;

/**
 * Converts the if-related JSON attribute into an object structure that can be used to evaluate the expression
 */
public class ConditionASTAttributeInterpreter implements AttributesInterpreter<ConditionElement, Void> {


    @Override
    public ConditionElement interpretAttributes(JsonNode attributes, Void configuration) {
        Problems.INVALID_IF_CONDITION.check(attributes.fieldNames().hasNext(), "if condition property", null);
        String fieldName = attributes.fieldNames().next();
        JsonNode fieldValue = attributes.get(fieldName);
        ConditionElement conditionElement;
        switch (fieldName) {
            case "not" -> conditionElement = Convenience.also(new Not(), not -> appendChildren(fieldValue, not));
            case "and" -> conditionElement = Convenience.also(new And(), and -> appendChildren(fieldValue, and));
            case "or" -> conditionElement = Convenience.also(new Or(), or -> appendChildren(fieldValue, or));
            default -> conditionElement = new Comparison(fieldName, new JsonCodec().getAsObject(fieldValue));
        }
        return conditionElement;
    }


    private void appendChildren(JsonNode fieldValue, ConditionElement currentElement) {
        if (fieldValue.isArray()) {
            for (int i = 0; i < fieldValue.size(); i++) {
                currentElement.add(interpretAttributes(fieldValue.get(i), null));
            }
        } else if (fieldValue.isContainerNode()) {
            currentElement.add(interpretAttributes(fieldValue, null));
        }

    }
}
