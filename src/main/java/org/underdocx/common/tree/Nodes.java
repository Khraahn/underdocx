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


package org.underdocx.common.tree;

import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Wrapper;
import org.w3c.dom.*;

import java.util.*;
import java.util.function.Predicate;

/**
 * Collection of helper methods to make W3C API more convenient
 */
public class Nodes {

    /*

      -- Collections --

     */

    @Deprecated
    public static List<Node> toList(NodeList nodeList) {
        return Convenience.buildList(result -> {
            for (int i = 0; i < nodeList.getLength(); i++) {
                result.add(nodeList.item(i));
            }
        });
    }

    public static Enumerator<Node> getChildren(Node node) {
        return new Enumerator<>() {
            private NodeList nodeList = node.getChildNodes();
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return nodeList.getLength() > counter;
            }

            @Override
            public Node next() {
                return nodeList.item(counter++);
            }
        };
    }

    public static Enumerator<Node> getChildren(Node node, Predicate<Node> filter) {

        return new Enumerator<>() {
            private Enumerator<Node> inner = getChildren(node);
            private Node next = findNext();

            private Node findNext() {
                while (inner.hasNext()) {
                    Node toTest = inner.next();
                    if (filter.test(toTest)) {
                        return toTest;
                    }
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Node next() {
                Node result = next;
                next = findNext();
                return result;
            }
        };
    }

    @Deprecated
    public static List<Node> children(Node node) {
        return toList(node.getChildNodes());
    }

    @Deprecated
    public static Map<String, String> attributes(Node node) {
        NamedNodeMap mapToConvert = node.getAttributes();
        if (mapToConvert == null) return new HashMap<>();
        Map<String, String> result = new LinkedHashMap<>();
        for (int i = 0; i < mapToConvert.getLength(); i++) {
            result.put(mapToConvert.item(i).getNodeName(), mapToConvert.item(i).getNodeValue());
        }
        return result;
    }


    /*

      -- search nodes --

     */

    public static List<Node> findDescendantNodes(Node start, String tagName, boolean skipChildrenOfFoundNodes) {
        return findDescendantNodes(start, node -> node.getNodeName().equals(tagName), skipChildrenOfFoundNodes);
    }


    public static Optional<Node> findFirstDescendantNode(Node start, String tagName) {
        return findFirstDescendantNode(start, node -> node.getNodeName().equals(tagName));
    }

    public static List<Node> findDescendantNodes(Node start, Predicate<Node> filter, boolean skipChildrenOfFoundNodes) {
        return Convenience.buildList(result -> {
            boolean found = false;
            if (filter.test(start)) {
                result.add(start);
                found = true;
            }
            if (!found || (found && !skipChildrenOfFoundNodes)) {
                getChildren(start).forEach(element -> result.addAll(findDescendantNodes(element, filter, skipChildrenOfFoundNodes)));
            }
        });
    }


    public static Optional<Node> findFirstDescendantNode(Node start, Predicate<Node> filter) {
        return (filter.test(start))
                ? Optional.of(start)
                : Convenience.findFirstPresent(children(start), child -> findFirstDescendantNode(child, filter));
    }

    public static Optional<Node> findAscendantNode(Node start, String tagName) {
        return findAscendantNode(start, node -> node.getNodeName().equals(tagName));
    }

    public static Optional<Node> findAscendantNode(Node start, Predicate<Node> filter) {
        if (filter.test(start)) {
            return Optional.of(start);
        } else {
            Node parent = start.getParentNode();
            return parent == null ? Optional.empty() : findAscendantNode(parent, filter);
        }
    }

    public static Optional<Node> findOldestAncestorNode(Node start, Predicate<Node> filter) {
        List<Node> ancestors = Convenience.reverse(getAncestors(start, null));
        for (int i = 0; i < ancestors.size(); i++) {
            if (filter == null || filter.test(ancestors.get(i))) {
                return Optional.of(ancestors.get(i));
            }
        }
        return Optional.empty();
    }

    public static Optional<Node> findAncestorChild(Node nodeToStart, Node ancestorNode) {
        return findAncestorChild(nodeToStart, (node -> ancestorNode == node));
    }

    public static Optional<Node> findAncestorChild(Node nodeToStart, Predicate<Node> ancestorNode) {
        Node currentParent = nodeToStart.getParentNode();
        Node currentChild = nodeToStart;
        while (currentParent != null && !ancestorNode.test(currentParent)) {
            currentParent = currentParent.getParentNode();
            currentChild = currentChild.getParentNode();
        }
        return currentParent == null ? Optional.empty() : Optional.of(currentChild);
    }


    public static List<Node> getAncestors(Node node, Node limit) {
        ArrayList<Node> result = new ArrayList<>();
        if (node == limit) {
            return Convenience.plus(result, node);
        }
        Node parent = node;
        while (parent != null && parent != limit) {
            parent = Convenience.add(result, parent).getParentNode();
        }
        if (parent != null) return Convenience.plus(result, parent);
        return result;
    }

    public static Optional<Node> findCommonAncestor(Node node1, Node node2) {
        Node currentCommon = null;
        List<Node> path1 = Convenience.reverse(getAncestors(node1, null));
        List<Node> path2 = Convenience.reverse(getAncestors(node2, null));
        for (int i = 0; i < Math.min(path1.size(), path2.size()); i++) {
            if (path1.get(i) == path2.get(i)) {
                currentCommon = path1.get(i);
            } else {
                return Optional.ofNullable(currentCommon);
            }
        }
        return Optional.empty();
    }

    @Deprecated
    public static List<Node> getSiblings(Node firstNode, Node limit) {
        return getSiblingsIterator(firstNode, limit).collect();
    }

    public static Enumerator<Node> getSiblingsIterator(Node firstNode, Node limit) {
        return new Enumerator<>() {
            Node next = firstNode;
            Boolean limitReached = false;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Node next() {
                return Convenience.also(next, n -> {
                    if (limitReached) {
                        next = null;
                    } else {
                        next = next.getNextSibling();
                        if (next == limit) {
                            limitReached = true;
                        }
                    }
                });
            }
        };
    }

    public static int compareNodePositions(Node node1, Node node2) {
        List<Node> path1 = Convenience.reverse(getAncestors(node1, null));
        List<Node> path2 = Convenience.reverse(getAncestors(node2, null));
        for (int treeDepth = 0; treeDepth < path1.size(); treeDepth++) {
            if (treeDepth >= path2.size()) {
                return -1; // a->b*->c  vs  a->b*
            }
            if (path1.get(treeDepth) == path2.get(treeDepth)) {
                continue; // a->b*->c  vs  a->b*->x
            }
            Node child1 = path1.get(treeDepth);
            Node child2 = path2.get(treeDepth);
            while (child1 != null) {
                child1 = child1.getNextSibling();
                if (child1 == child2) {
                    return -1;
                }
            }
            return 1;
        }
        return path1.size() == path2.size()
                ? 0  // a->b*  vs   a->b*
                : 1; // a->b*  vs   a->b*->c
    }

    public static Optional<Node> findNextNode(Node node, boolean skipTxtNode) {
        Node result = null;
        TreeWalker walker = new TreeWalker(node, null, null);
        while (result == null && walker.hasNext()) {
            TreeWalker.VisitState state = walker.next();
            if (state.getNode() == node) {
                state = walker.nextSkipChildren();
            }
            if (state != null && state.isBeginVisit()) {
                result = state.getNode();
            }
            if (result != null && skipTxtNode && result.getNodeType() == Node.TEXT_NODE) {
                result = null;
            }
        }
        return Optional.ofNullable(result);
    }

    public static Optional<Node> findPreviousNode(Node node) {
        Node currentNode = node;
        // Walk up until a previous sibling has been found
        while (currentNode.getPreviousSibling() == null) {
            currentNode = currentNode.getParentNode();
            if (currentNode == null) return Optional.empty();
        }
        currentNode = currentNode.getPreviousSibling();
        // find the last right child
        while (currentNode.hasChildNodes()) {
            currentNode = currentNode.getLastChild();
        }
        return Optional.of(currentNode);
    }

    public static boolean containsNode(Node tree, Node descendant) {
        return Nodes.findFirstDescendantNode(tree, node -> node == descendant).isPresent();
    }


    public static Optional<Document> getOwnerDocument(Node node) {
        if (node instanceof Document doc) {
            return Optional.of(doc);
        }
        return Optional.ofNullable(node.getOwnerDocument());
    }



    /*

      -- insert/reorder/delete nodes --

     */

    public static void insertNodes(Node refNode, List<? extends Node> toInsert, boolean insertBefore) {
        // TODO improve / speedup
        if (insertBefore) {
            final Wrapper<Node> last = new Wrapper<>(refNode);
            Convenience.reverse(toInsert).forEach(element -> {
                refNode.getParentNode().insertBefore(element, last.value);
                last.value = element;
            });
        } else {
            Convenience.reverse(toInsert).forEach(element -> {
                if (refNode.getNextSibling() == null) {
                    refNode.getParentNode().appendChild(element);
                } else {
                    refNode.getParentNode().insertBefore(element, refNode.getNextSibling());
                }
            });
        }
    }

    public static void insertNode(Node refNode, Node toInsert, boolean insertBefore) {
        if (insertBefore) {
            refNode.getParentNode().insertBefore(toInsert, refNode);
        } else {
            if (refNode.getNextSibling() == null) {
                refNode.getParentNode().appendChild(toInsert);
            } else {
                refNode.getParentNode().insertBefore(toInsert, refNode.getNextSibling());
            }
        }
    }

    public static void insertBefore(Node refNode, Node toInsert) {
        insertNode(refNode, toInsert, true);
    }

    public static void insertAfter(Node refNode, Node toInsert) {
        insertNode(refNode, toInsert, false);
    }


    public static void addChildren(Node parent, List<Node> toInsert, boolean atBegin) {
        if (parent.hasChildNodes() && atBegin) {
            insertNodes(parent.getFirstChild(), toInsert, true);
        } else {
            new ArrayList<>(toInsert).forEach(parent::appendChild);
        }
    }

    public static void deleteNodes(Collection<Node> nodes) {
        nodes.forEach(element -> element.getParentNode().removeChild(element));
    }

    public static void deleteNode(Node node) {
        if (node.getParentNode() != null)
            node.getParentNode().removeChild(node);
    }

    public static void deleteChildren(Node node) {
        deleteNodes(children(node));
    }

    public static void setNodeText(Node node, String text) {
        deleteChildren(node);
        node.appendChild(node.getOwnerDocument().createTextNode(text));
    }

    public static void replaceNode(Node oldNode, Node newNode) {
        insertBefore(oldNode, newNode);
        oldNode.getParentNode().removeChild(oldNode);
    }

    public static void insertBeforeFirstChild(Node parent, Node toAdd) {
        if (parent.hasChildNodes()) {
            parent.insertBefore(toAdd, parent.getFirstChild());
        } else {
            parent.appendChild(toAdd);
        }
    }




    /*

      -- clone/import nodes --

     */

    public static Node importNode(Node parent, Node toImport) {
        Node result = parent.getOwnerDocument().adoptNode(toImport.cloneNode(true));
        parent.appendChild(result);
        return result;
    }

    public static List<Node> importNodes(Node parent, Collection<Node> toImport) {
        return Convenience.buildList(result -> toImport.forEach(element -> result.add(importNode(parent, element))));
    }

    public static Node cloneNode(Node refNode, Node toClone, boolean insertBefore, boolean deepClone) {
        Node clone = toClone.cloneNode(deepClone);
        insertNode(refNode, clone, insertBefore);
        return clone;
    }

    public static List<Node> cloneNodes(Node refNode, List<Node> toClone, boolean insertBefore, boolean deepClone) {
        return Convenience.buildList(result -> {
            Node currentRefNode = refNode;
            List<Node> orderedNodesToClone = insertBefore ? Convenience.reverse(toClone) : toClone;
            for (Node currentNodeToClone : orderedNodesToClone) {
                Node clonedNode = cloneNode(currentRefNode, currentNodeToClone, insertBefore, deepClone);
                currentRefNode = clonedNode;
                result.add(clonedNode);
            }
        });

    }

    public static List<Node> cloneChildren(Node parentNode, List<Node> toClone, boolean atBegin, boolean deepClone) {
        return Convenience.buildList(result -> {
            toClone.forEach(element -> result.add(cloneNode(parentNode, element, false, deepClone)));
            addChildren(parentNode, result, atBegin);
        });
    }


    /*

    -- more helpers

     */
    public static boolean isText(Node... nodes) {
        return Convenience.all(Arrays.asList(nodes), node -> node.getNodeType() == Node.TEXT_NODE);
    }


    public static void appendNodeText(Node node, String text) {
        Document document = node.getOwnerDocument();
        Text textNode = document.createTextNode(text);
        node.appendChild(textNode);
    }
}

