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
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.common.tree.nodepath.TextNodePath;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.w3c.dom.Node;

public class PartialExtractor extends AbstractExtractor {

    public PartialExtractor(TextDetector detector, TextNodeInterpreter interpreter) {
        super(detector, interpreter);
    }

    private class ExtractNodeEnumerator extends AbstractPrepareNextEnumerator<Node> {

        private final Node tree;
        private final TreeWalker startNodeWalker;

        private ExtractNodeEnumerator(Node tree, Node firstValidNodeOrNull) {
            this.tree = tree;
            this.startNodeWalker = new TreeWalker(tree, tree, firstValidNodeOrNull);
        }

        private ExtractNodeEnumerator(ExtractNodeEnumerator other) {
            super(other);
            this.tree = other.tree;
            this.startNodeWalker = other.startNodeWalker.cloneEnumerator();
        }

        @Override
        protected Node findNext() {
            return Convenience.build(result -> {
                while (result.value == null && startNodeWalker.hasNext()) {
                    Convenience.ifIs(startNodeWalker.next(), state -> state.isBeginVisit() && state.isValid(), state -> {
                        Node startNode = state.getNode();
                        Convenience.ifNotNull(analyzeStartNode(startNode, tree), textArea -> {
                            Node placeholder = getIfEncapsulated(textArea).orElse(Encapsulator.encapsulate(textArea));
                            result.value = placeholder;
                            startNodeWalker.jump(placeholder);
                            startNodeWalker.next();
                            startNodeWalker.nextSkipChildren();
                        });
                    });
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

    private TextDetectionResult.TextArea analyzeStartNode(Node node, Node scope) {
        TreeNodeCollector nodesCollector = new TreeNodeCollector(node, scope, null);
        TextDetectionResult detection = null;
        while ((detection == null || detection.result == TextDetectionResult.TextDetectionResultType.CONTAINS_START) && nodesCollector.hasNext()) {
            nodesCollector.next();
            TextNodePath textNodePath = new TextNodePath(nodesCollector.getNodes(), getTextNodeInterpreter());
            detection = detector.detect(textNodePath);
        }
        return detection == null ? null : detection.area;
    }
}
