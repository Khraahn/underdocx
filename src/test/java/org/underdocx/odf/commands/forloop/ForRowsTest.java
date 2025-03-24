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

package org.underdocx.odf.commands.forloop;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.data.simple.DataTreeBuilder;
import org.underdocx.enginelayers.modelengine.data.simple.ReferredNode;

import java.io.IOException;

public class ForRowsTest extends AbstractOdtTest {

    @Test
    public void testTableRows() throws IOException {
        OdtContainer doc = new OdtContainer(getInputStream("TableLoop.odt"));
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "1", "Hans", "Müller");
        assertOrder(doc, "2", "Johanna", "Sommer");
        assertOrder(doc, "3", "Helene", "Fischer");
        assertOrder(doc, "1", "2", "3");
    }

    @Test
    public void testTableRowGroups() throws IOException {
        OdtContainer doc = new OdtContainer(getInputStream("TableLoopGroups.odt"));
        OdtEngine engine = new OdtEngine(doc);
        engine.run();
        //show(doc);
        assertNoPlaceholders(doc);
        assertOrder(doc, "1", "Hans", "Müller");
        assertOrder(doc, "2", "Johanna", "Sommer");
        assertOrder(doc, "3", "Helene", "Fischer");
        assertOrder(doc, "1", "2", "3");
    }

    @Test
    public void testStyledCells() {
        OdtContainer doc = readOdt("LoopStyledCells.odt");
        OdtEngine engine = new OdtEngine(doc);
        engine.pushVariable("data",
                DataTreeBuilder.beginList()

                        .beginMap()
                        .add("name", "data1")
                        .add("value", "0.1")
                        .addNode("style", new ReferredNode<>(() -> engine.getVariable("green").get()))
                        .end()

                        .beginMap()
                        .add("name", "data2")
                        .add("value", "1.2")
                        .addNode("style", new ReferredNode<>(() -> engine.getVariable("red").get()))
                        .end()

                        .beginMap()
                        .add("name", "data3")
                        .add("value", "0.8")
                        .addNode("style", new ReferredNode<>(() -> engine.getVariable("yellow").get()))
                        .end()


                        .end().build());
        engine.run();
        //show(doc);

        assertNoPlaceholders(doc);
        assertOrder(doc, "data1", "0.1", "data2", "1.2", "data3", "0.8");
    }
}
