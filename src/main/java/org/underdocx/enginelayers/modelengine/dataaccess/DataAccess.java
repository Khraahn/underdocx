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

package org.underdocx.enginelayers.modelengine.dataaccess;

import org.underdocx.common.types.Pair;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.datapath.DataPath;

import java.util.Optional;

public interface DataAccess {

    Optional<DataNode<?>> getCurrentModelNode();

    DataNode<?> getRootModelNode();

    DataPath getCurrentModelPath();

    void setCurrentModelPath(DataPath dataPath);

    Pair<String, Optional<DataNode<?>>> interpret(String suffix, boolean setAsCurrent);

    @Deprecated
    void setCurrentPath(String modelPath);

    void popVariable(String name);

    Optional<DataNode<?>> getVariable(String name);

    void pushVariable(String name, DataNode<?> value);


}
