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

package org.underdocx.enginelayers.baseengine;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.w3c.dom.Node;

import java.util.Optional;

public interface PlaceholdersProvider<C extends DocContainer<D>, P, D> {

    Enumerator<Node> getPlaceholders();

    P getPlaceholderData(Node node);

    Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit();

    /**
     * @param node     next valid node in the tree (all nodes before have to be ignored), null means start at begin
     * @param endOfDoc restart at end of document, probably there are no more placeholders to expect
     */
    void restartAt(Node node, boolean endOfDoc);

    interface Factory<C extends DocContainer<D>, P, D> {
        PlaceholdersProvider<C, P, D> createProvider(C doc);
    }
}
