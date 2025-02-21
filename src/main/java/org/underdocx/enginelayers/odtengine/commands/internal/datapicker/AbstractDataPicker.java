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
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.AttributesInterpreter;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.accesstype.AccessType;

import java.util.Optional;

public abstract class AbstractDataPicker<T, N> implements ExtendedDataPicker<T> {

    protected JsonNode attributes;
    protected DataAccess model;
    protected AttributesInterpreter<Optional<N>, String> attributeInterpreter;
    protected AttributesInterpreter<AccessType, String> typeInterpreter;

    protected AbstractDataPicker(
            AttributesInterpreter<AccessType, String> typeInterpreter,
            AttributesInterpreter<Optional<N>, String> attributeInterpreter) {

        this.typeInterpreter = typeInterpreter;
        this.attributeInterpreter = attributeInterpreter;
    }

    public JsonNode getAttributes() {
        return attributes;
    }

    public DataAccess getModel() {
        return model;
    }

    @Override
    public DataPickerResult<T> pickData(String name, DataAccess dataAccess, JsonNode attributes) {
        this.attributes = attributes;
        this.model = dataAccess;
        return pickData(name);
    }

    abstract protected DataPickerResult<T> pickData(String name);
}
