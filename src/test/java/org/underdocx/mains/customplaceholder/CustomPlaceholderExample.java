package org.underdocx.mains.customplaceholder;

import org.underdocx.common.codec.Codec;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.placeholder.basic.extraction.RegexExtractor;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.internal.placeholdersprovider.AbstractTextualPlaceholdersProvider;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.common.types.Regex;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

public class CustomPlaceholderExample {

    private static class MyPlaceholdersProvider extends AbstractTextualPlaceholdersProvider<OdtContainer, String, OdfTextDocument> {

        private static final Regex regex = new Regex("\\<\\w+\\>");
        private static final EncapsulatedNodesExtractor defaultExtractor = new RegexExtractor(regex, OdfTextNodeInterpreter.INSTANCE);
        private static final Codec<String> codec = new Codec<>() {
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

        public MyPlaceholdersProvider(OdtContainer doc) {
            super(doc, new TextualPlaceholderToolkit<>(defaultExtractor, codec));
        }

        public static class MyPlaceholderProviderFactory implements PlaceholdersProvider.Factory<OdtContainer, String, OdfTextDocument> {

            @Override
            public MyPlaceholdersProvider createProvider(OdtContainer doc) {
                return new MyPlaceholdersProvider(doc);
            }
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
            return CommandHandlerResult.EXECUTED;
        }
    }

    public static void main(String[] args) throws IOException {
        String content = "Hello <name>";
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerCommandHandler(new MyPlaceholdersProvider.MyPlaceholderProviderFactory(), new UpperCaseCommandHandler());
        engine.run();
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile); // Expectation: Document containing "Hello NAME"
    }
}