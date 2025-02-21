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

package org.underdocx.commands.string;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.modelengine.model.simple.MapDataNode;

public class MissingDataText extends AbstractOdtTest {

    @Test
    public void testOnNull() {
        String jsonString = """
                {"a":"Test"}
                """;
        String documentStr = "" +
                "A ${*b onNull:\"fallback\", fallback:\"hugo\"} A          \n" + // A hugo A
                "B ${*b onNull:\"empty\"}B                                 \n" + // B B
                "C ${*b onNull:\"deletePlaceholderKeepParagraph\"}C        \n" + // C C
                "D ${*b onNull:\"deletePlaceholderDeleteEmptyParagraph\"}D \n" + // D D
                "E ${*b onNull:\"deletePlaceholderDeleteParagraph\"}E      \n" + // -
                "F ${*b onNull:\"keepPlaceholder\"}F                       \n" + // F ${*b onNull:"keepPlaceholder"}F
                "  ${*b onNull:\"deletePlaceholderDeleteEmptyParagraph\"}  \n" + // -
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapDataNode(jsonString));
        engine.run();
        assertContains(doc, "A hugo A");
        assertContains(doc, "B B");
        assertContains(doc, "C C");
        assertContains(doc, "D D");
        assertNotContains(doc, "E");
        assertContains(doc, "F ${*b onNull:\"keepPlaceholder\"}F");
    }

    @Test
    public void testOnEmpty() {
        String jsonString = """
                {"b":""}
                """;
        String documentStr = "" +
                "A ${*b onEmpty:\"fallback\", fallback:\"hugo\"} A          \n" + // A hugo A
                "B ${*b onEmpty:\"empty\"}B                                 \n" + // B B
                "C ${*b onEmpty:\"deletePlaceholderKeepParagraph\"}C        \n" + // C C
                "D ${*b onEmpty:\"deletePlaceholderDeleteEmptyParagraph\"}D \n" + // D D
                "EE ${*b onEmpty:\"deletePlaceholderDeleteParagraph\"}E     \n" + // -
                "F ${*b onEmpty:\"keepPlaceholder\"}F                       \n" + // F ${*b onNull:"keepPlaceholder"}F
                "  ${*b onEmpty:\"deletePlaceholderDeleteEmptyParagraph\"}  \n" + // -
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapDataNode(jsonString));
        engine.run();
        assertContains(doc, "A hugo A");
        assertContains(doc, "B B");
        assertContains(doc, "C C");
        assertContains(doc, "D D");
        assertNotContains(doc, "EE");
        assertContains(doc, "F ${*b onEmpty:\"keepPlaceholder\"}F");
    }

    @Test
    public void testOnErr() {
        String jsonString = """
                {"b":[]}
                """;
        String documentStr = "" +
                "A ${*b onError:\"fallback\", fallback:\"hugo\"} A          \n" + // A hugo A
                "B ${*b onError:\"empty\"}B                                 \n" + // B B
                "C ${*b onError:\"deletePlaceholderKeepParagraph\"}C        \n" + // C C
                "D ${*b onError:\"deletePlaceholderDeleteEmptyParagraph\"}D \n" + // D D
                "EE ${*b onError:\"deletePlaceholderDeleteParagraph\"}E     \n" + // -
                "F ${*b onError:\"keepPlaceholder\"}F                       \n" + // F ${*b onNull:"keepPlaceholder"}F
                "  ${*b onError:\"deletePlaceholderDeleteEmptyParagraph\"}  \n" + // -
                "";
        OdtContainer doc = new OdtContainer(documentStr);
        OdtEngine engine = new OdtEngine(doc);
        engine.setModel(new MapDataNode(jsonString));
        engine.run();
        assertContains(doc, "A hugo A");
        assertContains(doc, "B B");
        assertContains(doc, "C C");
        assertContains(doc, "D D");
        assertNotContains(doc, "EE");
        assertContains(doc, "F ${*b onError:\"keepPlaceholder\"}F");
    }
}
