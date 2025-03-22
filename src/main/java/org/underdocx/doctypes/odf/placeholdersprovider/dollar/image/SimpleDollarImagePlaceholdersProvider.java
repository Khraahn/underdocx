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

package org.underdocx.doctypes.odf.placeholdersprovider.dollar.image;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.common.types.Tripple;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.placeholdersprovider.dollar.OdfSimpleDollarPlaceholderFactory;
import org.underdocx.doctypes.odf.tools.OdfSectionsWalker;
import org.underdocx.doctypes.tools.placeholder.GenericPlaceholderFromSectionsEnumerator;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDollarImagePlaceholdersProvider<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements EncapsulatedNodesExtractor, PlaceholdersProvider<C, SimpleDollarImagePlaceholderData, D> {
    private final AbstractOdfContainer<?> doc;
    private Node firstValidNode = null;
    private boolean endOfDoc;

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

    private Optional<SvgDescElement> getSvgDesc(DrawFrameElement node) {
        Optional<Node> child = Nodes.findFirstDescendantNode(node, OdfElement.DESC.getQualifiedName());
        return child.map(c -> (SvgDescElement) c);
    }

    private Optional<Tripple<DrawFrameElement, DrawImageElement, SvgDescElement>> getImageElements(Node node) {
        return Optional.ofNullable(Convenience.build(trippleWrapper -> {
            getFrame(node).ifPresent(frame -> {
                getImage(frame).ifPresent(image -> {
                    getSvgDesc(frame).ifPresent(desc -> {
                        trippleWrapper.value = new Tripple<>(frame, image, desc);
                    });
                });
            });
        }));
    }

    private Optional<SimpleDollarImagePlaceholderData> getImageDate(Node node) {
        return Optional.ofNullable(Convenience.build(w ->
                getImageElements(node).ifPresent(elements -> w.value = new SimpleDollarImagePlaceholderDataOdf(doc, elements))
        ));
    }

    // TODO implement enumerator
    private List<Node> extractNodesWorkaround(Node tree, Node firstValidNodeOrNull) {
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
    public Enumerator<Node> extractNodes(Node tree, Node firstValidNodeOrNull) {
        return Enumerator.of(extractNodesWorkaround(tree, firstValidNodeOrNull));
    }

    @Override
    public boolean isEncapsulatedNode(Node node) {
        return Convenience.build(false, w -> {
            getImageDate(node).ifPresent(imageData -> w.value = OdfSimpleDollarPlaceholderFactory.regex.matches(imageData.getDesc()));
        });
    }

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return interpreter;
    }

    @Override
    public Enumerator<Node> getPlaceholders() {
        if (endOfDoc) return Enumerator.empty();
        //return new ElementByElementEnumerator<>(doc, (p, first) -> this.extractNodes(p, first), firstValidNode, true, DrawFrameElement.class);
        OdfSectionsWalker sections = new OdfSectionsWalker(doc, firstValidNode);
        return new GenericPlaceholderFromSectionsEnumerator(sections, this::extractNodes, firstValidNode);
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
    public void restartAt(Node node, boolean endOfDoc) {
        this.firstValidNode = node;
        this.endOfDoc = endOfDoc;
    }

    public static class SimpleDollarImagePlaceholdersProviderFactory<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements PlaceholdersProvider.Factory<C, SimpleDollarImagePlaceholderData, D> {

        @Override
        public PlaceholdersProvider<C, SimpleDollarImagePlaceholderData, D> createProvider(C doc) {
            return new SimpleDollarImagePlaceholdersProvider(doc);
        }
    }
}
