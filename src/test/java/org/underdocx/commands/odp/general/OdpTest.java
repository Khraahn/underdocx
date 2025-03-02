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

package org.underdocx.commands.odp.general;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.doctypes.odf.odp.OdpContainer;
import org.underdocx.doctypes.odf.odp.OdpEngine;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

public class OdpTest extends AbstractOdtTest {

    protected List<Node> findTextNode(Node tree, String text) {
        return Convenience.buildList(result -> {
            TreeWalker walker = new TreeWalker(tree, tree, null);
            while (walker.hasNext()) {
                TreeWalker.VisitState state = walker.next();
                if (state.isBeginVisit() && state.getNode().getNodeType() == Node.TEXT_NODE && state.getNode().getTextContent().contains(text)) {
                    result.add(state.getNode());
                }
            }
        });
    }

    private boolean findTextNode(OdpContainer doc, String text) {
        return !findTextNode(doc.getContentRoot(), text).isEmpty();
    }

    @Test
    public void testOdp() throws IOException {
        OdpContainer presentation = new OdpContainer(readResource("TestPresentation.odp"));
        OdpEngine engine = new OdpEngine(presentation);
        engine.pushVariable("image", readResource("smile.png"));
        engine.run();
        //show(presentation);
        Assertions.assertThat(findTextNode(presentation, "$")).isFalse();
        Assertions.assertThat(findTextNode(presentation, "A")).isTrue();
        Assertions.assertThat(findTextNode(presentation, "B")).isTrue();
        Assertions.assertThat(findTextNode(presentation, "Hello")).isTrue();

    }
}
