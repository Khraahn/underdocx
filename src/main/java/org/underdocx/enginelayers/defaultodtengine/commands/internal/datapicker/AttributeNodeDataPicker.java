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

package org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker;

import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.enginelayers.modelengine.model.ModelNode;

/**
 * This is a {@link ExtendedDataPicker} that resolves a value from model, variable registry or from json attribute.
 * It analyses the prefix of the json attribute to find out the
 * source of the value (e.g. "value", "$value", "@value").
 * If source is model or variable registry, the attribute value is used as new name to look up for the value
 * Depending on used prefix this {@link DataPicker} calls {@link AttributeModelDataPicker},
 * {@link AttributeVarDataPicker} or {@link AttributeValueDataPicker}
 * <p>
 * Examples:
 * pick("x") and json x:"y" => returns y
 * pick("x") and json $x:"y" => returns value of registered variable y
 * pick("x") and json @x:"y" => returns value of model path y
 */
public class AttributeNodeDataPicker extends AbstractDataPicker<ModelNode, String> {

    public AttributeNodeDataPicker() {
        this(new AccessTypeJsonNameInterpreter());
    }

    public AttributeNodeDataPicker(
            AttributesInterpreter<AccessType, String> typeInterpreter
    ) {
        super(typeInterpreter, null);
    }

    @Override
    protected DataPickerResult<ModelNode> pickData(String name) {
        AccessType accessType = typeInterpreter.interpretAttributes(attributes, name);
        return switch (accessType) {
            case ACCESS_CURRENT_MODEL_NODE, ACCESS_MODEL_BY_NAME ->
                    new AttributeModelDataPicker(typeInterpreter).pickData(name, model, attributes);
            case ACCESS_ATTR_VALUE -> new AttributeValueDataPicker(typeInterpreter).pickData(name, model, attributes);
            case ACCESS_VARIABLE_BY_NAME ->
                    new AttributeVarDataPicker(typeInterpreter).pickData(name, model, attributes);
            case MISSING_ACCESS -> DataPickerResult.unresolvedMissingAttr(DataPickerResult.ResultSource.UNKNOWN);
        };
    }
}
