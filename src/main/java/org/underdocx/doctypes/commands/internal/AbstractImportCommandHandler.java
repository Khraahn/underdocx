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

package org.underdocx.doctypes.commands.internal;

import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.modifiermodule.resource.ResourceCommandModule;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

import java.util.HashMap;

/**
 * Implementation of the {{Import uri/data}} command.
 */
public abstract class AbstractImportCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {
    public static final Regex KEYS = new Regex("Import");

    public static final String CACHE_ATTR = "cache";

    private static final PredefinedDataPicker<Boolean> useCacheAttr = new BooleanDataPicker().asPredefined(CACHE_ATTR);

    public AbstractImportCommandHandler(ModifiersProvider modifiers) {
        super(KEYS, modifiers);
    }

    HashMap<String, byte[]> cache = new HashMap<>();

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        Resource resource = new ResourceCommandModule<C, ParametersPlaceholderData, D>(placeholderData.getJson()).execute(selection);
        boolean useCache = useCacheAttr.getData(dataAccess, placeholderData.getJson()).orElse(true);
        C importDoc = getDoc(resource, useCache);
        return doImport(resource.getIdentifier(), importDoc);
    }

    protected abstract CommandHandlerResult doImport(String identifier, C importDoc);

    private C getDoc(final Resource resource, boolean useCache) {
        String resourceId = resource.getIdentifier();
        byte[] data = cache.get(resourceId);
        if (data != null) {
            return Problems.IO_EXCEPTION.exec(() -> createContainer(data));
        } else {
            if (useCache) {
                final byte[] cacheData = Problems.IO_EXCEPTION.exec(resource::getData);
                cache.put(resourceId, cacheData);
                return Problems.IO_EXCEPTION.exec(() -> createContainer(cacheData));
            } else {
                return Problems.IO_EXCEPTION.exec(() -> createContainer(resource));
            }
        }
    }

    protected abstract C createContainer(Resource resource) throws Exception;

    protected abstract C createContainer(byte[] data) throws Exception;


}
