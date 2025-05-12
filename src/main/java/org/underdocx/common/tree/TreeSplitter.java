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

import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.tree.nodepath.TextNodePath;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class TreeSplitter {

    private final TextNodeInterpreter textNodeInterpreter;
    private final Node commonAncestor;
    private Node leftTree;
    private final Node leftNode;
    private Node rightTree;
    private final Node rightNode;
    private Node next;
    private Node previous;


    public static void split(Node nodeToSeparate, Node commonAncestor, TextNodeInterpreter textNodeInterpreter) {
        new TreeSplitter(nodeToSeparate, commonAncestor, textNodeInterpreter);
    }

    private TreeSplitter(Node nodeToSeparate, Node commonAncestor, TextNodeInterpreter textNodeInterpreter) {
        this.textNodeInterpreter = textNodeInterpreter;
        this.commonAncestor = commonAncestor;

        List<Node> pathToCommonAncestor = Nodes.getAncestors(nodeToSeparate, commonAncestor);
        Node middleTree = pathToCommonAncestor.get(pathToCommonAncestor.size() - 2);

        leftTree = TextualPlaceholderToolkit.clonePlaceholder(nodeToSeparate, true);
        Nodes.insertNode(middleTree, leftTree, true);

        rightTree = TextualPlaceholderToolkit.clonePlaceholder(nodeToSeparate, false);
        Nodes.insertNode(middleTree, rightTree, false);

        next = nodeToSeparate.getParentNode();
        previous = nodeToSeparate;

        leftNode = leftTree;
        rightNode = rightTree;

        nextStep();
        deleteNodesOrTrees();
    }

    private void nextStep() {
        if (next == commonAncestor) return;
        Node nextLeft = Nodes.cloneNode(next, next, false, false);
        Node nextRight = Nodes.cloneNode(next, next, false, false);
        replaceAndMakeChild(leftTree, nextLeft);
        replaceAndMakeChild(rightTree, nextRight);
        leftTree = nextLeft;
        rightTree = nextRight;

        Wrapper<Boolean> foundPlaceholderAncestor = new Wrapper<>(false);
        List<Node> leftChildren = new ArrayList<>();
        List<Node> rightChildren = new ArrayList<>();
        Nodes.children(next).forEach(child -> {
            if (child == previous) {
                foundPlaceholderAncestor.value = true;
            } else {
                if (foundPlaceholderAncestor.value) {
                    rightChildren.add(child);
                } else {
                    leftChildren.add(child);
                }
            }
        });
        Nodes.addChildren(leftTree, leftChildren, true);
        Nodes.addChildren(rightTree, rightChildren, false);

        previous = next;
        next = next.getParentNode();
        nextStep();
    }

    private void replaceAndMakeChild(Node oldNode, Node newNode) {
        Nodes.replaceNode(oldNode, newNode);
        newNode.appendChild(oldNode);
    }

    private void deleteNodesOrTrees() {
        Nodes.deleteNode(leftNode);
        Nodes.deleteNode(rightNode);
        if (isBlank(leftTree)) {
            Nodes.deleteNode(leftTree);
        }
        if (isBlank(rightTree)) {
            Nodes.deleteNode(rightTree);
        }
    }

    private boolean isBlank(Node node) {
        return TextNodePath.isBlank(node, textNodeInterpreter);
    }

}
