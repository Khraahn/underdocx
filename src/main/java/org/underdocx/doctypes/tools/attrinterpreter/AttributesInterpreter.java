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

package org.underdocx.doctypes.tools.attrinterpreter;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.codec.JsonCodec;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.AbstractPredefinedDataNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.underdocx.common.tools.Convenience.*;

/**
 * Interprets one or multiple JSON attributes and returns an aggregated result object
 */
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

    static Optional<DataNode> getModelNodeAttribute(JsonNode attributes, String property) {
        Optional<JsonNode> attrValue = getComplexAttribute(attributes, property);
        if (attrValue.isPresent()) {
            JsonNode json = attrValue.get();
            Object obj = new JsonCodec().getAsObject(json);
            return Optional.of(AbstractPredefinedDataNode.createRootNode(obj));
        }
        return Optional.empty();
    }


    static Optional<Integer> getIntegerAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isInt(), jp -> w.value = jp.asInt())));
    }

    static Optional<Double> getDoubleAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isDouble(), jp -> w.value = jp.asDouble())));
    }

    static Optional<List<String>> getStringListAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isArray(), jp -> {
                    w.value = new ArrayList<>();
                    for (JsonNode node : jp) {
                        if (node.isTextual()) {
                            w.value.add(node.asText());
                        } else {
                            w.value = null;
                            break;
                        }
                    }
                })));
    }

    static Optional<List<Integer>> getIntListAttribute(JsonNode attributes, String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isArray(), jp -> {
                    w.value = new ArrayList<>();
                    for (JsonNode node : jp) {
                        if (node.isInt()) {
                            w.value.add(node.asInt());
                        } else {
                            w.value = null;
                            break;
                        }
                    }
                })));
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
