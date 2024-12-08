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
        return AttributesInterpreter.getStringAttribute(attributes, property);
    }

    protected Optional<JsonNode> getComplexAttribute(String property) {
        return AttributesInterpreter.getComplexAttribute(attributes, property);
    }


    protected Optional<Integer> getIntegerAttribute(String property) {
        return AttributesInterpreter.getIntegerAttribute(attributes, property);
    }

    protected Optional<Double> getDoubleAttribute(String property) {
        return AttributesInterpreter.getDoubleAttribute(attributes, property);
    }

    protected Optional<Boolean> getBooleanAttribute(String property) {
        return AttributesInterpreter.getBooleanAttribute(attributes, property);
    }

    protected boolean hasAttribute(String property) {
        return AttributesInterpreter.hasAttribute(attributes, property);
    }

    protected boolean hasNotNullAttribute(String property) {
        return AttributesInterpreter.hasNotNullAttribute(attributes, property);
    }

}
