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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Enumerator<T> extends Iterable<T>, Iterator<T>, Enumeration<T> {

    static <T> InspectableEnumerator<T> empty() {
        return new InspectableEnumerator<T>() {
            @Override
            public Optional<T> inspectNext() {
                return Optional.empty();
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    static <T> InspectableEnumerator<T> of(T... elements) {
        return new InspectableEnumerator<T>() {
            private T[] array = elements;
            private int counter = 0;

            @Override
            public Optional<T> inspectNext() {
                return (counter < elements.length) ? Optional.of(elements[counter]) : Optional.empty();
            }

            @Override
            public boolean hasNext() {
                return (counter < elements.length);
            }

            @Override
            public T next() {
                T tmp = array[counter];
                counter++;
                return tmp;
            }
        };
    }

    boolean hasNext();

    T next();

    default Optional<T> next(Predicate<T> filter) {
        while (hasNext()) {
            T element = next();
            if (filter == null || filter.test(element)) {
                return Optional.of(element);
            }
        }
        return Optional.empty();
    }

    default List<T> collect(Predicate<T> filter) {
        return Convenience.buildList(list -> {
            while (hasNext()) {
                T element = next();
                if (filter == null || filter.test(element)) {
                    list.add(element);
                }
            }
        });
    }

    default List<T> collect() {
        return collect(null);
    }

    @Override
    default boolean hasMoreElements() {
        return hasNext();
    }

    @Override
    default T nextElement() {
        return next();
    }

    @Override
    default void remove() {
        Iterator.super.remove();
    }

    @Override
    default void forEachRemaining(Consumer<? super T> action) {
        Iterator.super.forEachRemaining(action);
    }

    static <T> Enumerator<T> fromIterator(Iterator<T> iterator) {
        return new Enumerator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    @Override
    default Iterator<T> iterator() {
        return this;
    }


}
