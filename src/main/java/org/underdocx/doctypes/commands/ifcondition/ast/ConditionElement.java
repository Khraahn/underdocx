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

package org.underdocx.doctypes.commands.ifcondition.ast;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ConditionElement {
    public final List<ConditionElement> elements;

    public ConditionElement(ConditionElement conditionElement) {
        this.elements = Convenience.buildList(result -> result.add(conditionElement));
    }

    public ConditionElement(List<ConditionElement> conditionElements) {
        this.elements = conditionElements;
    }

    public ConditionElement() {
        this.elements = new ArrayList<>();
    }

    public void add(ConditionElement conditionElement) {
        this.elements.add(conditionElement);
    }

    public abstract boolean eval(Function<Pair<String, Object>, Integer> comparator);

}
