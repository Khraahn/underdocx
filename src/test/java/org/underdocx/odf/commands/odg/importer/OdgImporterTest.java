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

package org.underdocx.odf.commands.odg.importer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.doctypes.odf.odg.OdgContainer;
import org.underdocx.doctypes.odf.odg.OdgEngine;
import org.underdocx.odf.commands.odg.AbstractOdgTest;

import java.io.IOException;

public class OdgImporterTest extends AbstractOdgTest {

    @Test
    public void testImport() throws IOException {
        OdgContainer graphics = new OdgContainer(readResource("ImportTest.odg"));
        OdgEngine engine = new OdgEngine(graphics);
        engine.pushLeafVariable("toImport", readResource("ToImport.odg"));
        engine.pushLeafVariable("toImport2", readResource("ToImport2.odg"));
        engine.run();
        //show(graphics);
        assertTextNodeOrder(graphics, "should be imported", "Text2");
        Assertions.assertThat(findTextNode(graphics, "should be imported")).isTrue();
        Assertions.assertThat(findTextNode(graphics, "$")).isFalse();

    }
}
