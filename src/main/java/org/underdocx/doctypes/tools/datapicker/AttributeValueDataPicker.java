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

package org.underdocx.doctypes.tools.datapicker;

import org.underdocx.doctypes.tools.attrinterpreter.AttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessType;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.util.Optional;

public class AttributeValueDataPicker extends AbstractDataPicker<DataNode<?>, DataNode<?>> {

    public AttributeValueDataPicker() {
        this(new AccessTypeJsonNameInterpreter(), AttributeInterpreterFactory.createModelNodeAttributeInterpreter(true));
    }

    public AttributeValueDataPicker(AttributesInterpreter<AccessType, String> typeInterpreter) {
        this(typeInterpreter, AttributeInterpreterFactory.createModelNodeAttributeInterpreter(true));
    }

    public AttributeValueDataPicker(
            AttributesInterpreter<AccessType, String> typeInterpreter,
            AttributesInterpreter<Optional<DataNode<?>>, String> attributeInterpreter
    ) {
        super(typeInterpreter, attributeInterpreter);
    }

    @Override
    protected DataPickerResult<DataNode<?>> pickData(String name) {
        AccessType type = typeInterpreter.interpretAttributes(attributes, name);
        if (type != AccessType.ACCESS_ATTR_VALUE) {
            return DataPickerResult.unresolvedMissingAttr(DataPickerResult.ResultSource.ATTR_VALUE);
        }
        String attrName = type.rename(name);
        Optional<DataNode<?>> oNode = attributeInterpreter.interpretAttributes(attributes, attrName);
        if (oNode.isEmpty()) {
            return DataPickerResult.unresolvedInvalidAttrValue(DataPickerResult.ResultSource.ATTR_VALUE);
        } else {
            return DataPickerResult.resolvedAttrValue(oNode.get());
        }
    }


}
