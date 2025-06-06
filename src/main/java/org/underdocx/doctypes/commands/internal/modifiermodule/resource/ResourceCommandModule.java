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

package org.underdocx.doctypes.commands.internal.modifiermodule.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.MimeType;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.modifiermodule.AbstractCommandModule;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.doctypes.tools.datapicker.*;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;
import org.underdocx.environment.err.Problems;

/**
 * This module tries to find out a referred {@link Resource} defined by JSON attribute "data", "uri" or "resource"
 */
public class ResourceCommandModule<C extends DocContainer<D>, P, D> extends AbstractCommandModule<C, P, D, Resource, JsonNode> {

    public static final String URI_ATTR = "uri";
    public static final String DATA_ATTR = "data";
    public static final String RESOURCE_ATTR = "resource";
    public static final String B64_ATTR = "base64";
    public static final String MIME_ATTR = "mimeType";

    private static final PredefinedDataPicker<String> uriPicker = new StringConvertDataPicker().asPredefined(URI_ATTR);
    private static final PredefinedDataPicker<Resource> resourcePicker = new ResourceDataPicker().asPredefined(RESOURCE_ATTR);
    private static final PredefinedDataPicker<byte[]> binaryPicker = new BinaryDataPicker().asPredefined(DATA_ATTR);
    private static final PredefinedDataPicker<String> b64Picker = new StringConvertDataPicker().asPredefined(B64_ATTR);
    private static final AttributeDataPicker<String> mimeTypePicker = new StringConvertDataPicker().optionalAttr(MIME_ATTR);

    public ResourceCommandModule(JsonNode json) {
        super(json);
    }

    @Override
    protected Resource execute() {
        DataAccess dataAccess = selection.getDataAccess().get();
        String mimeType = MimeType.tryConvertExtensionToMimeType(mimeTypePicker.get(dataAccess, configuration));
        if (AccessTypeJsonNameInterpreter.attributeExists(configuration, URI_ATTR)) {
            return Problems.IO_EXCEPTION.exec(() -> new Resource.UriResource(uriPicker.pickData(dataAccess, configuration).getOrThrow(URI_ATTR)));
        } else if (AccessTypeJsonNameInterpreter.attributeExists(configuration, DATA_ATTR)) {
            return Problems.IO_EXCEPTION.exec(() -> new Resource.DataResource(mimeType, binaryPicker.pickData(dataAccess, configuration).getOrThrow(DATA_ATTR)));
        } else if (AccessTypeJsonNameInterpreter.attributeExists(configuration, B64_ATTR)) {
            String b64 = b64Picker.pickData(dataAccess, configuration).getOrThrow(B64_ATTR);
            return Problems.IO_EXCEPTION.exec(() -> new Resource.Base64Resource(mimeType, b64, b64));
        } else {
            return Problems.IO_EXCEPTION.exec(() -> resourcePicker.pickData(dataAccess, configuration).getOrThrow(RESOURCE_ATTR));
        }
    }
}
