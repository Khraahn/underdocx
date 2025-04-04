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

import org.underdocx.common.enumerator.AbstractPrepareNextEnumerator;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TreePaths extends AbstractPrepareNextEnumerator<Node> {

    private List<Node> fittingNodes = new ArrayList<>();
    private int index = 0;

    public TreePaths(Node start, Predicate<Node>... pathFilters) {
        fittingNodes.add(start);
        int filterIndex = 0;
        while (filterIndex < pathFilters.length) {
            List<Node> nextFitting = new ArrayList<>();
            Predicate<Node> currentFilter = pathFilters[filterIndex];
            fittingNodes.forEach(currentNode -> {
                nextFitting.addAll(Nodes.getChildren(currentNode, currentFilter).collect());
            });
            filterIndex++;
            fittingNodes = nextFitting;
        }
    }

    private TreePaths(List<Node> fittingNodes, int index) {
        this.fittingNodes = new ArrayList<>(fittingNodes);
        this.index = index;
    }

    @Override
    protected Node findNext() {
        return Convenience.also(index < fittingNodes.size() ? fittingNodes.get(index) : null, x -> index++);
    }

    @Override
    public Enumerator<Node> cloneEnumerator() {
        return new TreePaths(fittingNodes, index);
    }
}
