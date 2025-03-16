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

package org.underdocx.doctypes.odf.odt.commands;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifierData;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.tools.placeholder.OdfParameterizedPlaceholderFactory;
import org.underdocx.doctypes.tools.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.doctypes.tools.datapicker.BinaryDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.Optional;

public class ExportCommandHandler extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {

    public static final String EXPORT_KEY = "Export";
    public static final Regex KEYS = new Regex(EXPORT_KEY);

    public static final String EXPORT_NAME_ATTR = "name";
    public static final String EXPORT_URI_ATTR = "uri";
    public static final String EXPORT_DATA_ATTR = "data";
    public static final String EXPORT_TARGET_NAME_ATTR = "name";
    public static final String EXPORT_TARGET_KEY = "ExportTarget";

    private static PredefinedDataPicker<String> namePicker = new StringConvertDataPicker().asPredefined(EXPORT_NAME_ATTR);
    private static PredefinedDataPicker<String> uriPicker = new StringConvertDataPicker().asPredefined(EXPORT_URI_ATTR);
    private static PredefinedDataPicker<byte[]> binaryPicker = new BinaryDataPicker().asPredefined(EXPORT_DATA_ATTR);

    private static PredefinedAttributesInterpreter<Optional<String>> targetNameInterpreter =
            AttributeInterpreterFactory.createStringAttributeInterpreter(false).asPredefined(EXPORT_TARGET_NAME_ATTR);

    public ExportCommandHandler(ModifiersProvider modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        OdtContainer targetDoc = getTargetDoc();
        String targetName = namePicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        Node refNode = findRefNode(targetDoc, targetName);
        new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        OdfImportModifierData modifiedData = new OdfImportModifierData.Simple(randomStr(), selection.getDocContainer(), targetDoc, refNode, true, null);
        new OdfImportModifier().modify(modifiedData);
        selection.getDocContainer().setDocument(targetDoc.getDocument());
        new OdfDeletePlaceholderModifier().modify(refNode, DeletePlaceholderModifierData.DEFAULT);
        return CommandHandlerResult.EXECUTED_FULL_RESCAN;
    }

    private String randomStr() {
        return "" + (int) Math.random() * 1000000;
    }

    private OdtContainer getTargetDoc() {
        Resource resource = new ResourceCommandModule<OdtContainer, ParametersPlaceholderData, OdfTextDocument>(placeholderData.getJson()).execute(selection);
        return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> new OdtContainer(resource));
    }

    private Node findRefNode(OdtContainer targetDoc, String nameOrNull) {
        if (nameOrNull == null) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> targetDoc.getDocument().newParagraph("remove me"));
        } else {
            PlaceholdersProvider<OdtContainer, ParametersPlaceholderData, OdfTextDocument> placeholderProvider = new OdfParameterizedPlaceholderFactory<OdtContainer, OdfTextDocument>().createProvider(targetDoc);

            Enumerator<Node> allPlaceholders = placeholderProvider.getPlaceholders();
            for (Node node : allPlaceholders) {
                ParametersPlaceholderData currentPlaceholderData = placeholderProvider.getPlaceholderData(node);
                if (currentPlaceholderData.getKey().equals(EXPORT_TARGET_KEY)) {
                    Optional<String> optionalName = targetNameInterpreter.interpretAttributes(currentPlaceholderData.getJson());
                    if (optionalName.isPresent() && optionalName.get().equals(nameOrNull)) {
                        return node;
                    }
                }
            }
        }
        return Problems.EXPECTED_PLACEHOLDER_MISSING.toProblem().property(EXPORT_TARGET_KEY).fire();
    }
}
