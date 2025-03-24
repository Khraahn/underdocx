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

package org.underdocx.enginelayers.modelengine;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.BaseEngine;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;
import org.underdocx.enginelayers.modelengine.datapath.DataPath;
import org.underdocx.enginelayers.modelengine.datapath.elements.DataPathElement;
import org.underdocx.enginelayers.modelengine.datapath.elements.PropertyDataPathElement;
import org.underdocx.enginelayers.modelengine.internal.MSelectionWrapper;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.*;

public class ModelEngine<C extends DocContainer<D>, D> extends BaseEngine<C, D> {

    protected DataNode<?> modelRoot = new MapDataNode();
    protected DataPath currentDataPath = new DataPath();
    protected Map<String, Deque<DataNode<?>>> variableStacks = new HashMap<>();


    public ModelEngine(C doc) {
        super(doc);
    }

    public void setModelRoot(DataNode<?> modelRoot) {
        this.modelRoot = modelRoot;
        this.currentDataPath = new DataPath();
    }

    public void pushVariable(String varName, DataNode<?> value) {
        new ModelEngineDataAccess().pushVariable(varName, value);
    }

    public Optional<DataNode<?>> getVariable(String varName) {
        return new ModelEngineDataAccess().getVariable(varName);
    }

    @Override
    protected Selection createSelection(PlaceholdersProvider provider, Node node, EngineAccess engineAccess) {
        Selection baseSelection = super.createSelection(provider, node, engineAccess);
        return new MSelectionWrapper(baseSelection, new ModelEngineDataAccess());
    }

    private class ModelEngineDataAccess implements DataAccess {

        @Override
        public Optional<DataNode<?>> getCurrentModelNode() {
            return currentDataPath.interpret(modelRoot);
        }

        @Override
        public DataNode<?> getRootModelNode() {
            return modelRoot;
        }

        @Override
        public DataPath getCurrentModelPath() {
            return currentDataPath.clone();
        }

        @Override
        public void setCurrentModelPath(DataPath dataPath) {
            currentDataPath = dataPath;
        }

        public void popVariable(String name) {
            Deque<DataNode<?>> stack = variableStacks.get(name);
            if (stack != null) {
                stack.pop();
            }
        }

        public Optional<DataNode<?>> getVariable(String name) {
            DataPath path = new DataPath(name);
            DataPathElement first = (path.getElements().size() > 0) ? path.getElements().get(0) : null;
            Problems.INVALID_VALUE.checkNot(
                    (first == null || !(first instanceof PropertyDataPathElement)), null, name);
            Deque<DataNode<?>> stack = variableStacks.get(((PropertyDataPathElement) first).getProperty());
            if (stack != null) {
                DataNode<?> varValue = stack.peek();
                if (varValue != null) {
                    path.subPath(1);
                    return path.interpret(varValue);
                }
            }
            return Optional.empty();
        }

        public void pushVariable(String name, DataNode<?> value) {
            Problems.INVALID_VALUE.checkNot(
                    (name == null || name.isBlank() || name.contains(".") || name.contains("[") || name.contains("^") || name.contains("<")),
                    null, name);
            Deque<DataNode<?>> stack = variableStacks.get(name);
            if (stack == null) {
                stack = new LinkedList<>();
                variableStacks.put(name, stack);
            }
            stack.push(value);
        }

        @Override
        public Pair<String, Optional<DataNode<?>>> interpret(String suffix, boolean setAsCurrent) {
            DataPath p = setAsCurrent ? currentDataPath : currentDataPath.clone();
            p.interpret(suffix);
            Optional<DataNode<?>> subNode = p.interpret(modelRoot);
            return new Pair<>(p.toString(), subNode);
        }

        @Override
        public void setCurrentPath(String modelPath) {
            currentDataPath = new DataPath(modelPath);
        }
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, MCommandHandler<C, X, D> commandHandler) {
        return super.registerCommandHandler(provider, commandHandler);
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider.Factory<C, X, D> provider, MCommandHandler<C, X, D> commandHandler) {
        return super.registerCommandHandler(provider, commandHandler);
    }


}
