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

package org.underdocx.doctypes.tools.parmaplaceholder;

import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Node;

import java.util.Optional;

public class GenericTextualPlaceholdersProvider<C extends DocContainer<D>, P, D> implements PlaceholdersProvider<C, P, D> {

    private final TextualPlaceholderToolkit<P> toolkit;
    private final C doc;
    private final GenericTextualPlaceholderFactory<C, P, D> info;
    private Node startNode = null;
    private boolean endOfDoc = false;

    protected GenericTextualPlaceholdersProvider(C doc, GenericTextualPlaceholderFactory<C, P, D> info) {
        this.info = info;
        this.toolkit = info.createToolkit();
        this.doc = doc;
    }

    @Override
    public Enumerator<Node> getPlaceholders() {
        if (endOfDoc) return Enumerator.empty();
        GenericElementByElementEnumerator<C, P, D> result = new GenericElementByElementEnumerator<>((p, firstValidNode) -> toolkit.extractPlaceholders(p, firstValidNode), startNode, info.createSectionEnumerator(doc));
        return result;
    }


    @Override
    public P getPlaceholderData(Node node) {
        return toolkit.parsePlaceholder(node);
    }

    @Override
    public Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit() {
        return Optional.of(toolkit);
    }

    @Override
    public void restartAt(Node node, boolean endOfDoc) {
        this.startNode = node;
        this.endOfDoc = endOfDoc;
    }

}
