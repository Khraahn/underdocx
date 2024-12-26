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
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.PredefinedAttributesInterpreter;
import de.underdocx.enginelayers.modelengine.model.ModelNode;

import java.util.List;
import java.util.Optional;

public class AttributeInterpreterFactory {

    public static AttributesInterpreter<Optional<String>, String> createStringAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getStringAttribute);
    }

    public static AttributesInterpreter<Optional<String>, String> createStringAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getStringAttribute);
    }

    public static AttributesInterpreter<Optional<Boolean>, String> createBooleanAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getBooleanAttribute);
    }

    public static AttributesInterpreter<Optional<Boolean>, String> createBooleanAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getBooleanAttribute);
    }

    public static AttributesInterpreter<Optional<Double>, String> createDoubleAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getDoubleAttribute);
    }

    public static AttributesInterpreter<Optional<Double>, String> createDoubleAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getDoubleAttribute);
    }

    public static AttributesInterpreter<Optional<Integer>, String> createIntegerAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getIntegerAttribute);
    }

    public static AttributesInterpreter<Optional<Integer>, String> createIntegerAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getIntegerAttribute);
    }

    public static AttributesInterpreter<Optional<JsonNode>, String> createJsonAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getComplexAttribute);
    }

    public static AttributesInterpreter<Optional<JsonNode>, String> createJsonAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getComplexAttribute);
    }

    public static AttributesInterpreter<Optional<ModelNode>, String> createModelNodeAttributeInterpreter(boolean ignoreAccessPrefix) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getModelNodeAttribute);
    }

    public static AttributesInterpreter<Optional<ModelNode>, String> createModelNodeAttributeInterpreter() {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getModelNodeAttribute);
    }

    public static PredefinedAttributesInterpreter<Optional<String>> createStringAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getStringAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<String>> createStringAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getStringAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Boolean>> createBooleanAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getBooleanAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Boolean>> createBooleanAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getBooleanAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Double>> createDoubleAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getDoubleAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Double>> createDoubleAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getDoubleAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Integer>> createIntegerAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getIntegerAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<Integer>> createIntegerAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getIntegerAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<JsonNode>> createJsonAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getComplexAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<JsonNode>> createJsonAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getComplexAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<List<String>>> createStringListAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getStringListAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<List<String>>> createStringListAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getStringListAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<List<Integer>>> createIntListAttributeInterpreter(boolean ignoreAccessPrefix, String property) {
        return new SinglePropertyInterpreter<>(ignoreAccessPrefix, AttributesInterpreter::getIntListAttribute).asPredefined(property);
    }

    public static PredefinedAttributesInterpreter<Optional<List<Integer>>> createIntListAttributeInterpreter(String property) {
        return new SinglePropertyInterpreter<>(AttributesInterpreter::getIntListAttribute).asPredefined(property);
    }
}
