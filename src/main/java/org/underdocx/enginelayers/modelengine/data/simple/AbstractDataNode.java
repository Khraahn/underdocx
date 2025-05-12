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

import org.underdocx.enginelayers.modelengine.data.DataNode;

public abstract class AbstractDataNode<T> implements DataNode<T> {

    protected T containedValue;
    protected AbstractDataNode<?> parent;

    protected void setParent(AbstractDataNode<?> node) {
        if (node != null && this.parent != null && this.parent != node) {
            this.parent.removeChild(node);
        }
        this.parent = node;
    }

    protected abstract void removeChild(AbstractDataNode<?> node);

    protected void checkParentOfChild(AbstractDataNode<?> node) {
        if (node.parent != null && node.parent != this) {
            node.setParent(null);
        }
    }

    @Override
    public T getValue() {
        return containedValue;
    }

    abstract protected AbstractDataNode<?> create(Object object);

    public static <T> AbstractDataNode<T> ensureAbstractDataNode(DataNode<T> node) {
        if (node instanceof AbstractDataNode<T> result) {
            return result;
        } else {
            return ReferredDataNode.convertToReferredDataNode(node);
        }
    }
}
