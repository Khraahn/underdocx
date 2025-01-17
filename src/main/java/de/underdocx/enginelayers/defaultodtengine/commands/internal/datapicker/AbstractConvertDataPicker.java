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
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;
import de.underdocx.tools.common.Convenience;

import java.util.Optional;

/**
 * An abstract implementation of {@link ExtendedDataPicker} that tries to convert a {@link ModelNode} to requested type
 */
public abstract class AbstractConvertDataPicker<T> implements ExtendedDataPicker<T> {

    private final ExtendedDataPicker<ModelNode> dataPicker;

    public AbstractConvertDataPicker() {
        this(new AttributeNodeDataPicker());
    }

    public AbstractConvertDataPicker(ExtendedDataPicker<ModelNode> dataPicker) {
        this.dataPicker = dataPicker;
    }

    public DataPickerResult<T> pickData(String name, ModelAccess modelAccess, JsonNode attributes) {
        DataPickerResult<ModelNode> tmpResult = dataPicker.pickData(name, modelAccess, attributes);
        if (!tmpResult.isResolved()) {
            return DataPickerResult.convert(null, tmpResult);
        } else {
            if (tmpResult == null || tmpResult.value.isNull()) {
                return new DataPickerResult<>(tmpResult.type, tmpResult.source, null);
            }
            return Convenience.build(result ->
                    convert(tmpResult.value).ifPresentOrElse(
                            value -> result.value = DataPickerResult.convert(value, tmpResult),
                            () -> result.value = DataPickerResult.unresolvedInvalidAttrValue(tmpResult.source)));
        }
    }

    abstract Optional<T> convert(ModelNode node);
}
