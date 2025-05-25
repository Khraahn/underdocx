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

import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class IgnoreStateHandler {
    private static final String EXIT = "Exit";
    private static final String IGNORE = "Ignore";
    private static final String END_IGNORE = "EndIgnore";

    private final boolean ignoreScanEnabled;
    private boolean ignoring = false;

    public IgnoreStateHandler(boolean ignoreScanEnabled) {
        this.ignoreScanEnabled = ignoreScanEnabled;
    }

    public IgnoreStateHandler() {
        this(true);
    }

    public IgnoreState interpretAndCheckExecution(ParametersPlaceholderData placeholderData) {
        if (ignoreScanEnabled)
            return handleIgnoreState(placeholderData);
        else
            return IgnoreState.NORMAL;
    }

    private IgnoreState handleIgnoreState(ParametersPlaceholderData placeholderData) {
        String key = placeholderData.getKey();
        return switch (key) {
            case EXIT -> {
                if (!ignoring) {
                    ignoring = true;
                    yield IgnoreState.EXIT;
                } else {
                    yield IgnoreState.IGNORING;
                }
            }
            case IGNORE -> {
                if (!ignoring) {
                    ignoring = true;
                    yield IgnoreState.START_IGNORE;
                } else {
                    yield IgnoreState.IGNORING;
                }
            }
            case END_IGNORE -> {
                ignoring = false;
                yield IgnoreState.END_IGNORE;
            }
            default -> {
                if (ignoring) {
                    yield IgnoreState.IGNORING;
                }
                yield IgnoreState.NORMAL;
            }
        };
    }

    public void setIgnoring(boolean ignoring) {
        this.ignoring = ignoring;
    }
}
