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

package org.underdocx.doctypes.odf.tools.importer.rules;

import org.underdocx.common.types.Pair;
import org.w3c.dom.Node;

/**
 * abstract description of a node (optional namespace prefix and name)
 * Can be used for Elements or Attributes
 */
public abstract class NodeDescr<T extends NodeDescr<T>> implements NodeFilter {
    private String nameSpace = null;
    private String singleNodeName;

    public NodeDescr(String ns, String singleNodeName) {
        this.singleNodeName = singleNodeName;
        this.nameSpace = ns;
    }

    private NodeDescr(Pair<String, String> nsAndName) {
        this.singleNodeName = nsAndName.right;
        this.nameSpace = nsAndName.left;
    }

    public NodeDescr(String qualifiedName) {
        this(parse(qualifiedName));
    }


    protected static Pair<String, String> parse(String qualifiedTagName) {
        String[] split = qualifiedTagName.split(":");
        if (split.length == 1) {
            return new Pair<>(null, split[0]);
        } else {
            return new Pair<>(split[0], split[1]);
        }
    }

    protected abstract T createInstance(String name);

    public boolean matches(NodeDescr other) {
        if (this.nameSpace != null && !this.nameSpace.equals(other.nameSpace)) return false;
        return this.singleNodeName.equals(other.singleNodeName) && this.getClass().getName().equals(other.getClass().getName());
    }

    public boolean test(Node node) {
        return matches(createInstance(node.getNodeName()));
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getSingleNodeName() {
        return singleNodeName;
    }
}
