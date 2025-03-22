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

package org.underdocx.doctypes.odf.tools;

import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.w3c.dom.Node;

import java.util.Optional;

public class OdfSectionsWalker implements Enumerator<Node> {

    private final Enumerator<Node> mainEnumerator;

    private Optional<Node> getSectionOfValidNode(Node validNode) {
        return Convenience.buildOptional(result -> {
            if (validNode != null) {
                Nodes.getOwnerDocument(validNode).ifPresent(document ->
                        result.value = document.getFirstChild());
            }
        });
    }

    private boolean shouldIncludeStyles(Node validNode) {
        return Convenience.build(true, result ->
                getSectionOfValidNode(validNode).ifPresent(section -> {
                    if (OdfElement.DOCUMENT_CONTENT.is(section)) {
                        result.value = false;
                    }
                }));
    }


    public OdfSectionsWalker(AbstractOdfContainer<?> doc, Node firstValidNode) {
        Node styleDom = doc.getStylesDom();
        Node contentDom = doc.getContentDom();
        if (shouldIncludeStyles(firstValidNode)) {
            mainEnumerator = Enumerator.of(styleDom, contentDom);
        } else {
            mainEnumerator = Enumerator.of(contentDom);
        }
    }

    private OdfSectionsWalker(OdfSectionsWalker other) {
        this.mainEnumerator = other.mainEnumerator.cloneEnumerator();
    }

    @Override
    public boolean hasNext() {
        return mainEnumerator.hasNext();
    }

    @Override
    public Node next() {
        return mainEnumerator.next();
    }

    @Override
    public Enumerator<Node> cloneEnumerator() {
        return new OdfSectionsWalker(this);
    }

    @Override
    public Optional<Node> inspectNext() {
        return mainEnumerator.inspectNext();
    }
}
