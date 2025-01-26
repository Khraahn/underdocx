package de.underdocx.mains.customplaceholder;

import de.underdocx.common.codec.Codec;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.common.placeholder.EncapsulatedNodesExtractor;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.common.placeholder.basic.extraction.RegexExtractor;
import de.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.Selection;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.AbstractTextualPlaceholdersProvider;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.tools.common.Regex;
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
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.registerCommandHandler(new MyPlaceholdersProvider.MyPlaceholderProviderFactory(), new UpperCaseCommandHandler());
        engine.run();
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile); // Expectation: Document containing "Hello NAME"
    }
}