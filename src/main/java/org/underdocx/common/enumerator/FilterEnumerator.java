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

package org.underdocx.common.enumerator;

import java.util.function.Predicate;

public class FilterEnumerator<T> extends AbstractPrepareNextEnumerator<T> {

    protected final Predicate<T> filter;
    protected final Enumerator<T> inner;

    public FilterEnumerator(Enumerator<T> inner, Predicate<T> filter) {
        super();
        this.filter = filter;
        this.inner = inner;
        init();
    }

    public FilterEnumerator(FilterEnumerator<T> other) {
        super(other);
        this.inner = other.inner.cloneEnumerator();
        this.filter = other.filter;
    }

    @Override
    protected T findNext() {
        while (inner.hasNext()) {
            T toTest = inner.next();
            if (filter == null || filter.test(toTest)) {
                return toTest;
            }
        }
        return null;
    }

    @Override
    public Enumerator<T> cloneEnumerator() {
        return new FilterEnumerator<>(this);
    }
}
