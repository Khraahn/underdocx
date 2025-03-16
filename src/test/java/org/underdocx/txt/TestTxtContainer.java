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
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.internal.TxtParameterizedPlaceholderFactory;
import org.underdocx.doctypes.txt.internal.TxtXml;
import org.underdocx.doctypes.txt.modifiers.TxtDeletePlaceholderModifier;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class TestTxtContainer extends AbstractTxtTest {

    @Test
    public void readConvertAndWriteBack2() throws IOException {
        TxtContainer doc = new TxtContainer("" +
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
        TxtContainer doc = new TxtContainer("" +
                "A${placeholder1}B\n" +
                "C${placeholder2 key:\"value\"}!\n" +
                " ---");
        PlaceholdersProvider<TxtContainer, ParametersPlaceholderData, TxtXml> placeholderProvider
                = new TxtParameterizedPlaceholderFactory().createProvider(doc);
        List<ParametersPlaceholderData> allPlaceholders = Convenience.buildList(result ->
                placeholderProvider.getPlaceholders().forEach(node ->
                        result.add(placeholderProvider.getPlaceholderData(node))));

        Assertions.assertThat(allPlaceholders.size()).isEqualTo(2);
        Assertions.assertThat(allPlaceholders.get(1).getKey()).isEqualTo("placeholder2");
    }

    @Test
    public void testRemovePlaceholderModifier() {
        TxtContainer doc = new TxtContainer("" +
                "A${placeholder1}B\n" +
                "C${placeholder2 key:\"value\"}!\n" +
                " ---");
        PlaceholdersProvider<TxtContainer, ParametersPlaceholderData, TxtXml> placeholderProvider
                = new TxtParameterizedPlaceholderFactory().createProvider(doc);
        List<Node> allPlaceholders = Convenience.buildList(result ->
                placeholderProvider.getPlaceholders().forEach(node ->
                        result.add(node)));

        Assertions.assertThat(allPlaceholders.size()).isEqualTo(2);
        assertParagraphsCount(doc, 3);

        TxtDeletePlaceholderModifier modifier = new TxtDeletePlaceholderModifier();
        modifier.modify(allPlaceholders.get(0), new DeletePlaceholderModifierData.Simple(DeletePlaceholderModifierData.Strategy.DELETE_PARAGRAPH));
        assertParagraphsCount(doc, 2);
        assertNotContains(doc, "A$");
        assertContains(doc, "C$");
    }

    @Test
    public void testRemovePlaceholderModifierBlank() {
        TxtContainer doc = new TxtContainer("" +
                " ${placeholder1} \n" +
                " ---");
        PlaceholdersProvider<TxtContainer, ParametersPlaceholderData, TxtXml> placeholderProvider
                = new TxtParameterizedPlaceholderFactory().createProvider(doc);
        List<Node> allPlaceholders = Convenience.buildList(result ->
                placeholderProvider.getPlaceholders().forEach(node ->
                        result.add(node)));

        Assertions.assertThat(allPlaceholders.size()).isEqualTo(1);
        assertParagraphsCount(doc, 2);

        TxtDeletePlaceholderModifier modifier = new TxtDeletePlaceholderModifier();
        modifier.modify(allPlaceholders.get(0), new DeletePlaceholderModifierData.Simple(DeletePlaceholderModifierData.Strategy.DELETE_BLANK_PARAGRAPH));
        assertParagraphsCount(doc, 1);
        assertNoPlaceholders(doc);
    }

}
