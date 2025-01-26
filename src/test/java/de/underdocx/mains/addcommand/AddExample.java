package de.underdocx.mains.addcommand;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.defaultodtengine.DefaultODTEngine;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.DataPickerResult;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.IntegerDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

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
            return CommandHandlerResult.EXECUTED;
        }
    }

    public static void main(String[] args) throws IOException {
        String content = """
                ${Add a:2, b:3, target:"sum"}
                ${$sum}
                """;
        OdtContainer doc = new OdtContainer(content);
        DefaultODTEngine engine = new DefaultODTEngine(doc);
        engine.registerParametersCommandHandler(new AddCommandHandler());
        engine.run();
        File tmpFile = File.createTempFile("Test_", ".odt");
        doc.save(tmpFile);
        System.out.println("Document created: %s".formatted(tmpFile));
    }
}