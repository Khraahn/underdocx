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

package org.underdocx.odf.commands.ods;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.ods.OdsContainer;
import org.underdocx.doctypes.odf.ods.OdsEngine;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.List;

public class OdsTest extends AbstractOdtTest {

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

    protected TableTableCellElement findCellWithText(OdsContainer doc, String text) {
        List<Node> nodesWithText = findTextNode(doc.getContentRoot(), text);
        return (TableTableCellElement) Nodes.findAscendantNode(nodesWithText.get(0), OdfElement.TABLE_CELL::is).get();
    }

    private boolean findTextNode(OdsContainer doc, String text) {
        return !findTextNode(doc.getContentRoot(), text).isEmpty();
    }


    @Test
    public void testOds() throws IOException {
        OdsContainer tables = new OdsContainer(readResource("TestTables.ods"));
        OdsEngine engine = new OdsEngine();
        engine.run(tables);
        //show(tables);
        Assertions.assertThat(findTextNode(tables, "$")).isFalse();
        Assertions.assertThat(findTextNode(tables, "Gerda")).isTrue();
    }

    @Test
    public void testOdsColmnStyles() throws IOException {
        OdsContainer tables = new OdsContainer(readResource("TestTableColumnTypes.ods"));
        OdsEngine engine = new OdsEngine();
        engine.run(tables);
        //show(tables);
        Assertions.assertThat(findTextNode(tables, "$")).isFalse();
        Assertions.assertThat(findTextNode(tables, "Hans")).isTrue();

        TableTableCellElement dateCell = findCellWithText(tables, "2023-05-06");
        Assertions.assertThat(dateCell.getOfficeDateValueAttribute()).isEqualTo("2023-05-06");
        Assertions.assertThat(dateCell.getOfficeValueTypeAttribute()).isEqualTo("date");
        Assertions.assertThat(dateCell.getStyleName()).isEqualTo("ce10");

        dateCell = findCellWithText(tables, "80 %");
        Assertions.assertThat(dateCell.getOfficeValueAttribute()).isEqualTo(0.8);
        Assertions.assertThat(dateCell.getOfficeValueTypeAttribute()).isEqualTo("percentage");
        Assertions.assertThat(dateCell.getStyleName()).isEqualTo("ce6");
    }
}
