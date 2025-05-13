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

package org.underdocx.odf.commands.odp.pagestyle;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.odp.OdpContainer;
import org.underdocx.doctypes.odf.odp.OdpEngine;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.odf.commands.odp.AbstractOdpTest;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

public class OdpPageStyleTest extends AbstractOdpTest {

    @Test
    public void testOdpPageStyle() throws IOException {
        OdpContainer doc = new OdpContainer(getInputStream("PageStyle.odp"));
        new OdpEngine().run(doc);
        List<Node> pages = Nodes.getChildren(OdfNodes.getMainContentNode(doc).get(), OdfElement.PAGE).collect();
        Assertions.assertThat(((DrawPageElement) pages.get(0)).getDrawMasterPageNameAttribute()).isEqualTo("Green");
        Assertions.assertThat(((DrawPageElement) pages.get(1)).getDrawMasterPageNameAttribute()).isEqualTo("Green");
    }

}
