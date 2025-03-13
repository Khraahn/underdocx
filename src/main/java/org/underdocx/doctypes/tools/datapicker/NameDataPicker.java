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

import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessType;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessTypeNameInterpreter;
import org.underdocx.enginelayers.modelengine.data.DataNode;


/**
 * A {@link DataPicker} that uses the provided name to lookup in model or variable registry.
 * The names prefix character is used to determine the {@link AccessType} by using the {@link AccessTypeNameInterpreter}
 * JSON attributes are ignored when "$" or "*" are used as name prefix, when no prefix is used the
 * attribute value of the attribute with the same name is returned, e.g.:
 * pickData("$x") returns the value of the registered variable "x". All JSON attributes are ignored
 * pickData("*x") returns the model value of the path "x". All JSON attributes are ignored
 * pickData("x") return the value of the JSON attribute "x".
 */
public class NameDataPicker extends AbstractDataPicker<DataNode, String> {

    public NameDataPicker() {
        super(new AccessTypeNameInterpreter(), null);
    }

    @Override
    protected DataPickerResult<DataNode> pickData(String name) {
        AccessType accessType = typeInterpreter.interpretAttributes(attributes, name);
        String pureName = AccessType.getPureName(name);
        return switch (accessType) {
            case ACCESS_CURRENT_MODEL_NODE, ACCESS_MODEL_BY_NAME ->
                    new ModelNameDataPicker().pickData(pureName, model, attributes);
            case ACCESS_ATTR_VALUE -> new AttributeValueDataPicker(typeInterpreter).pickData(name, model, attributes);
            case ACCESS_VARIABLE_BY_NAME -> new VarNameDataPicker().pickData(pureName, model, attributes);
            case MISSING_ACCESS -> DataPickerResult.unresolvedMissingAttr(DataPickerResult.ResultSource.UNKNOWN);
        };
    }
}
