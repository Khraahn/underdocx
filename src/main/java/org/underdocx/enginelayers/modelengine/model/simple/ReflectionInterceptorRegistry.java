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

import org.underdocx.common.types.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReflectionInterceptorRegistry implements ReflectionDataNode.Resolver {

    public static ReflectionInterceptorRegistry DEFAULT = new ReflectionInterceptorRegistry();

    private Map<Pair<String, String>, ReflectionDataNode.Resolver> interceptors = new HashMap<>();

    public void register(Class<?> clazz, String property, ReflectionDataNode.Resolver interceptor) {
        interceptors.put(new Pair<>(getClassName(clazz), property), interceptor);
    }

    public void register(Collection<Class<?>> classes, Collection<String> properties, ReflectionDataNode.Resolver interceptor) {
        classes.forEach(clazz -> properties.forEach(property -> register(clazz, property, interceptor)));
    }

    private String getClassName(Class<?> clazz) {
        return clazz.getName();
    }

    @Override
    public Optional<AbstractDataNode> resolve(Object reflectionObject, String requestedProperty) {
        ReflectionDataNode.Resolver interceptor = interceptors.get(new Pair(getClassName(reflectionObject.getClass()), requestedProperty));
        if (interceptor != null) {
            return interceptor.resolve(reflectionObject, requestedProperty);
        }
        return Optional.empty();
    }
}
