package org.underdocx.mains.addcommand;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.OdfModifiersProvider;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.tools.datapicker.IntegerDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;

import java.io.File;
import java.io.IOException;

public class AddExample {

    private static class AddCommandHandler extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
        protected AddCommandHandler() {
            super(new Regex("Add"), new OdfModifiersProvider<>());
        }


        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            int aValue = getAttr(new IntegerDataPicker().expectedAttr("a"));
            int bValue = getAttr(new IntegerDataPicker().expectedAttr("b"));
            String target = getAttr(new StringConvertDataPicker().expectedAttr("target"));
            int result = aValue + bValue;
            dataAccess.pushVariable(target, new LeafDataNode<>(result));
            new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }

    public static File doMain() throws IOException {
        String content = """
                ${Add a:2, b:3, target:"sum"}
                ${$sum}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine();
        engine.registerParametersCommandHandler(new AddCommandHandler());
        engine.run(doc);
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile);
        System.out.println("Document created: %s".formatted(tmpFile));
        return tmpFile;
    }

    public static void main(String[] args) throws IOException {
        doMain();
    }
}