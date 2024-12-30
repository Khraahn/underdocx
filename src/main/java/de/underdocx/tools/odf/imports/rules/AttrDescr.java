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

package de.underdocx.tools.odf.imports.rules;

import de.underdocx.environment.UnderdocxEnv;
import de.underdocx.tools.common.StringHash;
import de.underdocx.tools.debug.NodePrinter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AttrDescr extends NodeDescr<AttrDescr> {

    private static final int MAX_CACHE = 5;
    private static Map<String, String> hashCache = new HashMap<>();


    public AttrDescr(String ns, String tagName) {
        super(ns, tagName);
    }

    public AttrDescr(String qualifiedName) {
        super(qualifiedName);
    }

    @Override
    protected AttrDescr createInstance(String name) {
        return new AttrDescr(name);
    }

    public Optional<String> getValue(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String attributeName = attributes.item(i).getNodeName();
                if (matches(new AttrDescr(attributeName))) {
                    return Optional.of(attributes.item(i).getNodeValue());
                }
            }
        }
        return Optional.empty();
    }

    public void setValue(Node node, String value) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String attributeName = attributes.item(i).getNodeName();
                if (matches(new AttrDescr(attributeName))) {
                    attributes.item(i).setNodeValue(value);
                    return;
                }
            }
        }
    }


    private String getHash(String resource) {
        if (hashCache.size() > MAX_CACHE) {
            hashCache.clear();
        }
        String cachedValue = hashCache.get(resource);
        if (cachedValue == null) {
            cachedValue = StringHash.createStringHash32(resource);
            hashCache.put(resource, cachedValue);
        }
        return cachedValue;
    }

    public void modifyValue(String resourceDescr, Node node) {
        getValue(node).ifPresent(value -> {
            UnderdocxEnv.getInstance().logger.trace("attr before rename: " + new NodePrinter(node));
            String hash = getHash(resourceDescr);
            String newValue = hash + "_" + value;
            setValue(node, newValue);
            UnderdocxEnv.getInstance().logger.trace("attr after rename : " + new NodePrinter(node));
        });
    }
}
