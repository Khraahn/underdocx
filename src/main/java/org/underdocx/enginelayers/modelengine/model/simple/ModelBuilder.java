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

import org.underdocx.enginelayers.modelengine.model.ModelNode;

/**
 * This class provides a fluent API to build up {@link ModelNode}-data-trees
 */
public class ModelBuilder {

    private ModelBuilder() {
    }

    /**
     * Creates a root ModelNode that represents a List
     *
     * @return a {@link ModelNodeWrapper} that contains a {@link ListModelNode}
     */
    public static ModelNodeWrapper beginList() {
        return new ModelNodeWrapper(new ListModelNode());
    }

    /**
     * Creates a root ModelNode that represents a Map.
     *
     * @return a {@link ModelNodeWrapper} that contains a {@link MapModelNode}
     */
    public static ModelNodeWrapper beginMap() {
        return new ModelNodeWrapper(new MapModelNode());
    }

    /**
     * Fluent API to build up a {@link ModelNode}-data-tree
     * Use {@link #build()} to finish the tree construction, use
     * the begin*-Methods to increase the tree depth, use end*
     * to decrease the tree depth and use the
     * add methods to add data on the same level.
     */
    public static class ModelNodeWrapper {
        private ModelNode currentNode;
        private ModelNodeWrapper parent = null;

        protected ModelNodeWrapper(ModelNode node) {
            currentNode = node;
        }

        protected ModelNodeWrapper(ModelNodeWrapper parent, ModelNode node) {
            currentNode = node;
            this.parent = parent;
        }

        public ModelNodeWrapper beginList(String listName) {
            if (currentNode instanceof MapModelNode mapModelNode) {
                ListModelNode newList = new ListModelNode();
                mapModelNode.add(listName, newList);
                return new ModelNodeWrapper(this, newList);
            } else return unsupportedCall();
        }

        public ModelNodeWrapper beginList() {
            if (currentNode instanceof ListModelNode listModelNode) {
                ListModelNode newList = new ListModelNode();
                listModelNode.add(newList);
                return new ModelNodeWrapper(this, newList);
            } else return unsupportedCall();
        }


        public ModelNodeWrapper beginMap(String mapName) {
            if (currentNode instanceof MapModelNode mapModelNode) {
                MapModelNode newMap = new MapModelNode();
                mapModelNode.add(mapName, newMap);
                return new ModelNodeWrapper(this, newMap);
            } else return unsupportedCall();
        }

        public ModelNodeWrapper beginMap() {
            if (currentNode instanceof ListModelNode mapModelNode) {
                MapModelNode newMap = new MapModelNode();
                mapModelNode.add(newMap);
                return new ModelNodeWrapper(this, newMap);
            } else return unsupportedCall();
        }

        private <T> T unsupportedCall() {
            throw new UnsupportedOperationException("invalid method call, adding nodes with name only allowed for map node, adding nodes without name only allowed for list nodes");
        }

        public ModelNodeWrapper add(String name, String value) {
            return addObj(name, value);
        }

        public ModelNodeWrapper add(String value) {
            return addObj(value);
        }

        public ModelNodeWrapper add(String name, int value) {
            return addObj(name, value);
        }

        public ModelNodeWrapper add(int value) {
            return addObj(value);
        }

        public ModelNodeWrapper add(String name, boolean value) {
            return addObj(name, value);
        }

        public ModelNodeWrapper add(boolean value) {
            return addObj(value);
        }

        public ModelNodeWrapper add(String name, double value) {
            return addObj(name, value);
        }

        public ModelNodeWrapper add(double value) {
            return addObj(value);
        }


        public ModelNodeWrapper addObj(String name, Object value) {
            if (currentNode instanceof MapModelNode mapModelNode) {
                mapModelNode.add(name, new LeafModelNode<>(value));
                return this;
            } else return unsupportedCall();
        }

        public ModelNodeWrapper addObj(Object value) {
            if (currentNode instanceof ListModelNode listModelNode) {
                listModelNode.add(new LeafModelNode<>(value));
                return this;
            } else return unsupportedCall();
        }


        public ModelNodeWrapper end() {
            if (parent != null) {
                return parent;
            } else {
                return this;
            }
        }

        public ModelNode build() {
            if (parent != null) {
                return parent.build();
            } else {
                return currentNode;
            }
        }
    }


}
