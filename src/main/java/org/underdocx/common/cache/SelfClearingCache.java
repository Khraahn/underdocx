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

package org.underdocx.common.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class SelfClearingCache<K, V> {

    private final long timeout;
    private final Object lock = new Object();
    private boolean running = false;
    private final Map<K, V> cache = new HashMap<>();
    private final Timer timer = new Timer();

    public SelfClearingCache(long timeout) {
        this.timeout = timeout;
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            synchronized (lock) {
                cache.clear();
                running = false;
            }
        }
    };

    public V getOrCache(K key, Supplier<V> valueProvider) {
        V result;
        synchronized (lock) {
            if (!running) {
                running = true;
                timer.schedule(timerTask, timeout);
            }
            result = cache.get(key);
            if (result == null) {
                result = valueProvider.get();
                cache.put(key, result);
            }
        }
        return result;
    }


}
