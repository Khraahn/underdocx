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

import org.w3c.dom.Node;

public class ConsumerDescr {
    private final NodeFilter tagDescr;
    private final AttrDescr consumerAttr;

    public ConsumerDescr(NodeFilter tagDescr, AttrDescr consumerAttr) {
        this.tagDescr = tagDescr;
        this.consumerAttr = consumerAttr;
    }

    public ConsumerDescr(AttrDescr consumerAttr) {
        this(null, consumerAttr);
    }

    public void modifyOnMatch(String resource, Node node) {
        if (tagDescr == null || tagDescr.test(node)) {
            consumerAttr.modifyValue(resource, node);
        }
    }

    public NodeFilter getTagDescr() {
        return tagDescr;
    }

    public AttrDescr getConsumerAttr() {
        return consumerAttr;
    }
}
