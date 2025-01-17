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

package de.underdocx.enginelayers.defaultodtengine.commands;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BinaryDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.ResourceDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.resource.ResourceCommandModule;
import de.underdocx.enginelayers.defaultodtengine.modifiers.importmodifier.ImportModifier;
import de.underdocx.enginelayers.defaultodtengine.modifiers.importmodifier.ImportModifierData;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.common.Resource;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

/**
 * Implementation of the {{Import uri/data}} command.
 */
public class ImportCommandHander extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
    public static final String BEGIN_KEY = "Import";
    public static final Regex KEYS = new Regex(BEGIN_KEY);

    public static final String URI_ATTR = "uri";
    public static final String DATA_ATTR = "data";
    public static final String RESOURCE_ATTR = "resource";

    private static final PredefinedDataPicker<byte[]> binaryDataPicker = new BinaryDataPicker().asPredefined(DATA_ATTR);
    private static final PredefinedDataPicker<String> uriStringPicker = new StringConvertDataPicker().asPredefined(URI_ATTR);
    private static PredefinedDataPicker<Resource> resourcePicker = new ResourceDataPicker().asPredefined(RESOURCE_ATTR);


    public ImportCommandHander() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED_RESCAN_REQUIRED, result -> {
            Resource resource = new ResourceCommandModule<OdtContainer, ParametersPlaceholderData, OdfTextDocument>(placeholderData.getJson()).execute(selection);
            OdtContainer importDoc = Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> new OdtContainer(resource));
            ImportModifierData modifiedData = new ImportModifierData.Simple(resource.getIdentifier(), importDoc, selection.getDocContainer(), selection.getNode(), true);
            new ImportModifier().modify(modifiedData);
            DeletePlaceholderModifier.modify(selection.getNode());
        });
    }
}
