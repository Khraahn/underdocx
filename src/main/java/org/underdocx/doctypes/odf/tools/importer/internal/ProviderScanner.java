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

package org.underdocx.doctypes.odf.tools.importer.internal;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.tools.importer.rules.AttrDescr;
import org.underdocx.doctypes.odf.tools.importer.rules.ProviderDescr;
import org.underdocx.doctypes.odf.tools.importer.rules.Renameable;
import org.w3c.dom.Node;

import java.util.*;

/**
 * finds all nodes that provide style information. Required for renaming style names
 */
public class ProviderScanner implements Renameable {

    private Map<String, Pair<Node, AttrDescr>> cache = new HashMap<>();

    public ProviderScanner(AbstractOdfContainer<?> doc, List<ProviderDescr> providerDescr) {
        providerDescr.forEach(pd ->
                pd.pathDescr().findAll(doc).forEach(node -> {
                    AttrDescr providingAttr = pd.providingAttr();
                    providingAttr.getValue(node).ifPresent(value ->
                            cache.put(value, new Pair<>(node, providingAttr)));
                }));
        cache = Collections.unmodifiableMap(cache);
    }


    public Map<String, Pair<Node, AttrDescr>> getProviderEntries() {
        return cache;
    }

    public Optional<Pair<Node, AttrDescr>> findProviderByValue(String value) {
        return Optional.ofNullable(cache.get(value));
    }

    @Override
    public void rename(String resource) {
        Map<String, Pair<Node, AttrDescr>> newCache = new HashMap<>();
        getProviderEntries().forEach((key, value) -> {
            Optional<String> oNewValue = value.right.modifyValue(resource, value.left);
            if (oNewValue.isPresent()) {
                newCache.put(oNewValue.get(), new Pair<>(value.left, value.right));
            } else {
                newCache.put(key, new Pair<>(value.left, value.right));
            }
        });
        cache = Collections.unmodifiableMap(newCache);
    }
}
