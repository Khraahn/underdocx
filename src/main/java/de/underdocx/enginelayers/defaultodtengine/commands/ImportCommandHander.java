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
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BinaryDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.Uri2BinaryDataPicker;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.common.StringHash;
import de.underdocx.tools.odf.imports.Importer;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.io.InputStream;
import java.net.URI;

/**
 * Implementation of the {{Import uri/data}} command.
 */
public class ImportCommandHander extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
    public static final String BEGIN_KEY = "Import";
    public static final Regex KEYS = new Regex(BEGIN_KEY);

    public static final String URI_ATTR = "uri";
    public static final String DATA_ATTR = "data";

    private static final PredefinedDataPicker<byte[]> binaryDataPicker = new BinaryDataPicker().asPredefined(DATA_ATTR);
    private static final PredefinedDataPicker<byte[]> uriDataPicker = new Uri2BinaryDataPicker().asPredefined(URI_ATTR);
    private static final PredefinedDataPicker<String> uriStringPicker = new StringConvertDataPicker().asPredefined(URI_ATTR);


    public ImportCommandHander() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        return Convenience.build(CommandHandlerResult.EXECUTED_RESCAN_REQUIRED, result -> {
            boolean isUri = AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(placeholderData.getJson(), URI_ATTR) != AccessType.MISSING_ACCESS;
            OdtContainer importDoc;
            String resourceName;
            if (isUri) {
                resourceName = uriStringPicker.pickData(modelAccess, placeholderData.getJson()).getOrThrow("uri");
                InputStream is = Problems.IO_EXCEPTION.exec(() -> new URI(resourceName).toURL().openStream());
                importDoc = Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> new OdtContainer(is));
            } else {
                byte[] binaryData = binaryDataPicker.pickData(modelAccess, placeholderData.getJson()).getOrThrow("data");
                resourceName = StringHash.createStringHash32(binaryData);
                importDoc = Problems.IO_EXCEPTION.exec(() -> new OdtContainer(binaryData));
            }
            new Importer().importDoc(resourceName, importDoc, selection.getDocContainer(), selection.getNode());
            DeletePlaceholderModifier.modify(selection.getNode());
        });
    }
}
