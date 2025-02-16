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

import jakarta.validation.constraints.NotNull;
import org.underdocx.common.enumerator.Enumerator;
import org.w3c.dom.Node;

import java.util.function.Supplier;


/**
 * Visits all nodes of a subtree (entering and leaving each node)
 */
public class TreeWalker implements Enumerator<TreeWalker.VisitState> {

    private Node initialNode;
    private final Node scope;
    private VisitState state = null;
    private Node firstValidNode = null;


    public TreeWalker(@NotNull Node initialNode, Node scope) {
        this.initialNode = initialNode;
        this.scope = scope;
    }

    public TreeWalker(@NotNull Node initialNode, Node scope, Node firstValidNode) {
        this.initialNode = initialNode;
        this.scope = scope;
        this.firstValidNode = firstValidNode;
    }

    public TreeWalker(@NotNull TreeWalker treeWalker) {
        this.state = treeWalker.state != null ? new VisitState(treeWalker.state) : null;
        this.initialNode = treeWalker.initialNode;
        this.scope = treeWalker.scope;
        this.firstValidNode = treeWalker.firstValidNode;
    }

    private VisitState nextStateSkipChildren() {
        VisitState result;
        if (state != null && state.beginVisit && state.node.hasChildNodes()) {
            result = new VisitState(state.node, false, state.isValid);
        } else {
            return nextState();
        }
        return result;
    }

    private VisitState nextState() {
        if (state == null) return new VisitState(initialNode, true, isFirstValidNodeFound(null, initialNode));
        if (scope != null && state.node == scope && !state.beginVisit) return null;
        if (state.beginVisit) return state.node.hasChildNodes()
                ? new VisitState(state.node.getFirstChild(), true, isFirstValidNodeFound(state, state.node.getFirstChild()))
                : new VisitState(state.node, false, isFirstValidNodeFound(state, state.node));
        else {
            if (state.node.getNextSibling() != null) {
                Node nextSibling = state.node.getNextSibling();
                return new VisitState(nextSibling, true, isFirstValidNodeFound(state, nextSibling));
            } else {
                Node parent = state.node.getParentNode();
                return parent != null ? new VisitState(parent, false, isFirstValidNodeFound(state, parent)) : null;
            }
        }
    }

    public boolean hasNext() {
        return nextState() != null;
    }

    public VisitState next() {
        return doNextInternal(this::nextState);
    }

    public VisitState nextSkipChildren() {
        return doNextInternal(this::nextStateSkipChildren);
    }

    private VisitState doNextInternal(Supplier<VisitState> nextFunction) {
        VisitState result = nextFunction.get();
        if (result != null) state = result;
        return result;
    }

    private boolean isFirstValidNodeFound(VisitState lastState, Node currentNode) {
        if (firstValidNode == null) {
            return true;
        }
        if (currentNode == firstValidNode) {
            return true;
        }
        if (lastState == null) {
            return false;
        }
        return lastState.isValid;
    }

    public void jump(Node node) {
        if (state != null && state.isValid) {
            firstValidNode = null;
        }
        initialNode = node;
        state = null;
    }

    public static Node findNextNode(Node node, Node scope, Node firstValidNodeOrNull, boolean skipChildren) {
        TreeWalker walker = new TreeWalker(node, scope, firstValidNodeOrNull);
        VisitState state;
        walker.next();
        do {
            state = skipChildren ? walker.nextSkipChildren() : walker.next();
        } while (state != null && !state.beginVisit);

        return state == null ? null : state.node;
    }

    public static class VisitState {
        private final Node node;
        private final boolean beginVisit;
        private final boolean isValid;

        public VisitState(@NotNull Node node, boolean beginVisit, boolean isValid) {
            this.node = node;
            this.beginVisit = beginVisit;
            this.isValid = isValid;
        }

        public VisitState(@NotNull VisitState visitState) {
            this(visitState.node, visitState.beginVisit, visitState.isValid);
        }

        public Node getNode() {
            return this.node;
        }

        public boolean isBeginVisit() {
            return beginVisit;
        }

        public boolean isValid() {
            return isValid;
        }
    }

}
