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

package org.underdocx.enginelayers.modelengine.datapath;

import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.datapath.elements.DataPathElement;
import org.underdocx.enginelayers.modelengine.datapath.parser.DataPathCodec;
import org.underdocx.environment.err.Problems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataPath {

    private static final DataPathCodec codec = new DataPathCodec();

    protected List<DataPathElement> elements = new ArrayList<>();

    public DataPath(List<DataPathElement> elements) {
        this.elements.addAll(elements);
    }

    public DataPath(DataPath toClone) {
        this.elements.addAll(toClone.elements);
    }

    public DataPath() {
    }

    public DataPath(String toParse) {
        this.elements = Problems.CODEC_PARSE_ERROR.exec(() -> codec.parse(toParse), null, toParse).getElements();
    }

    public List<DataPathElement> getElements() {
        return new ArrayList<>(elements);
    }

    public String toString() {
        return codec.getTextContent(this);
    }

    public static Optional<DataNode<?>> interpret(String path, DataNode<?> node) {
        return new DataPath(path).interpret(node);
    }

    public Optional<DataNode<?>> interpret(DataNode<?> rootDataNode) {
        return Convenience.buildOptional(rootDataNode, w -> elements.forEach(element -> {
            if (w.value != null) {
                w.value = element.interpret(w.value).orElse(null);
            }
        }));
    }

    public void interpret(String path) {
        List<DataPathElement> subPath = new DataPath(path).getElements();
        interpret(subPath);
    }

    protected void interpret(List<DataPathElement> subPath) {
        subPath.forEach(subElement -> subElement.interpret(elements));
    }

    public void subPath(int index) {
        elements = elements.subList(index, elements.size());
    }

    public DataPath clone() {
        return new DataPath(this);
    }


}
