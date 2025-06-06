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

package org.underdocx.doctypes.tools.attrinterpreter.accesstype;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.doctypes.tools.attrinterpreter.AbstractAttributeInterpreter;

/**
 * A {@link org.underdocx.doctypes.tools.attrinterpreter.AttributesInterpreter}
 * that returns a {@link AccessType} according to the prefix of the corresponding json attribute.
 * For example:
 * Given JSON: {a:"a", $b:"b", *c:"c"},
 * interpreteAttribute("a") returns AccessType.ACCESS_ATTR_VALUE,
 * interpreteAttribute("b") returns AccessType.ACCESS_VARIABLE_BY_NAME,
 * interpreteAttribute("c") returns AccessType.ACCESS_MODEL_BY_NAME,
 *
 * @see AccessTypeNameInterpreter
 */
public class AccessTypeJsonNameInterpreter extends AbstractAttributeInterpreter<AccessType, String> {

    public static final AccessTypeJsonNameInterpreter DEFAULT = new AccessTypeJsonNameInterpreter();

    @Override
    protected AccessType interpretAttributes() {
        AccessType accessType = null;
        if (configuration != null && hasAttribute(AccessType.ACCESS_VAR_CONTAINS_NAME_OF_VAR.rename(configuration))) {
            accessType = AccessType.ACCESS_VAR_CONTAINS_NAME_OF_VAR;
        } else if (configuration != null && hasAttribute(AccessType.ACCESS_MODEL_BY_NAME.rename(configuration))) {
            accessType = AccessType.ACCESS_MODEL_BY_NAME;
        } else if (configuration != null && hasAttribute(AccessType.ACCESS_VARIABLE_BY_NAME.rename(configuration))) {
            accessType = AccessType.ACCESS_VARIABLE_BY_NAME;
        } else if (configuration != null && hasAttribute(configuration)) {
            accessType = AccessType.ACCESS_ATTR_VALUE;
        } else if (configuration == null) {
            accessType = AccessType.ACCESS_CURRENT_MODEL_NODE;
        } else {
            accessType = AccessType.MISSING_ACCESS;
        }
        return accessType;
    }

    public static boolean attributeExists(JsonNode attributes, String name) {
        return AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(attributes, name) != AccessType.MISSING_ACCESS;
    }
}
