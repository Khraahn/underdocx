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

package org.underdocx.doctypes.tools.placeholder;

import org.underdocx.common.codec.Codec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.w3c.dom.Node;

public interface GenericTextualPlaceholderFactory<C extends DocContainer<D>, P, D> extends PlaceholdersProvider.Factory<C, P, D> {

    TextNodeInterpreter getTextNodeInterpreter();

    Enumerator<? extends Node> createSectionEnumerator(C doc);

    EncapsulatedNodesExtractor getExtractor();

    Codec<P> getCodec();

    default TextualPlaceholderToolkit<P> createToolkit() {
        return new TextualPlaceholderToolkit<>(getExtractor(), getCodec());
    }

    default PlaceholdersProvider<C, P, D> createProvider(C doc) {
        return new GenericTextualPlaceholdersProvider<C, P, D>(doc, this);
    }

}
