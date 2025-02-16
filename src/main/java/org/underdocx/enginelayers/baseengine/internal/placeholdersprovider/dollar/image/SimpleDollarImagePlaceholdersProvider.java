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

package org.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.tools.ParagraphByParagraphNodesEnumerator;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDollarImagePlaceholdersProvider implements EncapsulatedNodesExtractor, PlaceholdersProvider<OdtContainer, SimpleDollarImagePlaceholderData, OdfTextDocument> {
    private final AbstractOdfContainer<?> doc;
    private Node firstValidNode = null;

    public SimpleDollarImagePlaceholdersProvider(AbstractOdfContainer<?> doc) {
        this.doc = doc;
    }

    private static TextNodeInterpreter interpreter = OdfTextNodeInterpreter.INSTANCE;

    private Optional<DrawFrameElement> getFrame(Node node) {
        return Optional.ofNullable(node instanceof DrawFrameElement ? (DrawFrameElement) node : null);
    }

    private Optional<DrawImageElement> getImage(DrawFrameElement node) {
        Optional<Node> child = Nodes.findFirstDescendantNode(node, OdfElement.IMAGE.getQualifiedName());
        return child.map(c -> (DrawImageElement) c);
    }

    private Optional<Pair<DrawFrameElement, DrawImageElement>> getImageElements(Node node) {
        return Optional.ofNullable(Convenience.build(pairWrapper -> {
            getFrame(node).ifPresent(frame -> {
                getImage(frame).ifPresent(image -> {
                    pairWrapper.value = new Pair<>(frame, image);
                });
            });
        }));
    }

    private Optional<SimpleDollarImagePlaceholderData> getImageDate(Node node) {
        return Optional.ofNullable(Convenience.build(w ->
                getImageElements(node).ifPresent(elements -> w.value = new SimpleDollarImagePlaceholderDataOdf(doc, elements))
        ));
    }

    @Override
    public List<Node> extractNodes(Node tree, Node firstValidNodeOrNull) {
        return Convenience.also(new ArrayList<Node>(), result -> {
            TreeNodeCollector collector = new TreeNodeCollector(tree, tree, firstValidNodeOrNull, new ArrayList<>(),
                    visitState -> visitState != null &&
                            visitState.isValid() &&
                            visitState.isBeginVisit() &&
                            visitState.getNode() != null &&
                            OdfElement.FRAME.is(visitState.getNode()));
            collector.forEach(frame -> {
                if (isEncapsulatedNode(frame)) result.add(frame);
            });
        });
    }

    @Override
    public boolean isEncapsulatedNode(Node node) {
        return Convenience.build(false, w -> {
            getImageDate(node).ifPresent(imageData -> w.value = SimpleDollarPlaceholdersProvider.regex.matches(imageData.getName()));
        });
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return interpreter;
    }

    @Override
    public Enumerator<Node> getPlaceholders() {
        return new ParagraphByParagraphNodesEnumerator(doc, (p, first) -> this.extractNodes(p, first), firstValidNode, true);
    }

    @Override
    public SimpleDollarImagePlaceholderData getPlaceholderData(Node node) {
        return new SimpleDollarImagePlaceholderDataOdf(doc, getImageElements(node).get());
    }

    @Override
    public Optional<TextualPlaceholderToolkit<SimpleDollarImagePlaceholderData>> getPlaceholderToolkit() {
        return Optional.empty();
    }

    @Override
    public void restartAt(Node node) {
        this.firstValidNode = node;
    }

    public static class SimpleDollarImagePlaceholdersProviderFactory implements PlaceholdersProvider.Factory<OdtContainer, SimpleDollarImagePlaceholderData, OdfTextDocument> {

        @Override
        public PlaceholdersProvider<OdtContainer, SimpleDollarImagePlaceholderData, OdfTextDocument> createProvider(OdtContainer doc) {
            return new SimpleDollarImagePlaceholdersProvider(doc);
        }
    }
}
