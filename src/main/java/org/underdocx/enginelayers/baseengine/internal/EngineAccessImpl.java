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
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.*;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class EngineAccessImpl<C extends DocContainer<D>, D> implements EngineAccess<C, D> {

    private final Set<EngineListener<C, D>> listeners;
    private final Runnable rescan;
    private final Supplier<List<Node>> lookBack;
    private final Supplier<Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>>> lookAhead;
    private final Map<Class<?>, PlaceholdersProvider<C, ?, D>> handler2ProviderMap;


    public EngineAccessImpl(Set<EngineListener<C, D>> listeners,
                            Runnable forceRescan,
                            Supplier<Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>>> lookAhead,
                            Supplier<List<Node>> lookBack,
                            Map<Class<?>, PlaceholdersProvider<C, ?, D>> handler2ProviderMap) {
        this.listeners = listeners;
        this.rescan = forceRescan;
        this.lookAhead = lookAhead;
        this.lookBack = lookBack;
        this.handler2ProviderMap = handler2ProviderMap;
    }

    @Override
    public void addListener(EngineListener<C, D> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(EngineListener<C, D> listener) {
        listeners.remove(listener);
    }

    @Override
    public void forceRescan() {
        rescan.run();
    }

    private class LookAheadEnumerator extends AbstractPrepareNextEnumerator<SelectedNode<?>> {
        private final Predicate<SelectedNode<?>> filter;
        private final Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> cloneEnumerator;

        protected LookAheadEnumerator(Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> engineEnumerator, Predicate<SelectedNode<?>> filter) {
            this.cloneEnumerator = engineEnumerator.cloneEnumerator();
            this.filter = filter;
        }

        private LookAheadEnumerator(LookAheadEnumerator other) {
            super(other);
            this.cloneEnumerator = other.cloneEnumerator.cloneEnumerator();
            this.filter = other.filter;
        }

        @Override
        protected SelectedNode<?> findNext() {
            while (cloneEnumerator.hasNext()) {
                Pair<PlaceholdersProvider<C, ?, D>, Node> pair = cloneEnumerator.next();
                SelectedNodeImpl<C, ?, D> selectedNode = new SelectedNodeImpl<>(pair.right, pair.left);
                if (filter == null || filter.test(selectedNode)) {
                    return selectedNode;
                }
            }
            return null;
        }

        @Override
        public Enumerator<SelectedNode<?>> cloneEnumerator() {
            return new LookAheadEnumerator(this);
        }
    }

    public Enumerator<SelectedNode<?>> lookAhead(Predicate<SelectedNode<?>> filter) {
        return new LookAheadEnumerator(lookAhead.get(), filter);
    }

    public List<Node> lookBack(Predicate<Node> filter) {
        return Convenience.filter(lookBack.get(), filter);
    }

    @Override
    public <H extends CommandHandler<C, ?, D>> Optional<? extends TextualPlaceholderToolkit<?>> getToolkit(Class<H> commandHandler) {
        PlaceholdersProvider<C, ?, D> provider = handler2ProviderMap.get(commandHandler);
        if (provider != null) {
            return provider.getPlaceholderToolkit();
        } else return Optional.empty();
    }
}
