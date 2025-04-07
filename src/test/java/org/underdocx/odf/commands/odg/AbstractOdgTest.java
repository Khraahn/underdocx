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

package org.underdocx.odf.commands.odg;

import org.assertj.core.api.Assertions;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.odg.OdgContainer;
import org.w3c.dom.Node;

public class AbstractOdgTest extends AbstractOdtTest {

    protected boolean findTextNode(OdgContainer doc, String text) {
        return !findTextNode(doc.getContentRoot(), text).isEmpty();
    }

    protected void assertTextNodeOrder(OdgContainer doc, String... txts) {
        for (int i = 0; i < txts.length - 1; i++) {
            Node node1 = findTextNode(doc.getContentRoot(), txts[i]).get(0);
            Node node2 = findTextNode(doc.getContentRoot(), txts[i + 1]).get(0);
            Assertions.assertThat(Nodes.compareNodePositions(node1, node2)).isLessThan(0);
        }
    }

    public void assertContains(OdgContainer doc, String text) {
        Assertions.assertThat(findTextNode(doc, text)).isTrue();
    }

    public void assertNotContains(OdgContainer doc, String text) {
        Assertions.assertThat(findTextNode(doc, text)).isFalse();
    }

    public void assertNoPlaceholders(OdgContainer doc) {
        Assertions.assertThat(findTextNode(doc, "$")).isFalse();
    }
}
