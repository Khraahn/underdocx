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

import java.util.Optional;

public class DataPickerResult<T> {

    public enum ResultType {
        RESOLVED(true), // value recieved, null value might be valid
        UNRESOLVED_INVALID_ATTR_VALUE(false), // For example invalid ModelPath syntax
        UNRESOLVED_MISSING_ATTR(false), // For example mandatory "@value" attribute is missing
        UNRESOLVED_MISSING_VALUE(false); // For example model does not contain requiested data

        private final boolean resolved;

        ResultType(boolean resolved) {
            this.resolved = resolved;
        }
    }

    public T value;
    public ResultType type;

    public boolean isResolved() {
        return type.resolved;
    }

    public Optional<T> getOptionalValue() {
        return Optional.ofNullable(value);
    }

    public DataPickerResult(T value) {
        this.type = ResultType.RESOLVED;
        this.value = value;
    }

    public DataPickerResult(ResultType type) {
        this.type = type;
        this.value = null;
    }


}
