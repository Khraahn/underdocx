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
import de.underdocx.tools.common.Wrapper;

import java.util.Optional;

public class StringConvertDataPicker implements ExtendedDataPicker<String> {

    private final Model2StringConverter converter;
    private final ExtendedDataPicker<ModelNode> dataPicker;

    public StringConvertDataPicker() {
        this(new AttributeNodeDataPicker(), new DefaultModel2StringConverter());
    }

    public StringConvertDataPicker(
            ExtendedDataPicker<ModelNode> dataPicker,
            Model2StringConverter converter
    ) {
        this.converter = converter;
        this.dataPicker = dataPicker;
    }

    public DataPickerResult<String> pickData(String name, ModelAccess modelAccess, JsonNode attributes) {
        DataPickerResult<ModelNode> tmpResult = dataPicker.pickData(name, modelAccess, attributes);
        if (!tmpResult.isResolved()) {
            return new DataPickerResult<>(tmpResult.type);
        } else {
            Optional<Wrapper<String>> converted = converter.convert(tmpResult.value);
            if (converted.isEmpty()) {
                return new DataPickerResult<>(DataPickerResult.ResultType.UNRESOLVED_INVALID_ATTR_VALUE);
            }
            String value = converted.get().value;
            return new DataPickerResult<>(value);
        }
    }

    public interface Model2StringConverter {
        Optional<Wrapper<String>> convert(ModelNode node);
    }

    public static class DefaultModel2StringConverter implements Model2StringConverter {

        @Override
        public Optional<Wrapper<String>> convert(ModelNode node) {
            if (node == null || node.isNull()) {
                return Optional.of(new Wrapper<>(null));
            }
            if (node.getType() == ModelNode.ModelNodeType.LEAF) {
                return Optional.of(new Wrapper<>(String.valueOf(node.getValue())));
            }
            return Optional.empty();
        }
    }
}
