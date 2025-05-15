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

package org.underdocx.doctypes.commands.ignore;

import org.underdocx.common.types.Pair;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class IgnoreStateHandler {
    private boolean ignoring = false;

    public Pair<Boolean, IgnoreInstruction> interpretAndCheckExecution(ParametersPlaceholderData placeholderData) {
        IgnoreInstruction instruction = interpret(placeholderData);
        if (!ignoring) {
            if (instruction == IgnoreInstruction.START_IGNORE) {
                ignoring = true;
            }
            return new Pair<>(true, instruction);
        } else {
            if (instruction == IgnoreInstruction.END_IGNORE) {
                ignoring = false;
                return new Pair<>(true, instruction);
            }
            return new Pair<>(false, instruction);
        }
    }

    public void setIgnoring(boolean state) {
        this.ignoring = state;
    }

    public boolean isIgnoring() {
        return ignoring;
    }

    public static IgnoreInstruction interpret(ParametersPlaceholderData data) {
        String key = data.getKey();
        if (key.equals("Ignore")) {
            return IgnoreInstruction.START_IGNORE;
        } else if (key.equals("EndIgnore")) {
            return IgnoreInstruction.END_IGNORE;
        } else return IgnoreInstruction.UNKNOWN;
    }


}
