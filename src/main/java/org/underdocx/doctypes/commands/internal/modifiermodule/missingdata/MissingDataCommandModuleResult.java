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

package org.underdocx.doctypes.commands.internal.modifiermodule.missingdata;


import org.underdocx.doctypes.tools.datapicker.DataPickerResult;

public class MissingDataCommandModuleResult<M> {

    public enum MissingDataCommandModuleResultType {
        STRATEGY_EXECUTED,
        VALUE_RECEIVED,
        SKIP
    }

    public M value = null;
    public DataPickerResult.ResultSource source;

    public MissingDataCommandModuleResultType resultType;

    public MissingDataCommandModuleResult(M value, DataPickerResult.ResultSource source) {
        this.value = value;
        this.resultType = MissingDataCommandModuleResultType.VALUE_RECEIVED;
        this.source = source;
    }

    public MissingDataCommandModuleResult(MissingDataCommandModuleResultType type) {
        this.resultType = MissingDataCommandModuleResultType.STRATEGY_EXECUTED;
    }

    public MissingDataCommandModuleResult(MissingDataCommandModuleResultType type, M value) {
        this.resultType = type;
        this.value = value;
    }
}
