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

package de.underdocx.tools.odf.imports.internal;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.odf.imports.rules.AttrDescr;
import de.underdocx.tools.odf.imports.rules.ProviderDescr;
import de.underdocx.tools.odf.imports.rules.Renameable;
import org.w3c.dom.Node;

import java.util.*;

/**
 * finds all nodes that provide style information. Required for renaming style names
 */
public class ProviderScanner implements Renameable {

    private final List<ProviderDescr> providerDescriptions;
    private Map<String, Pair<Node, AttrDescr>> cache = new HashMap<>();

    public ProviderScanner(OdtContainer doc, List<ProviderDescr> providerDescr) {
        this.providerDescriptions = providerDescr;
        providerDescr.forEach(pd ->
                pd.getPathDescr().findAll(doc).forEach(node -> {
                    AttrDescr providingAttr = pd.getProvidingAttr();
                    providingAttr.getValue(node).ifPresent(value -> {
                        cache.put(value, new Pair<>(node, providingAttr));
                    });
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
        getProviderEntries().entrySet().forEach(entry -> {
            Optional<String> oNewValue = entry.getValue().right.modifyValue(resource, entry.getValue().left);
            if (oNewValue.isPresent()) {
                newCache.put(oNewValue.get(), new Pair<>(entry.getValue().left, entry.getValue().right));
            } else {
                newCache.put(entry.getKey(), new Pair<>(entry.getValue().left, entry.getValue().right));
            }
        });
        cache = Collections.unmodifiableMap(newCache);
    }
}
