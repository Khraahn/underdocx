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

package org.underdocx.common.placeholder;

import org.underdocx.common.codec.Codec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.underdocx.common.tools.Convenience.also;

public class TextualPlaceholderToolkit<P> {
    private final EncapsulatedNodesExtractor extractor;
    private final Codec<P> codec;

    public TextualPlaceholderToolkit(EncapsulatedNodesExtractor extractor, Codec<P> codec) {
        this.codec = codec;
        this.extractor = extractor;
    }

    public Enumerator<Node> extractPlaceholders(Node tree, Node firstValidNodeOrNull) {
        return extractor.extractNodes(tree, firstValidNodeOrNull);
    }

    public void replacePlaceholderWithText(Node placeholder, String text) {
        extractor.getTextNodeInterpreter().setNodeText(placeholder, text);
    }

    private String getText(Node placeholder) {
        if (placeholder.hasChildNodes()) {
            return placeholder.getFirstChild().getNodeValue();
        } else {
            return null;
        }
    }

    public P parsePlaceholder(String content) {
        return Problems.PLACEHOLDER_PARSE_ERROR.exec(() -> codec.parse(content), null, content);
    }

    public P parsePlaceholder(Node placeholder) {
        return parsePlaceholder(getText(placeholder));
    }

    public Optional<P> tryParsePlaceholder(String content) {
        return codec.tryParse(content);
    }

    public Optional<P> tryParsePlaceholder(Node placeholder) {
        return tryParsePlaceholder(getText(placeholder));
    }

    public void setPlaceholder(Node placeholder, P data) {
        replacePlaceholderWithText(placeholder, codec.getTextContent(data));
    }

    public static void deletePlaceholder(Node placeholder) {
        Node nextSibling = placeholder.getNextSibling();
        Node prevSibling = placeholder.getPreviousSibling();
        Node parent = placeholder.getParentNode();

        if (nextSibling != null && prevSibling != null && Nodes.isText(nextSibling, prevSibling)) {
            prevSibling.setNodeValue(prevSibling.getNodeValue() + nextSibling.getNodeValue());
            parent.removeChild(nextSibling);
        }
        parent.removeChild(placeholder);
    }

    public static Node clonePlaceholder(Node placeholder, boolean insertBefore) {
        return Nodes.cloneNode(placeholder, placeholder, insertBefore, true);
    }

    public static List<Node> clonePlaceholder(Node node, int count) {
        return also(new ArrayList<>(), nodes -> {
            nodes.add(node);
            Node last = node;
            for (int i = 1; i < count; i++) {
                Node clone = clonePlaceholder(last, false);
                nodes.add(clone);
                last = clone;
            }
        });
    }
    
    public TextNodeInterpreter getTextNodeInterpreter() {
        return extractor.getTextNodeInterpreter();
    }

    public Node createPlaceholder(Node parent, P data) {
        Node placeholder = getTextNodeInterpreter().createTextContainer(parent);
        setPlaceholder(placeholder, data);
        return placeholder;
    }

    public Optional<P> examineNode(Node node) {
        return Convenience.buildOptional(w -> {
            if (node.getParentNode() != null && node.getOwnerDocument() != null) {
                tryParsePlaceholder(node).ifPresent(data -> w.value = data);
            }
        });
    }
}
