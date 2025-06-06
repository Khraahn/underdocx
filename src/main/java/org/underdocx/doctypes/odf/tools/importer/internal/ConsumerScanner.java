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

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.enumerator.EnumeratorOfEnumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.tools.importer.rules.ConsumerDescr;
import org.underdocx.doctypes.odf.tools.importer.rules.Renameable;

import java.util.List;

/**
 * finds all nodes that contain attribute with style name references. Required for renaming style names
 */
public class ConsumerScanner implements Renameable {

    private final AbstractOdfContainer<?> doc;
    private final List<ConsumerDescr> consumerDescr;

    public ConsumerScanner(AbstractOdfContainer<?> doc, List<ConsumerDescr> consumerDescr) {
        this.doc = doc;
        this.consumerDescr = consumerDescr;
    }

    @Override
    public void rename(String resource) {
        OdfContentDom c = doc.getContentDom();
        OdfStylesDom s = doc.getStylesDom();
        Enumerator<TreeWalker.VisitState> treeWalkers = new EnumeratorOfEnumerator<>(new TreeWalker(s, s), new TreeWalker(c, c));
        while (treeWalkers.hasNext()) {
            Convenience.also(treeWalkers.next(), next -> {
                if (next.isBeginVisit()) {
                    consumerDescr.forEach(cd -> cd.modifyOnMatch(resource, next.getNode()));
                }
            });
        }
    }

}
