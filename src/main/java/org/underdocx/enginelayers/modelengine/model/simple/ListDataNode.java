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

package org.underdocx.enginelayers.modelengine.model.simple;

import java.util.ArrayList;
import java.util.List;

public class ListDataNode extends AbstractPredefinedDataNode<List<AbstractDataNode<?>>> {

    public ListDataNode() {
        this.containedValue = new ArrayList<>();
    }


    @Override
    protected void removeChild(AbstractDataNode<?> node) {
        getValue().remove(node);
    }

    @Override
    public DataNodeType getType() {
        return DataNodeType.LIST;
    }

    @Override
    public AbstractDataNode<?> getProperty(int index) {
        return index < containedValue.size() ? containedValue.get(index) : null;
    }

    @Override
    public boolean hasProperty(int index) {
        return index < containedValue.size();
    }

    public <T extends AbstractDataNode<?>> void add(T node) {
        checkParentOfChild(node);
        node.setParent(this);
        containedValue.add(node);
    }

    @Override
    public int getSize() {
        return containedValue.size();
    }
}
