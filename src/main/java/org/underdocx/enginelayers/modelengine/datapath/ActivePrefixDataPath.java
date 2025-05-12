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

import org.underdocx.enginelayers.modelengine.datapath.elements.DataPathElement;
import org.underdocx.enginelayers.modelengine.datapath.elements.DatalPathElementType;
import org.underdocx.enginelayers.modelengine.datapath.elements.PropertyDataPathElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ActivePrefixDataPath extends DataPath {

    private Supplier<String> prefixSupplier = null;

    public ActivePrefixDataPath(Supplier<String> prefixSupplier, List<DataPathElement> elements) {
        super(elements);
        setPrefix(prefixSupplier);
    }

    public ActivePrefixDataPath(Supplier<String> prefixSupplier, DataPath dataPath) {
        this(prefixSupplier, dataPath.elements);
    }

    public ActivePrefixDataPath(String prefix, DataPath dataPath) {
        this(() -> prefix, dataPath.elements);
    }

    public ActivePrefixDataPath(Supplier<String> prefixSupplier, String pathToParse) {
        this(prefixSupplier, new DataPath(pathToParse));
    }

    public ActivePrefixDataPath(ActivePrefixDataPath toClone) {
        this(toClone.prefixSupplier, new ArrayList<>(toClone.elements));
    }


    public void setPrefix(Supplier<String> prefixSupplier) {
        this.prefixSupplier = prefixSupplier;
    }

    public void setPrefix(String prefix) {
        setPrefix(() -> prefix);
    }

    @Override
    public void interpret(String path) {
        List<DataPathElement> subPath = new DataPath(path).getElements();
        String prefix = prefixSupplier != null ? prefixSupplier.get() : null;
        if (prefix != null
                && subPath.size() > 0
                && subPath.get(0).getType() == DatalPathElementType.PROPERTY
                && ((PropertyDataPathElement) subPath.get(0)).property().equals(prefix)) {
            subPath.remove(0);
        }
        super.interpret(subPath);
    }

    public DataPath clone() {
        return new ActivePrefixDataPath(this);
    }

}
