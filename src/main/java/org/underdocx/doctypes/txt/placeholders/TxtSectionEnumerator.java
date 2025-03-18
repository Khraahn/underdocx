package org.underdocx.doctypes.txt.placeholders;

import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.txt.TxtContainer;
import org.w3c.dom.Node;

import java.util.Optional;

class TxtSectionEnumerator implements Enumerator<Node> {

    private final Enumerator<Node> inner;

    public TxtSectionEnumerator(TxtContainer doc) {
        Optional<Node> root = Nodes.findFirstDescendantNode(doc.getDocument().getDoc(), "root");
        if (root.isPresent()) {
            inner = Nodes.getChildren(root.get());
        } else {
            inner = Enumerator.empty();
        }
    }

    @Override
    public boolean hasNext() {
        return inner.hasNext();
    }

    @Override
    public Node next() {
        return inner.next();
    }
}