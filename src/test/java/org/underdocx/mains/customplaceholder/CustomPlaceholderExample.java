package org.underdocx.mains.customplaceholder;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.codec.Codec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.basic.extraction.RegexExtractor;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.TextNodeInterpreter;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.odf.tools.OdfSectionsWalker;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

public class CustomPlaceholderExample {

    private static class MyPlaceholdersProvider implements GenericTextualPlaceholderFactory<OdtContainer, String, OdfTextDocument> {

        private static final Regex regex = new Regex("\\<\\w+\\>");

        @Override
        public TextNodeInterpreter getTextNodeInterpreter() {
            return OdfTextNodeInterpreter.INSTANCE;
        }

        @Override
        public Enumerator<Node> createSectionEnumerator(OdtContainer doc, Node firstValidNode) {
            return new OdfSectionsWalker(doc, firstValidNode);
        }

        @Override
        public EncapsulatedNodesExtractor getExtractor() {
            return new RegexExtractor(regex, getTextNodeInterpreter());
        }

        @Override
        public Codec<String> getCodec() {
            return new Codec<>() {
                @Override
                public String parse(String string) {
                    if (string.startsWith("<") && string.endsWith(">"))
                        return string.substring(1, string.length() - 1);
                    throw new RuntimeException("parse error");
                }

                @Override
                public String getTextContent(String data) {
                    return "<" + data + ">";
                }
            };
        }
    }

    private static class UpperCaseCommandHandler implements CommandHandler<OdtContainer, String, OdfTextDocument> {

        @Override
        public CommandHandlerResult tryExecuteCommand(Selection<OdtContainer, String, OdfTextDocument> selection) {
            selection.getPlaceholderToolkit().ifPresent(toolkit -> {
                Node placeholderNode = selection.getNode();
                String word = toolkit.parsePlaceholder(placeholderNode);
                toolkit.replacePlaceholderWithText(placeholderNode, word.toUpperCase());
            });
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }

    public static void main(String[] args) throws IOException {
        String content = "Hello <name>";
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerCommandHandler(new MyPlaceholdersProvider(), new UpperCaseCommandHandler());
        engine.run();
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile); // Expectation: Document containing "Hello NAME"
    }
}