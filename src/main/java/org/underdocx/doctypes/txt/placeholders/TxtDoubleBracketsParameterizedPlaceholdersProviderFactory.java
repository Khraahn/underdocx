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

package org.underdocx.doctypes.txt.placeholders;

import org.underdocx.common.codec.Codec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.basic.extraction.PartialExtractor;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholdersProviderFactory;
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.TxtXml;
import org.underdocx.enginelayers.parameterengine.DoubleBracketsPlaceholderCodec;
import org.underdocx.enginelayers.parameterengine.DoubleBracketsTextDetector;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

public class TxtDoubleBracketsParameterizedPlaceholdersProviderFactory implements GenericTextualPlaceholdersProviderFactory<TxtContainer, ParametersPlaceholderData, TxtXml> {

    @Override
    public TextNodeInterpreter getTextNodeInterpreter() {
        return TxtNodeInterpreter.INSTANCE;
    }

    @Override
    public Enumerator<Node> createSectionEnumerator(TxtContainer doc, Node firstValid) {
        return new TxtSectionEnumerator(doc, firstValid);
    }

    @Override
    public EncapsulatedNodesExtractor getExtractor() {
        return new PartialExtractor(DoubleBracketsTextDetector.INSTANCE, getTextNodeInterpreter());
    }

    @Override
    public Codec<ParametersPlaceholderData> getCodec() {
        return DoubleBracketsPlaceholderCodec.INSTANCE;
    }
}
