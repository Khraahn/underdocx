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

package de.underdocx.enginelayers.modelengine;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.BaseEngine;
import de.underdocx.enginelayers.baseengine.EngineAccess;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.modelengine.internal.MSelectionWrapper;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;
import de.underdocx.enginelayers.modelengine.modelpath.ModelPath;
import de.underdocx.enginelayers.modelengine.modelpath.elements.ModelPathElement;
import de.underdocx.enginelayers.modelengine.modelpath.elements.PropertyModelPathElement;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Pair;
import org.w3c.dom.Node;

import java.util.*;

public class ModelEngine<C extends DocContainer<D>, D> extends BaseEngine {

    protected ModelNode modelRoot = new MapModelNode();
    protected ModelPath currentModelPath = new ModelPath();
    protected Map<String, Deque<ModelNode>> variableStacks = new HashMap<>();


    public ModelEngine(DocContainer doc) {
        super(doc);
    }

    public void setModelRoot(ModelNode modelRoot) {
        this.modelRoot = modelRoot;
        this.currentModelPath = new ModelPath();
    }

    public void pushVariable(String varName, ModelNode value) {
        new ModelEngineModelAccess().pushVariable(varName, value);
    }

    @Override
    protected Selection createSelection(PlaceholdersProvider provider, Node node, EngineAccess engineAccess) {
        Selection baseSelection = super.createSelection(provider, node, engineAccess);
        return new MSelectionWrapper(baseSelection, new ModelEngineModelAccess());
    }

    private class ModelEngineModelAccess implements ModelAccess {

        @Override
        public Optional<ModelNode> getCurrentModelNode() {
            return currentModelPath.interpret(modelRoot);
        }

        @Override
        public ModelNode getRootModelNode() {
            return modelRoot;
        }

        @Override
        public ModelPath getCurrentModelPath() {
            return currentModelPath.clone();
        }

        @Override
        public void setCurrentModelPath(ModelPath modelPath) {
            currentModelPath = modelPath;
        }

        public void popVariable(String name) {
            Deque<ModelNode> stack = variableStacks.get(name);
            if (stack != null) {
                stack.pop();
            }
        }

        public Optional<ModelNode> getVariable(String name) {
            ModelPath path = new ModelPath(name);
            ModelPathElement first = (path.getElements().size() > 0) ? path.getElements().get(0) : null;
            Problems.INVALID_VALUE.checkNot(
                    (first == null || !(first instanceof PropertyModelPathElement)), null, name);
            Deque<ModelNode> stack = variableStacks.get(((PropertyModelPathElement) first).getProperty());
            if (stack != null) {
                ModelNode varValue = stack.peek();
                if (varValue != null) {
                    path.subPath(1);
                    return path.interpret(varValue);
                }
            }
            return Optional.empty();
        }

        public void pushVariable(String name, ModelNode value) {
            Problems.INVALID_VALUE.checkNot(
                    (name == null || name.isBlank() || name.contains(".") || name.contains("[") || name.contains("^") || name.contains("<")),
                    null, name);
            Deque<ModelNode> stack = variableStacks.get(name);
            if (stack == null) {
                stack = new LinkedList<>();
                variableStacks.put(name, stack);
            }
            stack.push(value);
        }

        @Override
        public Pair<String, Optional<ModelNode>> interpret(String suffix, boolean setAsCurrent) {
            ModelPath p = setAsCurrent ? currentModelPath : currentModelPath.clone();
            p.interpret(suffix);
            Optional<ModelNode> subNode = p.interpret(modelRoot);
            return new Pair<>(p.toString(), subNode);
        }

        @Override
        public void setCurrentPath(String modelPath) {
            currentModelPath = new ModelPath(modelPath);
        }
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, MCommandHandler<C, X, D> commandHandler) {
        return super.registerCommandHandler(provider, commandHandler);
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider.Factory<C, X, D> provider, MCommandHandler<C, X, D> commandHandler) {
        return super.registerCommandHandler(provider, commandHandler);
    }
}
