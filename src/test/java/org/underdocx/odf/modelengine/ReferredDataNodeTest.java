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

package org.underdocx.odf.modelengine;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.ListDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.ReferredDataNode;

public class ReferredDataNodeTest extends AbstractOdtTest {

    @Test
    public void testReferredNode() {
        ListDataNode aList = new ListDataNode();

        MapDataNode root = new MapDataNode();
        root.add("listA", new ReferredDataNode(() -> aList));
        root.add("listB", new ReferredDataNode(() -> aList));

        aList.add(new LeafDataNode<>("Paul"));
        aList.add(new LeafDataNode<>("Sam"));

        Assertions.assertThat(root.getProperty("listA").getProperty(0).getValue()).isEqualTo("Paul");
        Assertions.assertThat(root.getProperty("listA").getProperty(1).getValue()).isEqualTo("Sam");
        Assertions.assertThat(root.getProperty("listB").getProperty(0).getValue()).isEqualTo("Paul");
        Assertions.assertThat(root.getProperty("listB").getProperty(1).getValue()).isEqualTo("Sam");

        Assertions.assertThat(root.getProperty("listA").getProperty(0).getParent().getParent()).isSameAs(root);
        Assertions.assertThat(root.getProperty("listB").getProperty(1).getParent().getParent()).isSameAs(root);
    }
}
