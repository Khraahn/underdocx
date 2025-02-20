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

package org.underdocx.enginelayers.defaultodtengine.commands;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BooleanDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.enginelayers.defaultodtengine.modifiers.importmodifier.ImportModifier;
import org.underdocx.enginelayers.defaultodtengine.modifiers.importmodifier.ImportModifierData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

import java.util.HashMap;

/**
 * Implementation of the {{Import uri/data}} command.
 */
public class ImportCommandHander extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
    public static final String BEGIN_KEY = "Import";
    public static final Regex KEYS = new Regex(BEGIN_KEY);

    public static final String CACHE_ATTR = "cache";

    private static final PredefinedDataPicker<Boolean> useCacheAttr = new BooleanDataPicker().asPredefined(CACHE_ATTR);

    public ImportCommandHander() {
        super(KEYS);
    }

    HashMap<String, byte[]> cache = new HashMap<>();

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        Resource resource = new ResourceCommandModule<OdtContainer, ParametersPlaceholderData, OdfTextDocument>(placeholderData.getJson()).execute(selection);
        boolean useCache = useCacheAttr.getData(modelAccess, placeholderData.getJson()).orElse(true);
        OdtContainer importDoc = getDoc(resource, useCache);
        ImportModifierData modifiedData = new ImportModifierData.Simple(resource.getIdentifier(), importDoc, selection.getDocContainer(), selection.getNode(), true);
        new ImportModifier().modify(modifiedData);
        return CommandHandlerResult.FACTORY.convert(DeletePlaceholderModifier.modify(selection.getNode()));
    }

    private OdtContainer getDoc(final Resource resource, boolean useCache) {
        String resourceId = resource.getIdentifier();
        byte[] data = cache.get(resourceId);
        if (data != null) {
            return Problems.IO_EXCEPTION.exec(() -> new OdtContainer(data));
        } else {
            if (useCache) {
                final byte[] cacheData = Problems.IO_EXCEPTION.exec(() -> resource.getData());
                cache.put(resourceId, cacheData);
                return Problems.IO_EXCEPTION.exec(() -> new OdtContainer(cacheData));
            } else {
                return Problems.IO_EXCEPTION.exec(() -> new OdtContainer(resource));
            }
        }
    }
}
