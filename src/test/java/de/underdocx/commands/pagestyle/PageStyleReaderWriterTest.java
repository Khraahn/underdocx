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

package de.underdocx.commands.pagestyle;

import de.underdocx.AbstractOdtTest;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import de.underdocx.tools.common.Wrapper;
import de.underdocx.tools.odf.pagestyle.PageStyle;
import de.underdocx.tools.odf.pagestyle.PageStyleReader;
import de.underdocx.tools.odf.pagestyle.PageStyleWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PageStyleReaderWriterTest extends AbstractOdtTest {

    @Test
    public void testReader() {
        OdtContainer doc = readOdt("PageStyle.odt");

        testStyledDoc(doc);
    }

    private void testStyledDoc(OdtContainer doc) {
        PageStyle pageStyleA = PageStyleReader.readPageStyle(getParagraph(doc, "A"));
        Assertions.assertThat(pageStyleA.isEmpty()).isTrue();

        PageStyle pageStyleB = PageStyleReader.readPageStyle(getParagraph(doc, "B"));
        Assertions.assertThat(pageStyleB.breakBefore).isEqualTo(new Wrapper<>("column"));
        Assertions.assertThat(pageStyleB.breakAfter).isNull();
        Assertions.assertThat(pageStyleB.masterPage).isNull();
        Assertions.assertThat(pageStyleB.pageNumber).isNull();

        PageStyle pageStyleC = PageStyleReader.readPageStyle(getParagraph(doc, "C"));
        Assertions.assertThat(pageStyleC.breakBefore).isEqualTo(new Wrapper<>("auto"));
        Assertions.assertThat(pageStyleC.breakAfter).isEqualTo(new Wrapper<>("auto"));
        Assertions.assertThat(pageStyleC.masterPage).isEqualTo(new Wrapper<>("Landscape"));
        Assertions.assertThat(pageStyleC.pageNumber).isEqualTo(new Wrapper<>("5"));

        PageStyle pageStyleD = PageStyleReader.readPageStyle(getParagraph(doc, "D"));
        Assertions.assertThat(pageStyleD.breakBefore).isEqualTo(new Wrapper<>("page"));
        Assertions.assertThat(pageStyleD.breakAfter).isNull();
        Assertions.assertThat(pageStyleD.masterPage).isNull();
        Assertions.assertThat(pageStyleD.pageNumber).isNull();
    }

    @Test
    public void testWriter() {
        OdtContainer doc = new OdtContainer("A\nB\nC\nD\n");

        PageStyleWriter.writePageStyle(
                getParagraph(doc, "A"), new PageStyle(
                        new Wrapper<>(null), // master page
                        new Wrapper<>(null), // page number
                        new Wrapper<>(null), // break before
                        new Wrapper<>(null)),// break after
                true);

        PageStyleWriter.writePageStyle(
                getParagraph(doc, "B"), new PageStyle(
                        new Wrapper<>(null), // master page
                        new Wrapper<>(null), // page number
                        new Wrapper<>("column"), // break before
                        new Wrapper<>(null)),// break after
                true);

        PageStyleWriter.writePageStyle(
                getParagraph(doc, "C"), new PageStyle(
                        new Wrapper<>("Landscape"), // master page
                        new Wrapper<>("5"), // page number
                        new Wrapper<>("auto"), // break before
                        new Wrapper<>("auto")),// break after
                true);

        PageStyleWriter.writePageStyle(
                getParagraph(doc, "D"), new PageStyle(
                        new Wrapper<>(null), // master page
                        new Wrapper<>(null), // page number
                        new Wrapper<>("page"), // break before
                        new Wrapper<>(null)),// break after
                true);

        testStyledDoc(doc);
    }

    @Test
    public void testBreaksAfterReplacement() {
        OdtContainer doc = readOdt("PageStyle.odt");
        String json = """
                {
                    "A": "A !",
                    "B": "B !",
                    "C": "C !",
                    "D": "D !",
                }
                """;
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.setModel(new MapModelNode(json));
        engine.run();
        testStyledDoc(doc);
        assertContains(doc, "!");
        assertNoPlaceholders(doc);
    }
}
