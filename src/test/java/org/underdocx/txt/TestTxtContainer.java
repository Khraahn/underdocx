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

package org.underdocx.txt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.common.codec.XMLCodec;
import org.underdocx.common.tools.Convenience;
import org.underdocx.doctypes.txt.TextContainer;
import org.underdocx.doctypes.txt.TextXML;
import org.underdocx.doctypes.txt.TxtParameterizedPlaceholderFactory;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TestTxtContainer {

    @Test
    public void readConvertAndWriteBack2() throws IOException {
        TextContainer doc = new TextContainer("" +
                "A B CDE<>   F\n" +
                "G   HIJ!  \n" +
                " ---");
        String xml = XMLCodec.DEFAULT.getTextContent(doc.getDocument().getDoc());
        System.out.println(xml);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        doc.save(bos);
        String string = bos.toString();

        String[] split = string.split("\n");
        Assertions.assertThat(split[0]).isEqualTo("A B CDE<>   F");
        Assertions.assertThat(split[1]).isEqualTo("G   HIJ!  ");
        Assertions.assertThat(split[2]).isEqualTo(" ---");
        Assertions.assertThat(split.length).isEqualTo(3);
    }

    @Test
    public void testPlaceholderProvider() {
        TextContainer doc = new TextContainer("" +
                "A${placeholder1}B\n" +
                "C${placeholder2 key:\"value\"}!\n" +
                " ---");
        PlaceholdersProvider<TextContainer, ParametersPlaceholderData, TextXML> placeholderProvider
                = new TxtParameterizedPlaceholderFactory().createProvider(doc);
        List<ParametersPlaceholderData> allPlaceholders = Convenience.buildList(result ->
                placeholderProvider.getPlaceholders().forEach(node ->
                        result.add(placeholderProvider.getPlaceholderData(node))));

        Assertions.assertThat(allPlaceholders.size()).isEqualTo(2);
        Assertions.assertThat(allPlaceholders.get(1).getKey()).isEqualTo("placeholder2");
    }
}
