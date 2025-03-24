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

package org.underdocx.enginelayers.modelengine.data.simple;

import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.util.Set;
import java.util.function.Supplier;

public class ReferredDataNode<T> extends AbstractDataNode<T> {

    private final Supplier<DataNode<T>> supplier;

    public ReferredDataNode(Supplier<DataNode<T>> nodeSupplier) {
        this.supplier = nodeSupplier;
        this.containedValue = null;
    }

    @Override
    protected void removeChild(AbstractDataNode<?> node) {
    }

    @Override
    protected ReferredDataNode<T> create(Object object) {
        return Convenience.also(new ReferredDataNode<T>(() -> (DataNode<T>) object), referredDataNode -> referredDataNode.setParent(this));
    }

    private ReferredDataNode<?> wrap(DataNode<?> node) {
        if (node == null) {
            return null;
        }
        return create(node);
    }

    @Override
    public DataNodeType getType() {
        return supplier.get().getType();
    }

    @Override
    public DataNode<?> getProperty(String name) {
        return wrap(supplier.get().getProperty(name));
    }

    @Override
    public DataNode<?> getProperty(int index) {
        return wrap(supplier.get().getProperty(index));
    }

    @Override
    public DataNode<?> getParent() {
        return parent;
    }

    @Override
    public int getSize() {
        return supplier.get().getSize();
    }

    @Override
    public boolean hasProperty(String name) {
        return supplier.get().hasProperty(name);
    }

    @Override
    public boolean hasProperty(int index) {
        return supplier.get().hasProperty(index);
    }

    @Override
    public boolean isNull() {
        return supplier.get().isNull();
    }

    @Override
    public Set<String> getPropertyNames() {
        return supplier.get().getPropertyNames();
    }

    @Override
    public T getValue() {
        return (T) supplier.get().getValue();
    }
}
