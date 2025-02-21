package org.underdocx.mains.addcommand;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import org.underdocx.enginelayers.odtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.DataPickerResult;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.IntegerDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.StringConvertDataPicker;

import java.io.File;
import java.io.IOException;

public class AddExample {

    private static class AddCommandHandler extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
        protected AddCommandHandler() {
            super(new Regex("Add"));
        }

        @Override
        protected CommandHandlerResult tryExecuteTextualCommand() {
            DataPickerResult<Integer> aValue = new IntegerDataPicker().asPredefined("a").pickData(modelAccess, placeholderData.getJson());
            DataPickerResult<Integer> bValue = new IntegerDataPicker().asPredefined("b").pickData(modelAccess, placeholderData.getJson());
            DataPickerResult<String> target = new StringConvertDataPicker().asPredefined("target").pickData(modelAccess, placeholderData.getJson());
            if (!(aValue.isResolvedNotNull() && bValue.isResolvedNotNull() && target.isResolvedNotNull()))
                return CommandHandlerResult.IGNORED;
            int result = aValue.value + bValue.value;
            modelAccess.pushVariable(target.value, new LeafModelNode<>(result));
            DeletePlaceholderModifier.modify(selection.getNode());
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }

    public static void main(String[] args) throws IOException {
        String content = """
                ${Add a:2, b:3, target:"sum"}
                ${$sum}
                """;
        OdtContainer doc = new OdtContainer(content);
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(new AddCommandHandler());
        engine.run();
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile);
        System.out.println("Document created: %s".formatted(tmpFile));
    }
}