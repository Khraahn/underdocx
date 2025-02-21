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

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;

import java.util.Optional;

/**
 * A {@link ExtendedDataPicker} is an implementation that tries to resolve requested resources
 * by a given string (usualy a name), a {@link ModelAccess} object to receive variables or model data
 * and a {@link JsonNode} instance to read out json attributes (of the placeholder)
 * <p>
 * All {@link ExtendedDataPicker} can behave very different, it depends on their concrete implementations
 * Most important is {@link AttributeNodeDataPicker}
 */
public interface ExtendedDataPicker<T> {

    DataPickerResult<T> pickData(String name, ModelAccess modelAccess, JsonNode jsonNode);

    default Optional<T> getData(String name, ModelAccess modelAccess, JsonNode jsonNode) {
        return pickData(name, modelAccess, jsonNode).getOptionalValue();
    }

    default PredefinedDataPicker<T> asPredefined(String name) {
        return new PredefinedDataPicker<>() {
            @Override
            public DataPickerResult<T> pickData(ModelAccess modelAccess, JsonNode jsonNode) {
                return ExtendedDataPicker.this.pickData(name, modelAccess, jsonNode);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

}
