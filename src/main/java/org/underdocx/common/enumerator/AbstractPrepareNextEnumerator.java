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

import org.underdocx.common.tools.Convenience;

public abstract class AbstractPrepareNextEnumerator<T> implements Enumerator<T> {

    protected class PreparedNextElement {
        private boolean isValid;
        private T preparedNextElement;

        public PreparedNextElement(T preparedNextElement, boolean valid) {
            this.preparedNextElement = preparedNextElement;
            this.isValid = valid;
        }

        public T getPreparedNextElement() {
            return preparedNextElement;
        }

        public void setPreparedNextElement(T element) {
            this.preparedNextElement = element;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean value) {
            isValid = value;
        }
    }

    protected PreparedNextElement next;

    protected AbstractPrepareNextEnumerator() {
        next = null;
    }

    protected AbstractPrepareNextEnumerator(T initialNode) {
        next = new PreparedNextElement(initialNode, true);
    }

    protected AbstractPrepareNextEnumerator(AbstractPrepareNextEnumerator<T> other) {
        this.next = other.next;
    }

    protected abstract T findNext();

    @Override
    public boolean hasNext() {
        if (next == null || !next.isValid) {
            next = new PreparedNextElement(findNext(), true);
        }
        return next.getPreparedNextElement() != null;
    }

    @Override
    public T next() {
        if (next == null || !next.isValid) {
            next = new PreparedNextElement(findNext(), true);
        }
        return Convenience.also(next.getPreparedNextElement(), n -> next.setValid(false));
    }


}
