package org.underdocx.doctypes.txt.placeholders;

import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.enumerator.EnumeratorOfEnumerator;
import org.underdocx.common.enumerator.InspectableEnumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.SimpleTreeWalker;
import org.underdocx.doctypes.txt.TxtContainer;
import org.w3c.dom.Node;

import java.util.Optional;

class TxtSectionEnumerator implements InspectableEnumerator<Node> {

    private final InspectableEnumerator<Node> inner;

    private Enumerator<Node> getSectionOfValidNodeAsEnumerator(Node validNode) {
        return Convenience.build(Enumerator.empty(), result -> {
            if (validNode != null) {
                Nodes.findAscendantNode(validNode, "p").ifPresent(section ->
                        result.value = Enumerator.of(section));
            }
        });
    }

    private Optional<Node> getSectionOfValidNode(Node validNode) {
        if (validNode != null) {
            return Nodes.findOldestAncestorNode(validNode, node -> node.getNodeName().equals("p"));
        } else {
            return Optional.empty();
        }
    }

    public InspectableEnumerator<Node> ensureCurrentSection(Node firstValid, InspectableEnumerator<Node> sections) {
        return Convenience.build(sections, result ->
                getSectionOfValidNode(firstValid).ifPresent(sectionContainingValidNode -> {
                    Optional<Node> nextSection = sections.inspectNext();
                    if (nextSection.isEmpty()) {
                        result.value = new EnumeratorOfEnumerator<>(Enumerator.of(sectionContainingValidNode), sections);
                    } else {
                        if (nextSection.get() != sectionContainingValidNode) {
                            result.value = new EnumeratorOfEnumerator<>(Enumerator.of(sectionContainingValidNode), sections);
                        }
                    }
                })
        );
    }

    public TxtSectionEnumerator(TxtContainer doc, Node firstValid) {
        Optional<Node> root = Nodes.findFirstDescendantNode(doc.getDocument().getDoc(), "root");
        if (root.isPresent()) {
            Node r = root.get();
            inner = ensureCurrentSection(
                    firstValid,
                    new SimpleTreeWalker(r, r, firstValid, SimpleTreeWalker.buildFilter(node -> node.getNodeName() == "p"), true)
            );
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

    @Override
    public Optional<Node> inspectNext() {
        return inner.inspectNext();
    }
}