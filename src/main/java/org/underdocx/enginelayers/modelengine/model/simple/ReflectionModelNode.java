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
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.common.tools.Convenience;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionModelNode extends AbstractModelNode<Object> implements ModelNode {

    private final Object containedValue;
    protected ModelNode parent;
    protected Resolver resolver = ReflectionInterceptorRegistry.DEFAULT;

    public ReflectionModelNode(Object object) {
        this.containedValue = object;
        this.parent = null;
    }

    public ReflectionModelNode(Object object, Resolver resolver) {
        this.containedValue = object;
        this.parent = null;
        this.resolver = resolver;
    }

    @Override
    protected ReflectionModelNode create(Object object) {
        return Convenience.also(new ReflectionModelNode(object, resolver), result -> result.parent = this);
    }

    @Override
    public ModelNodeType getType() {
        if (containedValue == null) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Collection<?>) {
            return ModelNodeType.LIST;
        } else if (containedValue instanceof Map<?, ?>) {
            return ModelNodeType.MAP;
        } else if (containedValue instanceof Boolean) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Integer) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Long) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Double) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof Float) {
            return ModelNodeType.LEAF;
        } else if (containedValue instanceof String) {
            return ModelNodeType.LEAF;
        } else {
            return ModelNodeType.MAP;
        }
    }

    @Override
    protected void removeChild(AbstractModelNode<?> node) {
    }

    @Override
    public Object getValue() {
        return containedValue;
    }

    @Override
    public ModelNode getProperty(String name) {
        if (resolver != null) {
            Optional<AbstractModelNode> resolved = resolver.resolve(containedValue, name);
            if (resolved.isPresent()) {
                resolved.get().setParent(this);
                return resolved.get();
            }
        }
        if (getType() != ModelNodeType.MAP) {
            return null;
        } else if (getValue() instanceof Map<?, ?>) {
            return create(((Map<?, ?>) containedValue).get(name));
        } else {
            return Convenience.build(
                    w -> findField(name).ifPresentOrElse(
                            field -> readField(field).ifPresent(
                                    result -> w.value = result),
                            () -> findGetMethod(name).ifPresent(
                                    method -> callGetMethod(method).ifPresent(
                                            result -> w.value = result))));
        }
    }

    @Override
    public ModelNode getProperty(int index) {
        if (getType() != ModelNodeType.LIST) {
            return null;
        } else if (getValue() instanceof List<?>) {
            List<?> list = ((List<?>) containedValue);
            if (index >= list.size()) {
                return null;
            }
            return create(((List<?>) containedValue).get(index));
        } else {
            return null;
        }
    }

    @Override
    public ModelNode getParent() {
        return parent;
    }

    @Override
    public int getSize() {
        if (getValue() instanceof List<?>) {
            return ((List<?>) containedValue).size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasProperty(String name) {
        if (resolver != null && resolver.resolve(containedValue, name).isPresent()) return true;
        if (getType() != ModelNodeType.MAP) {
            return false;
        } else if (getValue() instanceof Map<?, ?>) {
            return ((Map<?, ?>) containedValue).containsKey(name);
        } else {
            return findGetMethod(name).isPresent() || findField(name).isPresent();
        }
    }

    @Override
    public boolean hasProperty(int index) {
        if (getType() != ModelNodeType.LIST) {
            return false;
        } else if (getValue() instanceof List<?>) {
            return ((List<?>) containedValue).size() > index;
        } else {
            return false;
        }
    }

    @Override
    public boolean isNull() {
        return containedValue == null;
    }

    protected Optional<ReflectionModelNode> callGetMethod(Method method) {
        Object result;
        try {
            method.setAccessible(true);
            result = method.invoke(containedValue);
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.error(e);
            return Optional.empty();
        }
        return Optional.of(create(result));
    }

    protected Optional<Method> findGetMethod(String name) {
        return Convenience.findFirst(Arrays.asList(containedValue.getClass().getMethods()), method -> {
            boolean nameFits = method.getName().equalsIgnoreCase(name) ||
                    method.getName().equalsIgnoreCase(("get" + name)) ||
                    method.getName().equalsIgnoreCase(("is" + name));
            boolean returnFits = method.getReturnType() != Void.class;
            boolean parametersFits = method.getParameterTypes().length == 0;
            return nameFits && returnFits && parametersFits;
        });
    }

    protected Optional<ReflectionModelNode> readField(Field field) {
        Object result;
        try {
            field.setAccessible(true);
            result = field.get(containedValue);
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.error(e);
            return Optional.empty();
        }
        return Optional.of(create(result));
    }

    protected Optional<Field> findField(String name) {
        return Convenience.findFirst(Arrays.asList(containedValue.getClass().getFields()),
                field -> field.getName().equalsIgnoreCase(name) ||
                        field.getName().equalsIgnoreCase(("is" + name)));
    }

    public interface Resolver {
        Optional<AbstractModelNode> resolve(Object reflectionObject, String requestedProperty);
    }

    @Override
    public String toString() {
        return "ReflectionModelNode: " + containedValue.getClass().getSimpleName() + " : " + containedValue;
    }
}
