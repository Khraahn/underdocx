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

public abstract class AbstractAttributeInterpreter<R, C> implements AttributesInterpreter<R, C> {

    protected C configuration;
    protected JsonNode attributes;


    @Override
    public R interpretAttributes(JsonNode attributes, C configuration) {
        this.attributes = attributes;
        this.configuration = configuration;
        return interpretAttributes();
    }

    protected abstract R interpretAttributes();

    protected Optional<String> getStringAttribute(String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isTextual(), jp -> w.value = jp.asText())));
    }

    protected Optional<Integer> getIntegerAttribute(String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isInt(), jp -> w.value = jp.asInt())));
    }

    protected Optional<Double> getDoubleAttribute(String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isDouble(), jp -> w.value = jp.asDouble())));
    }

    protected Optional<Boolean> getBooleanAttribute(String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null && jp.isBoolean(), jp -> w.value = jp.asBoolean())));
    }

    protected Optional<JsonNode> getComplexAttribute(String property) {
        return buildOptional(w -> ifNotNull(attributes,
                json -> ifIs(json.get(property), jp -> jp != null, jp -> w.value = jp)));
    }

    protected boolean hasAttribute(String property) {
        return attributes != null && attributes.has(property);
    }

    protected boolean hasNotNullAttribute(String property) {
        return attributes != null && attributes.hasNonNull(property);
    }

}
