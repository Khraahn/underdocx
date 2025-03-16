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

package org.underdocx.doctypes.txt.internal;

import org.underdocx.common.codec.Codec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.basic.extraction.AbstractPartialExtractor;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.enginelayers.parameterengine.internal.ParametersDetector;
import org.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import org.w3c.dom.Node;

public class TxtParameterizedPlaceholderFactory implements GenericTextualPlaceholderFactory<TxtContainer, ParametersPlaceholderData, TxtXml> {

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return TxtNodeInterpreter.INSTANCE;
    }

    @Override
    public Enumerator<? extends Node> createSectionEnumerator(TxtContainer doc) {
        return Convenience.build(Enumerator.empty(), result ->
                Nodes.findFirstDescendantNode(doc.getDocument().getDoc(), "root").ifPresent(root ->
                        result.value = Nodes.getChildren(root)));
    }

    @Override
    public EncapsulatedNodesExtractor getExtractor() {
        return new AbstractPartialExtractor(ParametersDetector.INSTANCE, getTextNodeInterpreter());
    }

    @Override
    public Codec<ParametersPlaceholderData> getCodec() {
        return ParametersPlaceholderCodec.INSTANCE;
    }
}
