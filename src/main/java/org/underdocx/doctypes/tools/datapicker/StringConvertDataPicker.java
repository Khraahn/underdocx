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

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.Wrapper;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;

import java.util.Optional;

public class StringConvertDataPicker implements ExtendedDataPicker<String> {

    private final Model2StringConverter converter;
    private final ExtendedDataPicker<DataNode> dataPicker;

    public StringConvertDataPicker() {
        this(new AttributeNodeDataPicker(), new DefaultModel2StringConverter());
    }

    public StringConvertDataPicker(
            ExtendedDataPicker<DataNode> dataPicker,
            Model2StringConverter converter
    ) {
        this.converter = converter;
        this.dataPicker = dataPicker;
    }

    public StringConvertDataPicker(
            ExtendedDataPicker<DataNode> dataPicker
    ) {
        this.converter = new DefaultModel2StringConverter();
        this.dataPicker = dataPicker;
    }

    public DataPickerResult<String> pickData(String name, DataAccess dataAccess, JsonNode attributes) {
        DataPickerResult<DataNode> tmpResult = dataPicker.pickData(name, dataAccess, attributes);
        if (!tmpResult.isResolved()) {
            return DataPickerResult.convert(null, tmpResult);
        } else {
            Optional<Wrapper<String>> converted = converter.convert(tmpResult.value);
            if (converted.isEmpty()) {
                return DataPickerResult.unresolvedInvalidAttrValue(tmpResult.source);
            }
            String value = converted.get().value;
            return DataPickerResult.convert(value, tmpResult);
        }
    }

    public interface Model2StringConverter {
        Optional<Wrapper<String>> convert(DataNode node);
    }

    public static class DefaultModel2StringConverter implements Model2StringConverter {

        @Override
        public Optional<Wrapper<String>> convert(DataNode node) {
            if (node == null || node.isNull()) {
                return Optional.of(new Wrapper<>(null));
            }
            if (node.getType() == DataNode.DataNodeType.LEAF) {
                return Optional.of(new Wrapper<>(String.valueOf(node.getValue())));
            }
            return Optional.empty();
        }
    }
}
