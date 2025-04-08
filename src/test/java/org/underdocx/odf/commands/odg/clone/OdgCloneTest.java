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

package org.underdocx.odf.commands.odg.clone;

import org.junit.jupiter.api.Test;
import org.underdocx.doctypes.odf.odg.OdgContainer;
import org.underdocx.doctypes.odf.odg.OdgEngine;
import org.underdocx.odf.commands.odg.AbstractOdgTest;

import java.io.IOException;

public class OdgCloneTest extends AbstractOdgTest {

    @Test
    public void testClonePage() throws IOException {
        OdgContainer doc = new OdgContainer(readResource("ClonePage.odg"));
        OdgEngine engine = new OdgEngine();
        engine.run(doc);
        //show(doc);
        assertTextNodeOrder(doc, "2", "10", "42", "50", "178", "180");
    }
}
