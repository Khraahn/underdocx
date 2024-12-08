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

package de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

import static de.underdocx.tools.common.Convenience.*;

public interface AttributesInterpreter<R, C> {

    R interpretAttributes(JsonNode attributes, C configuration);

    default PredefinedAttributesInterpreter<R> asPredefined(C configuration) {
        return attributes -> AttributesInterpreter.this.interpretAttributes(attributes, configuration);
    }

    static Optional<String> getStringAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isTextual(), jp -> w.value = jp.asText())));
    }


    static Optional<JsonNode> getComplexAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null, jp -> w.value = jp)));
    }


    static Optional<Integer> getIntegerAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isInt(), jp -> w.value = jp.asInt())));
    }

    static Optional<Double> getDoubleAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isDouble(), jp -> w.value = jp.asDouble())));
    }

    static Optional<Boolean> getBooleanAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isBoolean(), jp -> w.value = jp.asBoolean())));
    }

    static boolean hasAttribute(JsonNode attributes, String property) {
        return attributes != null && attributes.has(property);
    }

    static boolean hasNotNullAttribute(JsonNode attributes, String property) {
        return attributes != null && attributes.hasNonNull(property);
    }
}
