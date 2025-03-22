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

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CachedEnumerator<T> implements Enumerator<T> {

    private final CachedEnumeratorCore<T> core;

    private CachedEnumerator(CachedEnumeratorCore<T> core) {
        this.core = core;
    }

    public static <T> CachedEnumerator<T> createCachedEnumerator(Enumerator<T> inner) {
        CachedEnumeratorCore<T> core = new CachedEnumeratorCore<>(inner.cloneEnumerator());
        return core.createFirstClient();
    }

    @Override
    public boolean hasNext() {
        return core.hasNext(this);
    }

    @Override
    public T next() {
        return core.next(this);
    }

    @Override
    public Enumerator<T> cloneEnumerator() {
        return core.cloneClient(this);
    }

    private static class CachedEnumeratorCore<T> {

        private final LinkedList<T> cache;
        private final Enumerator<T> inner;
        private int offset;
        private final Map<CachedEnumerator<T>, Integer> clients = new IdentityHashMap<>();

        protected CachedEnumeratorCore(Enumerator<T> inner) {
            this.inner = inner;
            this.offset = 0;
            this.cache = new LinkedList<>();
        }

        private CachedEnumerator<T> cloneClient(CachedEnumerator<T> client) {
            CachedEnumerator<T> result = new CachedEnumerator<>(this);
            int clientIndex = clients.get(client);
            clients.put(result, clientIndex);
            return result;
        }

        private CachedEnumerator<T> createFirstClient() {
            CachedEnumerator<T> result = new CachedEnumerator<T>(this);
            clients.put(result, 0);
            return result;
        }

        private T next(CachedEnumerator<T> client) {
            int currentIndex = clients.get(client);
            T result = getItem(currentIndex);
            clients.put(client, currentIndex + 1);
            trim();
            return result;
        }

        private void trim() {
            int min = Integer.MAX_VALUE;
            int max = 0;
            for (int value : clients.values()) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
            int realMin = min - offset;
            int realMax = max - offset;
            while (realMax >= cache.size()) {
                if (inner.hasNext()) {
                    cache.add(inner.next());
                } else {
                    cache.add(null);
                }
            }
            for (int i = 0; i < realMin; i++) {
                cache.remove(0);
            }
            offset = offset + realMin;
        }

        private T getItem(int currentIndex) {
            int realIndex = currentIndex - offset;
            while (realIndex >= cache.size()) {
                if (inner.hasNext()) {
                    cache.add(inner.next());
                } else {
                    cache.add(null);
                }
            }
            return cache.get(realIndex);
        }

        private boolean hasNext(CachedEnumerator<T> client) {
            int currentIndex = clients.get(client);
            T result = getItem(currentIndex);
            return result != null;
        }
    }
}
