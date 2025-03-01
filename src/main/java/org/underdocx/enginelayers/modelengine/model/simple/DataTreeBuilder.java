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

import org.underdocx.enginelayers.modelengine.model.DataNode;

/**
 * This class provides a fluent API to build up {@link DataNode}-data-trees
 */
public class DataTreeBuilder {

    private DataTreeBuilder() {
    }

    /**
     * Creates a root ModelNode that represents a List
     *
     * @return a {@link DataNodeWrapper} that contains a {@link ListDataNode}
     */
    public static DataNodeWrapper beginList() {
        return new DataNodeWrapper(new ListDataNode());
    }

    /**
     * Creates a root ModelNode that represents a Map.
     *
     * @return a {@link DataNodeWrapper} that contains a {@link MapDataNode}
     */
    public static DataNodeWrapper beginMap() {
        return new DataNodeWrapper(new MapDataNode());
    }

    /**
     * Fluent API to build up a {@link DataNode}-data-tree
     * Use {@link #build()} to finish the tree construction, use
     * the begin*-Methods to increase the tree depth, use end*
     * to decrease the tree depth and use the
     * add methods to add data on the same level.
     */
    public static class DataNodeWrapper {
        private AbstractDataNode<?> currentNode;
        private DataNodeWrapper parent = null;

        protected DataNodeWrapper(AbstractDataNode<?> node) {
            currentNode = node;
        }

        protected DataNodeWrapper(DataNodeWrapper parent, AbstractDataNode<?> node) {
            currentNode = node;
            this.parent = parent;
        }

        public DataNodeWrapper beginList(String listName) {
            if (currentNode instanceof MapDataNode mapModelNode) {
                ListDataNode newList = new ListDataNode();
                mapModelNode.add(listName, newList);
                return new DataNodeWrapper(this, newList);
            } else return unsupportedCall();
        }

        public DataNodeWrapper beginList() {
            if (currentNode instanceof ListDataNode listModelNode) {
                ListDataNode newList = new ListDataNode();
                listModelNode.add(newList);
                return new DataNodeWrapper(this, newList);
            } else return unsupportedCall();
        }


        public DataNodeWrapper beginMap(String mapName) {
            if (currentNode instanceof MapDataNode mapModelNode) {
                MapDataNode newMap = new MapDataNode();
                mapModelNode.add(mapName, newMap);
                return new DataNodeWrapper(this, newMap);
            } else return unsupportedCall();
        }

        public DataNodeWrapper beginMap() {
            if (currentNode instanceof ListDataNode mapModelNode) {
                MapDataNode newMap = new MapDataNode();
                mapModelNode.add(newMap);
                return new DataNodeWrapper(this, newMap);
            } else return unsupportedCall();
        }

        private <T> T unsupportedCall() {
            throw new UnsupportedOperationException("invalid method call, adding nodes with name only allowed for map node, adding nodes without name only allowed for list nodes");
        }

        public DataNodeWrapper add(String name, String value) {
            return addObj(name, value);
        }

        public DataNodeWrapper add(String value) {
            return addObj(value);
        }

        public DataNodeWrapper add(String name, int value) {
            return addObj(name, value);
        }

        public DataNodeWrapper add(int value) {
            return addObj(value);
        }

        public DataNodeWrapper add(String name, boolean value) {
            return addObj(name, value);
        }

        public DataNodeWrapper add(boolean value) {
            return addObj(value);
        }

        public DataNodeWrapper add(String name, double value) {
            return addObj(name, value);
        }

        public DataNodeWrapper add(double value) {
            return addObj(value);
        }


        public DataNodeWrapper addObj(String name, Object value) {
            if (currentNode instanceof MapDataNode mapModelNode) {
                mapModelNode.add(name, new LeafDataNode<>(value));
                return this;
            } else return unsupportedCall();
        }

        public DataNodeWrapper addNode(String name, AbstractDataNode<?> node) {
            if (currentNode instanceof MapDataNode mapModelNode) {
                mapModelNode.add(name, node);
                return this;
            } else return unsupportedCall();
        }


        public DataNodeWrapper addObj(Object value) {
            if (currentNode instanceof ListDataNode listModelNode) {
                listModelNode.add(new LeafDataNode<>(value));
                return this;
            } else return unsupportedCall();
        }

        public DataNodeWrapper addNode(AbstractDataNode<?> node) {
            if (currentNode instanceof ListDataNode listModelNode) {
                listModelNode.add(node);
                return this;
            } else return unsupportedCall();
        }


        public DataNodeWrapper end() {
            if (parent != null) {
                return parent;
            } else {
                return this;
            }
        }

        public DataNode build() {
            if (parent != null) {
                return parent.build();
            } else {
                return currentNode;
            }
        }
    }


}
