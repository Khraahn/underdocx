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

package org.underdocx.doctypes.odf.commands.exportcommand;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.ImportType;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifierData;
import org.underdocx.doctypes.tools.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Optional;

public abstract class AbstractExportCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractTextualCommandHandler<C, D> {

    public static final String EXPORT_KEY = "Export";
    public static final Regex KEYS = new Regex(EXPORT_KEY);

    public static final String EXPORT_NAME_ATTR = "name";
    public static final String EXPORT_TARGET_NAME_ATTR = "name";
    public static final String EXPORT_TARGET_KEY = "ExportTarget";

    private static final PredefinedDataPicker<String> namePicker = new StringConvertDataPicker().asPredefined(EXPORT_NAME_ATTR);

    protected static final PredefinedAttributesInterpreter<Optional<String>> targetNameInterpreter =
            AttributeInterpreterFactory.createStringAttributeInterpreter(false).asPredefined(EXPORT_TARGET_NAME_ATTR);

    public AbstractExportCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        C targetDoc = getTargetDoc();
        String targetName = namePicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        Pair<Node, Boolean> refNodeAndDeleteFlag = findRefNode(targetDoc, targetName);
        new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        OdfImportModifierData modifiedData = new OdfImportModifierData.Simple(getType(), randomStr(), selection.getDocContainer(), targetDoc, refNodeAndDeleteFlag.left, true, null);
        new OdfImportModifier().modify(modifiedData);
        selection.getDocContainer().setDocument(targetDoc.getDocument());
        cleanUpRefNode(refNodeAndDeleteFlag.left);
        return CommandHandlerResult.EXECUTED_FULL_RESCAN;
    }

    private String randomStr() {
        return "" + (int) (Math.random() * 1000000);
    }

    private C getTargetDoc() {
        Resource resource = new ResourceCommandModule<C, ParametersPlaceholderData, D>(placeholderData.getJson()).execute(selection);
        return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> createDoc(resource));
    }


    protected Pair<Node, Boolean> findRefNode(C targetDoc, String nameOrNull) {
        if (nameOrNull == null) {
            return getUnnamedRefNode(targetDoc);
        } else {
            Enumerator<Node> allPlaceholders = Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> placeholderToolkit.extractPlaceholders(targetDoc.getDocument().getContentRoot(), null));
            for (Node node : allPlaceholders) {
                ParametersPlaceholderData currentPlaceholderData = placeholderToolkit.parsePlaceholder(node);
                if (currentPlaceholderData.getKey().equals(EXPORT_TARGET_KEY)) {
                    Optional<String> optionalName = targetNameInterpreter.interpretAttributes(currentPlaceholderData.getJson());
                    if (optionalName.isPresent() && optionalName.get().equals(nameOrNull)) {
                        return new Pair<>(node, true);
                    }
                }
            }
        }
        return Problems.EXPECTED_PLACEHOLDER_MISSING.toProblem().property(EXPORT_TARGET_KEY).fire();
    }

    protected abstract Pair<Node, Boolean> getUnnamedRefNode(C targetRefNode);


    protected abstract C createDoc(Resource resource) throws IOException;

    protected abstract ImportType getType();

    protected abstract void cleanUpRefNode(Node refNode);
}
