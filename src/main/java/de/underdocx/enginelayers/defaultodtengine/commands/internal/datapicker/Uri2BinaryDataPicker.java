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
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;

import java.net.URI;

/**
 * A {@link ExtendedDataPicker} that first tries to resolve a String and interprets it
 * as URL to resolve the binary data as byte[]
 */
public class Uri2BinaryDataPicker implements ExtendedDataPicker<byte[]> {

    private final ExtendedDataPicker<String> dataPicker;

    public Uri2BinaryDataPicker(ExtendedDataPicker<String> dataPicker) {
        this.dataPicker = dataPicker;
    }

    public Uri2BinaryDataPicker() {
        this(new StringConvertDataPicker());
    }

    public DataPickerResult<byte[]> pickData(String name, ModelAccess modelAccess, JsonNode attributes) {
        DataPickerResult<String> tmpResult = dataPicker.pickData(name, modelAccess, attributes);
        if (!tmpResult.isResolved()) {
            return DataPickerResult.convert(null, tmpResult);
        } else {
            try {
                byte[] data = URI.create(tmpResult.value).toURL().openConnection().getInputStream().readAllBytes();
                return DataPickerResult.convert(data, tmpResult);
            } catch (Exception e) {
                e.printStackTrace();
                return DataPickerResult.unresolvedInvalidAttrValue(tmpResult.source);
            }
        }
    }


}
