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

package org.underdocx.odf.commands.pagestyle;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyleReader;

public class PageStyleCommandTest extends AbstractOdtTest {
    @Test
    public void testPageCommand() {
        OdtContainer doc = readOdt("PageStyleCommand.odt");
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        PageStyle pageStyleA = PageStyleReader.readPageStyle(getParagraph(doc, "A"));
        Assertions.assertThat(pageStyleA.isEmpty()).isTrue();

        PageStyle pageStyleB = PageStyleReader.readPageStyle(getParagraph(doc, "B"));
        Assertions.assertThat(pageStyleB.breakBefore).isEqualTo(new Wrapper<>("page"));
        Assertions.assertThat(pageStyleB.breakAfter).isNull();
        Assertions.assertThat(pageStyleB.masterPage).isEqualTo(new Wrapper<>("Landscape"));
        Assertions.assertThat(pageStyleB.pageNumber).isEqualTo(new Wrapper<>("5"));

        PageStyle pageStyleC = PageStyleReader.readPageStyle(getParagraph(doc, "C"));
        Assertions.assertThat(pageStyleC.breakBefore).isEqualTo(new Wrapper<>("page"));
        Assertions.assertThat(pageStyleC.breakAfter).isNull();
        Assertions.assertThat(pageStyleC.masterPage).isNull();
        Assertions.assertThat(pageStyleC.pageNumber).isNull();

        PageStyle pageStyleD = PageStyleReader.readPageStyle(getParagraph(doc, "D"));
        Assertions.assertThat(pageStyleD.breakBefore).isEqualTo(new Wrapper<>("page"));
        Assertions.assertThat(pageStyleD.breakAfter).isNull();
        Assertions.assertThat(pageStyleD.masterPage).isEqualTo(new Wrapper<>("Default Page Style"));
        Assertions.assertThat(pageStyleD.pageNumber).isNull();
    }
}
