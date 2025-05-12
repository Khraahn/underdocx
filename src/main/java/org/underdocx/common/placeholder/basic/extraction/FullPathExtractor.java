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

package org.underdocx.common.placeholder.basic.extraction;

import org.underdocx.common.enumerator.AbstractPrepareNextEnumerator;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.basic.detection.TextDetectionResult;
import org.underdocx.common.placeholder.basic.detection.TextDetector;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.SimpleTreeWalker;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.common.tree.nodepath.TextNodePath;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.w3c.dom.Node;

import java.util.List;

public class FullPathExtractor extends AbstractExtractor {

    public FullPathExtractor(TextDetector detector, TextNodeInterpreter interpreter) {
        super(detector, interpreter);
    }

    private class TextSequenceEnumerator extends AbstractPrepareNextEnumerator<TextNodePath> {

        private final Enumerator<Node> nodes;

        private TextSequenceEnumerator(Node startNode, Node scope, Node firstValid) {
            this.nodes = new SimpleTreeWalker(startNode, scope, firstValid);
        }

        public TextSequenceEnumerator(TextSequenceEnumerator other) {
            super(other);
            this.nodes = other.nodes.cloneEnumerator();
        }

        private boolean isPlaceholderTextElement(Node node) {
            if (node.getNodeType() == Node.TEXT_NODE) {
                return interpreter.isTextRelatedType(node.getParentNode());
            } else {
                return interpreter.isTextOnlyType(node);
            }
        }

        @Override
        protected TextNodePath findNext() {
            List<Node> pathNodes = Convenience.buildList(result -> {
                while (nodes.hasNext()) {
                    Node currentNode = nodes.next();
                    if (!isPlaceholderTextElement(currentNode)) {
                        if (!result.isEmpty()) {
                            break;
                        }
                    } else {
                        result.add(currentNode);
                    }
                }
            });
            TextNodePath result = pathNodes.isEmpty() ? null : new TextNodePath(pathNodes, interpreter);
            return result;
        }

        @Override
        public TextSequenceEnumerator cloneEnumerator() {
            return new TextSequenceEnumerator(this);
        }
    }

    private class ExtractNodeEnumerator extends AbstractPrepareNextEnumerator<Node> {

        private final Node tree;
        private final Node firstValidNode;
        private Node start;

        private ExtractNodeEnumerator(Node tree, Node firstValidNodeOrNull) {
            this.tree = tree;
            this.start = tree;
            this.firstValidNode = firstValidNodeOrNull;
        }

        private ExtractNodeEnumerator(ExtractNodeEnumerator other) {
            super(other);
            this.tree = other.tree;
            this.start = other.start;
            this.firstValidNode = other.firstValidNode;
        }

        @Override
        protected Node findNext() {
            return Convenience.build(result -> {
                if (start != null) {
                    TextSequenceEnumerator pathEnumerator = new TextSequenceEnumerator(start, tree, firstValidNode);
                    while (pathEnumerator.hasNext()) {
                        TextNodePath path = pathEnumerator.next();
                        TextDetectionResult.TextArea area = detector.detect(path).area;
                        if (area != null) {
                            Node foundNode = getIfEncapsulated(area).orElse(Encapsulator.encapsulate(area));
                            result.value = foundNode;
                            start = TreeWalker.findNextNode(foundNode, tree, null, true);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public Enumerator<Node> cloneEnumerator() {
            return new ExtractNodeEnumerator(this);
        }
    }


    @Override
    public Enumerator<Node> extractNodes(Node tree, Node firstValidNodeOrNull) {
        return new ExtractNodeEnumerator(tree, firstValidNodeOrNull);
    }
}
