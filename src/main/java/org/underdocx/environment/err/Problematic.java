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

package org.underdocx.environment.err;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * An Implementation that is able to produce a {@link Problem} instance.
 * This interface also provides helper method to identify the existence
 * of this problem and to throw the corresponding {@link ProblemException}
 */
public interface Problematic {

    Problem toProblem();

    default <T> T fire() {
        toProblem().fire();
        return null;
    }

    default <T> T fire(Exception e) {
        toProblem().handle(e).fire();
        return null;
    }

    default <T> T fireProperty(String missingProperty) {
        toProblem().property(missingProperty).fire();
        return null;
    }

    default <T> T fireValue(String failedValue) {
        toProblem().value(failedValue).fire();
        return null;
    }

    default <T> T notNull(T value, String missingProperty) {
        if (value == null) {
            fireProperty(missingProperty);
        }
        return value;
    }

    default <T> T get(Optional<T> value, String missingProperty) {
        if (value == null || value.isEmpty()) {
            fireProperty(missingProperty);
        }
        return value.get();
    }

    default <T> T notNull(T value, String missingProperty, String failedValue) {
        if (value == null) {
            toProblem().property(missingProperty).value(failedValue).fire();
        }
        return value;
    }

    default <T extends Collection<X>, X> T notNullOrEmpty(T value, String missingProperty) {
        if (value == null || value.isEmpty()) {
            toProblem().property(missingProperty).fire();
        }
        return value;
    }

    default <T extends Collection<X>, X> T notNullOrEmpty(T value, String missingProperty, String failedValue) {
        if (value == null || value.isEmpty()) {
            toProblem().property(missingProperty).value(failedValue).fire();
        }
        return value;
    }

    default <T> T get(Optional<T> value, String missingProperty, String failedValue) {
        if (value == null || value.isEmpty()) {
            toProblem().property(missingProperty).value(failedValue).fire();
        }
        return value.get();
    }

    default <T> T check(boolean condition) {
        if (!condition) {
            toProblem().fire();
        }
        return null;
    }

    default <T> T check(boolean condition, String failedProperty, String failedValue) {
        if (!condition) {
            toProblem().property(failedProperty).value(failedValue).fire();
        }
        return null;
    }

    default <T> T checkNot(boolean condition) {
        if (condition) {
            toProblem().fire();
        }
        return null;
    }

    default <T> T checkNot(boolean condition, String failedProperty, String failedValue) {
        if (condition) {
            toProblem().property(failedProperty).value(failedValue).fire();
        }
        return null;
    }

    default <T> T exec(Callable<T> executable) {
        try {
            return executable.call();
        } catch (Exception e) {
            return fire(e);
        }
    }

    default void run(Callable<Void> executable) {
        try {
            executable.call();
        } catch (Exception e) {
            fire(e);
        }
    }


    default <T> T exec(Callable<T> executable, String failedProperty, String failedValue) {
        try {
            return executable.call();
        } catch (Exception e) {
            return toProblem().property(failedProperty).value(failedValue).exception(e).fire();
        }
    }

    default void run(Executable executable) {
        try {
            executable.run();
        } catch (Exception e) {
            fire(e);
        }
    }

    interface Executable {
        void run() throws Exception;
    }

}
