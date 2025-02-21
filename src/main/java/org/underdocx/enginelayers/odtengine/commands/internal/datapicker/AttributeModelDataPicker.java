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

package org.underdocx.enginelayers.odtengine.commands.internal.datapicker;

import org.underdocx.enginelayers.modelengine.model.ModelNode;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;

import java.util.Optional;

public class AttributeModelDataPicker extends AbstractDataPicker<ModelNode, String> {

    private ModelNameDataPicker nameDataPicker = new ModelNameDataPicker();

    public AttributeModelDataPicker() {
        this(new AccessTypeJsonNameInterpreter(), AttributeInterpreterFactory.createStringAttributeInterpreter(true));
    }

    public AttributeModelDataPicker(AttributesInterpreter<AccessType, String> typeInterpreter) {
        this(typeInterpreter, AttributeInterpreterFactory.createStringAttributeInterpreter(true));
    }

    public AttributeModelDataPicker(
            AttributesInterpreter<AccessType, String> typeInterpreter,
            AttributesInterpreter<Optional<String>, String> stringAttributeInterpreter
    ) {
        super(typeInterpreter, stringAttributeInterpreter);
    }

    @Override
    protected DataPickerResult<ModelNode> pickData(String name) {
        AccessType type = typeInterpreter.interpretAttributes(attributes, name);
        if (name == null) {
            if (type != AccessType.ACCESS_CURRENT_MODEL_NODE) {
                return DataPickerResult.unresolvedMissingAttr(DataPickerResult.ResultSource.MODEL);
            } else {
                return fetchModelNode("");
            }
        }
        String attrName = type.rename(name);
        Optional<String> attrValue = attributeInterpreter.interpretAttributes(attributes, attrName);
        if (attrValue.isEmpty()) {
            return DataPickerResult.unresolvedInvalidAttrValue(DataPickerResult.ResultSource.MODEL);
        }
        String modelName = attrValue.get();
        return fetchModelNode(modelName);
    }

    private DataPickerResult<ModelNode> fetchModelNode(String modelName) {
        return nameDataPicker.pickData(modelName, model, null);
    }
}
