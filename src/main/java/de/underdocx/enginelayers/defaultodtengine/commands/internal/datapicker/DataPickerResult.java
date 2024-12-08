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

import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;

import java.util.Optional;

public class DataPickerResult<T> {

    public enum ResultSource {
        MODEL, // value received from model
        VAR, // value received from variable storage
        ATTR_VALUE,  // value provided directly by attribute
        UNKNOWN; // value provided directly by attribute or any other source

        public static ResultSource convert(AccessType accessType) {
            return switch (accessType) {
                case ACCESS_ATTR_VALUE -> ATTR_VALUE;
                case MISSING_ACCESS -> UNKNOWN;
                case ACCESS_VARIABLE_BY_NAME -> VAR;
                case ACCESS_MODEL_BY_NAME, ACCESS_CURRENT_MODEL_NODE -> MODEL;
            };
        }
    }

    public enum ResultType {
        RESOLVED(true), // value received, null value might be valid
        UNRESOLVED_INVALID_VALUE(false), // For example invalid ModelPath syntax
        UNRESOLVED_MISSING_ATTR(false), // For example mandatory "@value" attribute is missing
        UNRESOLVED_MISSING_VALUE(false); // For example model does not contain requiested data

        private final boolean resolved;

        ResultType(boolean resolved) {
            this.resolved = resolved;
        }
    }

    public T value;
    public ResultSource source;
    public ResultType type;

    public boolean isResolved() {
        return type.resolved;
    }

    public Optional<T> getOptionalValue() {
        return Optional.ofNullable(value);
    }

    public DataPickerResult(ResultType type, ResultSource source, T value) {
        this.type = type;
        this.value = value;
        this.source = source;
    }

    public static <T> DataPickerResult<T> unresolvedInvalidAttrValue(ResultSource source) {
        return new DataPickerResult<>(ResultType.UNRESOLVED_INVALID_VALUE, source, null);
    }

    public static <T> DataPickerResult<T> unresolvedMissingAttr(ResultSource source) {
        return new DataPickerResult<>(ResultType.UNRESOLVED_MISSING_ATTR, source, null);
    }

    public static <T> DataPickerResult<T> unresolvedMissingValue(ResultSource source) {
        return new DataPickerResult<>(ResultType.UNRESOLVED_MISSING_VALUE, source, null);
    }

    public static <T> DataPickerResult<T> resolvedModel(T value) {
        return new DataPickerResult<>(ResultType.RESOLVED, ResultSource.MODEL, value);
    }

    public static <T> DataPickerResult<T> resolvedVariable(T value) {
        return new DataPickerResult<>(ResultType.RESOLVED, ResultSource.VAR, value);
    }

    public static <T> DataPickerResult<T> resolvedAttrValue(T value) {
        return new DataPickerResult<>(ResultType.RESOLVED, ResultSource.ATTR_VALUE, value);
    }

    public static <T> DataPickerResult<T> resolvedUnknownSource(T value) {
        return new DataPickerResult<>(ResultType.RESOLVED, ResultSource.UNKNOWN, value);
    }

    public static <T> DataPickerResult<T> convert(T value, DataPickerResult<?> toConvert) {
        return new DataPickerResult<>(toConvert.type, toConvert.source, value);
    }
}
