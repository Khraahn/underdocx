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

package org.underdocx.enginelayers.modelengine.datapath.elements;

import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.util.List;
import java.util.Optional;

public class IndexDataPathElement implements DataPathElement {
    private final int index;

    public int getIndex() {
        return index;
    }

    public IndexDataPathElement(int index) {
        this.index = index;
    }

    public String toString() {
        return "[" + index + "]";
    }

    @Override
    public DatalPathElementType getType() {
        return DatalPathElementType.INDEX;
    }

    @Override
    public Optional<DataNode> interpret(DataNode node) {
        return Convenience.buildOptional(w -> w.value = node.hasProperty(index) ? node.getProperty(index) : null);
    }

    @Override
    public void interpret(List<DataPathElement> elementsWithoutThis) {
        elementsWithoutThis.add(this);
    }
}
