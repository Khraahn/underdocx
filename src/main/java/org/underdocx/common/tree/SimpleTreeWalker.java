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

package org.underdocx.common.tree;

import org.underdocx.common.enumerator.Enumerator;
import org.w3c.dom.Node;

import java.util.Optional;
import java.util.function.Predicate;

public class SimpleTreeWalker implements Enumerator<Node> {

    public static final Predicate<TreeWalker.VisitState> BeginVisitValidNodeFilter
            = visitState -> visitState.isValid() && visitState.isBeginVisit();

    public static Predicate<TreeWalker.VisitState> buildFilter(Predicate<Node> filter) {
        return visitState -> BeginVisitValidNodeFilter.test(visitState) && filter.test(visitState.getNode());
    }

    protected final Predicate<TreeWalker.VisitState> filter;
    protected final TreeWalker walker;

    public SimpleTreeWalker(Node start, Node limit, Node firstValidNodeOrNull, Predicate<TreeWalker.VisitState> filter) {
        this.filter = filter;
        this.walker = new TreeWalker(start, limit, firstValidNodeOrNull);
    }

    public SimpleTreeWalker(Node start, Node limit, Node firstValidNodeOrNull) {
        this(start, limit, firstValidNodeOrNull, BeginVisitValidNodeFilter);
    }

    protected Optional<Node> nextNode(boolean keepCurrentState) {
        TreeWalker treeWalker = keepCurrentState ? new TreeWalker(walker) : walker;
        Optional<TreeWalker.VisitState> next = treeWalker.next(filter);
        return next.map(TreeWalker.VisitState::getNode);
    }

    @Override
    public boolean hasNext() {
        return nextNode(true).isPresent();
    }

    public Node next() {
        return nextNode(false).orElse(null);
    }
    
}
