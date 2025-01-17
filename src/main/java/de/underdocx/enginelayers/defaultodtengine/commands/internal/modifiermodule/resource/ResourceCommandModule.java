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

package de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.resource;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BinaryDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.ResourceDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.AbstractCommandModule;
import de.underdocx.enginelayers.modelengine.modelaccess.ModelAccess;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Resource;

/**
 * This module tries to find out a referred {@link Resource} defined by JSON attribute "data", "uri" or "resource"
 */
public class ResourceCommandModule<C extends DocContainer<D>, P, D> extends AbstractCommandModule<C, P, D, Resource, JsonNode> {

    public static final String URI_ATTR = "uri";
    public static final String DATA_ATTR = "data";
    public static final String RESOURCE_ATTR = "resource";

    private static PredefinedDataPicker<String> uriPicker = new StringConvertDataPicker().asPredefined(URI_ATTR);
    private static PredefinedDataPicker<Resource> resourcePicker = new ResourceDataPicker().asPredefined(RESOURCE_ATTR);
    private static PredefinedDataPicker<byte[]> binaryPicker = new BinaryDataPicker().asPredefined(DATA_ATTR);

    public ResourceCommandModule(JsonNode json) {
        super(json);
    }

    @Override
    protected Resource execute() {
        ModelAccess modelAccess = selection.getModelAccess().get();
        if (AccessTypeJsonNameInterpreter.attributeExists(configuration, URI_ATTR)) {
            return Problems.IO_EXCEPTION.exec(() -> new Resource.UriResource(uriPicker.pickData(modelAccess, configuration).getOrThrow(URI_ATTR)));
        } else if (AccessTypeJsonNameInterpreter.attributeExists(configuration, DATA_ATTR)) {
            return Problems.IO_EXCEPTION.exec(() -> new Resource.DataResource(binaryPicker.pickData(modelAccess, configuration).getOrThrow(DATA_ATTR)));
        } else {
            return Problems.IO_EXCEPTION.exec(() -> resourcePicker.pickData(modelAccess, configuration).getOrThrow(RESOURCE_ATTR));
        }
    }
}
