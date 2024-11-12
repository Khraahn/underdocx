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

package de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeInterpreter;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;

import java.util.Optional;

public class AttributeNodeDataPicker extends AbstractDataPicker<ModelNode, String> {

    public AttributeNodeDataPicker(ModelAccess model, JsonNode attributes) {
        this(model, attributes, new AccessTypeInterpreter());
    }

    public AttributeNodeDataPicker(
            ModelAccess model,
            JsonNode attributes,
            AttributesInterpreter<Optional<AccessType>, String> typeInterpreter
    ) {
        super(model, attributes, typeInterpreter, null);
    }

    @Override
    public DataPickerResult<ModelNode> pickData(String name) {
        AccessType accessType = typeInterpreter.interpretAttributes(attributes, name).orElse(AccessType.ATTR);
        return switch (accessType) {
            case ATTR -> new AttributeValueDataPicker(attributes).pickData(name);
            case MODEL -> new AttributeModelDataPicker(model, attributes).pickData(name);
            case VAR -> new AttributeVarDataPicker(model, attributes).pickData(name);
        };
    }
}
