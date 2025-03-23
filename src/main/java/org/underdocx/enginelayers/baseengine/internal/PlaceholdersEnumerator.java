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

package org.underdocx.enginelayers.baseengine.internal;

import org.underdocx.common.enumerator.AbstractPrepareNextEnumerator;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlaceholdersEnumerator<C extends DocContainer<D>, D> extends AbstractPrepareNextEnumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> {

    private final Map<PlaceholdersProvider<C, ?, D>, Enumerator<Node>> currentEnumerators = new HashMap<>();
    private final Map<PlaceholdersProvider<C, ?, D>, Node> currentNodes = new HashMap<>();


    public PlaceholdersEnumerator(Set<PlaceholdersProvider<C, ?, D>> providers) {
        providers.forEach(provider -> currentEnumerators.put(provider, provider.getPlaceholders()));
        currentEnumerators.forEach((provider, enumerator) -> {
            Node node = enumerator.next();
            if (node != null && (next == null || Nodes.compareNodePositions(node, next.getPreparedNextElement().right) < 0)) {
                next = new PreparedNextElement(new Pair<>(provider, node), true);
            }
            currentNodes.put(provider, node);
        });
    }

    private PlaceholdersEnumerator(PlaceholdersEnumerator<C, D> other) {
        super(other);
        other.currentEnumerators.forEach((key, value) -> this.currentEnumerators.put(key, value.cloneEnumerator()));
        this.currentNodes.putAll(other.currentNodes);
    }

    @Override
    protected Pair<PlaceholdersProvider<C, ?, D>, Node> findNext() {
        if (next == null || next.getPreparedNextElement() == null) return null;
        Node newElement = currentEnumerators.get(next.getPreparedNextElement().left).next();
        currentNodes.put(next.getPreparedNextElement().left, newElement);
        Wrapper<Pair<PlaceholdersProvider<C, ?, D>, Node>> result = new Wrapper<>();

        currentNodes.forEach((provider, node) -> {
            if (node != null && (result.value == null || Nodes.compareNodePositions(node, result.value.right) < 0)) {
                result.value = new Pair<>(provider, node);
            }
        });
        return result.value;
    }

    @Override
    public PlaceholdersEnumerator<C, D> cloneEnumerator() {
        return new PlaceholdersEnumerator<>(this);
    }
}
