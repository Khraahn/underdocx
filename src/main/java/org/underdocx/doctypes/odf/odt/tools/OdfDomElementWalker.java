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

package org.underdocx.doctypes.odf.odt.tools;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

import static org.underdocx.common.tools.Convenience.also;
import static org.underdocx.common.tools.Convenience.getOrDefault;

public class OdfDomElementWalker<T extends OdfElement> implements Enumerator<T> {

    private final Iterator<TreeWalker> treeWalkers;
    private TreeWalker currentTreeWalker;
    private final boolean skipChildren;
    private T next;
    private Predicate<TreeWalker.VisitState> filter;

    public OdfDomElementWalker(AbstractOdfContainer<?> doc, boolean skipFoundNodesChildren, Class<T> searchType) {
        this.filter = state -> state != null && state.isBeginVisit() && searchType.isAssignableFrom(state.getNode().getClass());
        Node contentDom = doc.getContentDom();
        Node styleDom = doc.getStylesDom();
        this.treeWalkers = Arrays.asList(
                new TreeWalker(styleDom, styleDom),
                new TreeWalker(contentDom, contentDom)
        ).iterator();
        this.currentTreeWalker = treeWalkers.next();
        this.skipChildren = skipFoundNodesChildren;
        findNext();
    }

    private void findNext() {
        if (next != null && skipChildren) {
            currentTreeWalker.nextSkipChildren();
            next = null;
        }
        next = (T) getOrDefault(currentTreeWalker.next(filter), TreeWalker.VisitState::getNode, null);
        if (next == null && treeWalkers.hasNext()) {
            currentTreeWalker = treeWalkers.next();
            findNext();
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        return also(next, n -> findNext());
    }
}
