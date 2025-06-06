package org.underdocx;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.AbstractOdfTextNodeInterpreter;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.tools.OdfSectionsWalker;
import org.underdocx.environment.UnderdocxEnv;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractOdtTest extends AbstractTest {

    /*

      -- Detection --

     */

    public boolean isIsLibreOfficeInstalled() {
        return UnderdocxEnv.getInstance().libreOfficeExecutable != null;
    }

    protected Optional<TextSelection> search(OdfTextDocument doc, String text) {
        return search(doc, Pattern.compile(Pattern.quote(text)));
    }

    protected Optional<TextSelection> search(OdfTextDocument doc, Pattern pattern) {
        TextNavigation searcher = new TextNavigation(pattern, doc);
        return searcher.hasNext() ? Optional.of(searcher.next()) : Optional.empty();
    }

    protected boolean containsText(OdfTextDocument doc, String text) {
        return search(doc, text).isPresent();
    }

    protected boolean containsText(OdtContainer doc, String text) {
        return containsText(doc.getDocument(), text);
    }

    protected Optional<Integer> getOrder(OdfTextDocument doc, String text1, String text2) {
        return Convenience.buildOptional(result -> {
            if (containsText(doc, text1) && containsText(doc, text2)) {
                Pattern regex = Pattern.compile(Pattern.quote(text1) + "|" + Pattern.quote(text2));
                Optional<TextSelection> searchResult = search(doc, regex);
                searchResult.ifPresent(selection -> result.value = selection.getText().equals(text1) ? -1 : 1);
            }
        });
    }

    protected Optional<Integer> getOrder(OdtContainer doc, String text1, String text2) {
        return getOrder(doc.getDocument(), text1, text2);
    }

    protected TextParagraphElementBase getParagraph(OdtContainer doc, String content) {
        Optional<TextSelection> searchResult = search(doc.getDocument(), content);
        return findOldestParagraph(searchResult.get().getElement()).get();
    }

    public static Optional<TextParagraphElementBase> findOldestParagraph(Node node) {
        return Nodes.findOldestAncestorNode(node, currentNode -> currentNode instanceof TextParagraphElementBase).map(x -> (TextParagraphElementBase) x);
    }




    /*

      -- Assertions

     */


    protected void assertContains(OdtContainer doc, String text) {
        assertThat(containsText(doc, text)).isTrue();
    }

    protected void assertContains(OdfTextDocument doc, String text) {
        assertThat(containsText(doc, text)).isTrue();
    }

    protected void assertNotContains(OdtContainer doc, String text) {
        assertThat(containsText(doc, text)).isFalse();
    }

    protected void assertNotContains(OdfTextDocument doc, String text) {
        assertThat(containsText(doc, text)).isFalse();
    }

    protected void assertFirst(OdtContainer doc, String first, String second) {
        assertFirst(doc.getDocument(), first, second);
    }

    protected void assertFirst(OdfTextDocument doc, String first, String second) {
        assertThat(getOrder(doc, first, second).get()).isLessThan(0);
    }

    protected void assertOrder(OdfTextDocument doc, String... texts) {
        for (int i = 0; i < texts.length - 2; i++) {
            assertFirst(doc, texts[i], texts[i + 1]);
        }
    }

    protected void assertOrder(OdtContainer doc, String... texts) {
        assertOrder(doc.getDocument(), texts);
    }

    protected void assertNoPlaceholders(OdtContainer doc) {
        assertThat(search(doc.getDocument(), Pattern.compile("\\$\\{.*\\}")).isPresent()).isFalse();
        assertThat(search(doc.getDocument(), Pattern.compile("\\$\\.*")).isPresent()).isFalse();
    }

    protected int countParagraphs(OdtContainer doc) {
        int counter = 0;
        OdfSectionsWalker walker = new OdfSectionsWalker(doc, null);
        while (walker.hasNext()) {
            counter++;
            walker.next();
        }
        return counter;
    }

    protected void assertParagraphsCount(OdtContainer doc, int expectation) {
        assertThat(countParagraphs(doc)).isEqualTo(expectation);
    }



    /*

    -- Creation

     */


    protected static final TextNodeInterpreter testTextNodeInterpreter = new AbstractOdfTextNodeInterpreter() {

        @Override
        protected String getNodeName(OdfElement name) {
            return name.getPureName();
        }

        @Override
        protected String getNodeName(OdfAttribute name) {
            return name.getPureName();
        }


        @Override
        public Node createTextContainer(Node parent) {
            return Convenience.also(parent.getOwnerDocument().createElement("span"), parent::appendChild);
        }

        @Override
        public void setNodeText(Node node, String text) {
            Nodes.setNodeText(node, text);
        }

        @Override
        public void appendNodeText(Node node, String text) {
            Nodes.appendNodeText(node, text);
        }
    };

    protected OdtContainer readOdt(String name) {
        try {
            return new OdtContainer(getInputStream(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
