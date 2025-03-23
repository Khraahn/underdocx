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

package org.underdocx.doctypes.odf.commands.image;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.enumerator.AbstractPrepareNextEnumerator;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.tools.OdfSectionsWalker;
import org.underdocx.doctypes.tools.placeholder.GenericPlaceholderFromSectionsEnumerator;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Optional;

public class ImagePlaceholdersProvider<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements EncapsulatedNodesExtractor, PlaceholdersProvider<C, ImagePlaceholderData, D> {
    private final C doc;
    private Node firstValidNode = null;
    private boolean endOfDoc;

    public ImagePlaceholdersProvider(C doc) {
        this.doc = doc;
    }

    private static TextNodeInterpreter interpreter = OdfTextNodeInterpreter.INSTANCE;

    private class ExtractEnumerator extends AbstractPrepareNextEnumerator<Node> {

        private Enumerator<Node> inner;

        private ExtractEnumerator(Node tree, Node firstValidNodeOrNull) {
            super();
            this.inner = new TreeNodeCollector(tree, tree, firstValidNodeOrNull, new ArrayList<>(),
                    visitState -> visitState != null &&
                            visitState.isValid() &&
                            visitState.isBeginVisit() &&
                            visitState.getNode() != null &&
                            OdfElement.FRAME.is(visitState.getNode()));
        }

        private ExtractEnumerator(ExtractEnumerator other) {
            super(other);
            this.inner = other.inner.cloneEnumerator();
        }

        @Override
        protected Node findNext() {
            while (inner.hasNext()) {
                Node frame = inner.next();
                if (isEncapsulatedNode(frame)) {
                    return frame;
                }
            }
            return null;
        }

        @Override
        public Enumerator<Node> cloneEnumerator() {
            return new ExtractEnumerator(this);
        }
    }

    @Override
    public Enumerator<Node> extractNodes(Node tree, Node firstValidNodeOrNull) {
        return new ExtractEnumerator(tree, firstValidNodeOrNull);
    }

    @Override
    public boolean isEncapsulatedNode(Node node) {
        return ImagePlaceholderData.createPlaceholder(node, doc).isPresent();
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
    public ImagePlaceholderData getPlaceholderData(Node node) {
        return ImagePlaceholderData.createPlaceholder(node, doc).get();
    }

    @Override
    public Optional<TextualPlaceholderToolkit<ImagePlaceholderData>> getPlaceholderToolkit() {
        return Optional.empty();
    }

    @Override
    public void restartAt(Node node, boolean endOfDoc) {
        this.firstValidNode = node;
        this.endOfDoc = endOfDoc;
    }

    public static class ImagePlaceholdersProviderFactory<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements PlaceholdersProvider.Factory<C, ImagePlaceholderData, D> {

        @Override
        public PlaceholdersProvider<C, ImagePlaceholderData, D> createProvider(C doc) {
            return new ImagePlaceholdersProvider<>(doc);
        }
    }


}
