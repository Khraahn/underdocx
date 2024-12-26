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

package de.underdocx.tools.common;

import java.util.Collections;
import java.util.List;

public class Range extends Pair<Integer, Integer> {

    public Range(int a, int b) {
        super(a, b);
    }

    public Range(int a) {
        super(a, a);
    }

    public Range(List<Integer> aList) {
        super(Collections.min(aList), Collections.max(aList));
    }

    public int getMin() {
        return Math.min(left, right);
    }

    public int getMax() {
        return Math.max(left, right);
    }

    public int getLength() {
        return getMax() - getMin() + 1;
    }

    public boolean contains(int value) {
        return value >= getMin() && value <= getMax();
    }
}
