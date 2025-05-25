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

public enum IgnoreState {

    EXIT(true, true, true, true),
    START_IGNORE(false, true, true, true),
    IGNORING(false, false, true, false),
    END_IGNORE(false, true, false, true),
    NORMAL(false, true, false, false);

    private final boolean exit;
    private final boolean process;
    private final boolean ignoring;
    private final boolean ignoreRelated;

    IgnoreState(boolean exit, boolean process, boolean ignoring, boolean ignoreRelated) {
        this.exit = exit;
        this.process = process;
        this.ignoring = ignoring;
        this.ignoreRelated = ignoreRelated;
    }

    public boolean shouldExit() {
        return this.exit;
    }

    public boolean shouldProcessCommand() {
        return this.process;
    }

    public boolean isIgnoring() {
        return this.ignoring;
    }

    public boolean isIgnoreRelatedCommand() {
        return this.ignoreRelated;
    }


}
