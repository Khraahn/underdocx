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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.codec.JsonCodec;
import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.modelengine.data.DataNode;

import java.util.*;

public abstract class AbstractPredefinedDataNode<T> extends AbstractDataNode<T> implements DataNode<T> {

    protected static final JsonCodec jsonCodec = new JsonCodec(true, true, true);


    @Override
    public DataNode<?> getProperty(String name) {
        return null;
    }

    @Override
    public DataNode<?> getProperty(int index) {
        return null;
    }

    @Override
    public DataNode<?> getParent() {
        return parent;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean hasProperty(String name) {
        return false;
    }

    @Override
    public boolean hasProperty(int index) {
        return false;
    }

    @Override
    public boolean isNull() {
        return getValue() == null;
    }

    @Override
    protected AbstractPredefinedDataNode<?> create(Object object) {
        return Convenience.also(createRootNode(object), result -> result.setParent(this));
    }

    @Override
    public Set<String> getPropertyNames() {
        return Set.of();
    }

    /**
     * converts a {@link Map}, {@link List} or Leaf object into a {@link DataNode} tree
     */
    public static AbstractPredefinedDataNode<?> createRootNode(Object object) {
        return Convenience.build(w -> {
            if (object == null) {
                w.value = new LeafDataNode<>(null);
            } else if (object instanceof Collection<?>) {
                w.value = Convenience.also(new ListDataNode(),
                        node -> ((List<?>) object).forEach(
                                child -> node.add(node.create(child))));
            } else if (object instanceof Map<?, ?>) {
                w.value = Convenience.also(new MapDataNode(),
                        node -> ((Map<?, ?>) object).forEach(
                                (key, value) -> node.add(String.valueOf(key), node.create(value))));
            } else {
                w.value = new LeafDataNode<>(object);
            }
        });
    }

    /**
     * unpacks the {@link DataNode} Tree and creates a new tree of {@link Map}, {@link List} and Leaf objects
     */
    @SuppressWarnings("unchecked")
    public static Object convert(DataNode node) {
        if (node.getValue() == null) {
            return null;
        } else if (node.getType() == DataNodeType.MAP) {
            return Convenience.also(new HashMap<String, Object>(),
                    map -> ((Map<String, DataNode>) node.getValue()).forEach(
                            (key, value) -> map.put(key, convert(value))));
        } else if (node.getType() == DataNodeType.LIST) {
            return Convenience.also(new ArrayList<>(),
                    list -> ((List<DataNode>) node.getValue()).forEach(
                            value -> list.add(convert(value))));
        } else {
            return node.getValue();
        }
    }

    public String toString() {
        return jsonCodec.convertMapToJsonString(convert(this)).orElse("ModelNode: " + containedValue);
    }

    /**
     * Creates a {@link DataNode} tree based on a JSON string using the {@link JsonCodec}
     */
    public static AbstractPredefinedDataNode<?> createFromJson(String json) throws JsonProcessingException {
        JsonNode parsed = jsonCodec.parse(json);
        if (parsed.isArray()) {
            List<Object> list = jsonCodec.getAsList(parsed);
            return createRootNode(list);
        } else {
            Map<String, Object> map = jsonCodec.getAsMap(parsed);
            return createRootNode(map);
        }
    }
}
