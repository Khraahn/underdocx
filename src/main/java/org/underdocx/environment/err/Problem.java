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

import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A Problem describes details about an error occurred during template processing
 * It can be thrown using a {@link ProblemException} an can be caught, extended and
 * rethrown on multiple code layers
 */
public class Problem {

    public Throwable exception = null;
    public CommandHandler<?, ?, ?> command = null;
    public String key = null;
    public String description = null;
    public String property = null;
    public String value = null;
    public Node node = null;

    public Problem(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public Problem(Problem problem) {
        be(problem);
    }


    public Problem command(CommandHandler<?, ?, ?> commandHandler, boolean overwrite) {
        if (overwrite || this.command == null) {
            this.command = commandHandler;
        }
        return this;
    }

    public Problem command(CommandHandler<?, ?, ?> commandHandler) {
        return this.command(commandHandler, false);
    }

    public Problem key(String key, boolean overwrite) {
        if (overwrite || this.key == null) {
            this.key = key;
        }
        return this;
    }

    public Problem key(String key) {
        return this.key(key, false);
    }

    public Problem description(String description, boolean overwrite) {
        if (description != null && (overwrite || this.description == null)) {
            this.description = description;
        }
        return this;
    }

    public Problem description(String description) {
        return this.description(description, false);
    }

    public Problem property(String property, boolean overwrite) {
        if (property != null && (overwrite || this.property == null)) {
            this.property = property;
        }
        return this;
    }

    public Problem property(String property) {
        return this.property(property, false);
    }

    public Problem value(String value, boolean overwrite) {
        if (value != null && (overwrite || this.value == null)) {
            this.value = value;
        }
        return this;
    }

    public Problem value(String value) {
        return this.value(value, false);
    }

    public Problem node(Node node, boolean overwrite) {
        if (node != null && (overwrite || this.node == null)) {
            this.node = node;
        }
        return this;
    }

    public Problem node(Node node) {
        return this.node(node, false);
    }

    public Problem exception(Throwable exception, boolean overwrite) {
        if (exception != null && (overwrite || this.exception == null)) {
            this.exception = exception;
        }
        return this;
    }

    public Problem exception(Throwable exception) {
        return this.exception(exception, true); // always take newest
    }

    public <T> T fire() {
        throw new ProblemException(this);
    }

    public Problem extend(Problem problem) {
        this.description(problem.description);
        this.key(problem.key);
        this.property(problem.property);
        this.value(problem.value);
        this.exception(problem.exception);
        this.node(problem.node);
        this.command(problem.command);
        return this;
    }

    public Problem handle(Throwable caughtThrowable) {
        if (caughtThrowable instanceof ProblemException) {
            Problem caughtProblem = new Problem(((ProblemException) caughtThrowable).getProblem()).exception(caughtThrowable);
            return be(caughtProblem.extend(this));
        } else {
            return exception(caughtThrowable, true);
        }
    }

    public Problem be(Problem problem) {
        this.exception = problem.exception;
        this.command = problem.command;
        this.key = problem.key;
        this.description = problem.description;
        this.property = problem.property;
        this.value = problem.value;
        this.node = problem.node;
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendLineNotNull(sb, "key: ", this.key, "-");
        appendLineNotNull(sb, "description: ", this.description, "-");
        appendLineNotNull(sb, "handler: ", getString(this.command), "-");
        appendLineNotNull(sb, "property: ", this.property, "-");
        appendLineNotNull(sb, "value: ", this.value, "-");
        appendLineNotNull(sb, "node: ", getString(this.node), "-");
        appendLineNotNull(sb, "exception: ", getString(this.exception), "-");
        for (Throwable t : getCauses(this.exception)) {
            if (!(t instanceof ProblemException)) {
                appendLineNotNull(sb, "cause: ", getString(t), "-");
            }
        }
        return sb.toString();
    }

    public String getSingleLineReport() {
        List<String> elements = new ArrayList<>();
        Convenience.ifNotNull(description, elements::add);
        Convenience.ifNotNull(getString(command), command -> elements.add("handler: " + command));
        Convenience.ifNotNull(property, property -> elements.add("property: " + property));
        Convenience.ifNotNull(value, value -> elements.add("value: " + value));
        Convenience.ifNotNull(getString(node), node -> elements.add("node: " + node));
        return String.join(", ", elements);
    }


    private String getString(Node node) {
        if (node == null) return null;
        else return node.getTextContent();
    }

    private String getString(Throwable throwable) {
        if (throwable == null) return null;
        else return throwable.getMessage();
    }

    private String getString(CommandHandler<?, ?, ?> commandHandler) {
        if (commandHandler == null) return null;
        else return commandHandler.getName();
    }


    private void appendLineNotNull(StringBuilder sb, String str) {
        if (str != null) {
            sb.append(str).append("\n");
        }
    }

    private void appendLineNotNull(StringBuilder sb, String str, String fallback) {
        if (str != null) {
            appendLineNotNull(sb, str);
        } else {
            appendLineNotNull(sb, fallback);
        }
    }

    private void appendLineNotNull(StringBuilder sb, String prefix, String str, String fallback) {
        if (prefix != null)
            sb.append(prefix);
        appendLineNotNull(sb, str, fallback);
    }

    public List<Throwable> getCauses(Throwable e) {
        return Convenience.buildList(result -> {
            if (e != null) {
                Throwable current = e.getCause();
                while (current != null) {
                    result.add(current);
                    current = current.getCause();
                }
            }
        });
    }

}
