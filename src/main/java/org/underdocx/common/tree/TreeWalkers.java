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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A combination of multiple {@link TreeWalker}s to combine them in one {@link Enumerator}
 */
public class TreeWalkers implements Enumerator<TreeWalker.VisitState> {

    private Iterator<TreeWalker> treeWalkers;
    private TreeWalker currentTreeWalker = null;

    public TreeWalkers(Iterator<TreeWalker> treeWalkers) {
        this.treeWalkers = treeWalkers;

    }

    public TreeWalkers(Node... nodes) {
        this(getTreeWalkers(nodes));
    }

    private static Iterator<TreeWalker> getTreeWalkers(Node... nodes) {
        List<TreeWalker> result = new ArrayList<>();
        for (Node node : nodes) {
            result.add(new TreeWalker(node, node));
        }
        return result.iterator();
    }

    @Override
    public boolean hasNext() {
        if (currentTreeWalker != null && currentTreeWalker.hasNext()) {
            return true;
        } else {
            currentTreeWalker = null;
        }
        if (currentTreeWalker == null && treeWalkers.hasNext()) {
            currentTreeWalker = treeWalkers.next();
        }
        if (currentTreeWalker == null) {
            return false;
        } else return hasNext();
    }

    @Override
    public TreeWalker.VisitState next() {
        if (currentTreeWalker != null && currentTreeWalker.hasNext()) {
            return currentTreeWalker.next();
        } else {
            currentTreeWalker = null;
        }
        if (currentTreeWalker == null && treeWalkers.hasNext()) {
            currentTreeWalker = treeWalkers.next();
        }
        if (currentTreeWalker == null) {
            return null;
        } else return next();
    }
}
