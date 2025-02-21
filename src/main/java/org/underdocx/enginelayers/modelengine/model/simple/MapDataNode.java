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

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapDataNode extends AbstractPredefinedDataNode<Map<String, AbstractDataNode<?>>> {


    public MapDataNode() {
        this.containedValue = new LinkedHashMap<>();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public MapDataNode(InputStream is) {
        this(jsonCodec.getAsMap(is).get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public MapDataNode(String jsonString) {
        this(jsonCodec.getAsMap(jsonString).get());
    }

    public MapDataNode(Map<String, Object> map) {
        if (map != null)
            //noinspection unchecked
            this.containedValue = (Map<String, AbstractDataNode<?>>) create(map).containedValue;
        else
            this.containedValue = new LinkedHashMap<>();
    }

    @Override
    protected void removeChild(AbstractDataNode<?> node) {
        new HashMap<>(getValue()).forEach((key, value) -> {
            if (value == node) getValue().remove(key);
        });
    }

    @Override
    public ModelNodeType getType() {
        return ModelNodeType.MAP;
    }

    @Override
    public AbstractDataNode<?> getProperty(String name) {
        return containedValue.get(name);
    }

    @Override
    public boolean hasProperty(String name) {
        return getValue().containsKey(name);
    }

    public void add(String property, AbstractDataNode<?> node) {
        checkParentOfChild(node);
        node.setParent(this);
        containedValue.put(property, node);
    }


}
