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

package org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype;

import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AbstractAttributeInterpreter;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.AttributesInterpreter;

/**
 * A {@link AttributesInterpreter}
 * that returns a {@link AccessType} according to the prefix of the provided name, independent from json.
 * For example:
 * interpreteAttribute("a") returns AccessType.ACCESS_ATTR_VALUE,
 * interpreteAttribute("$a") returns AccessType.ACCESS_VARIABLE_BY_NAME,
 * interpreteAttribute("@a") returns AccessType.ACCESS_MODEL_BY_NAME,
 *
 * @see AccessTypeJsonNameInterpreter
 */
public class AccessTypeNameInterpreter extends AbstractAttributeInterpreter<AccessType, String> {

    public static AccessTypeNameInterpreter DEFAULT = new AccessTypeNameInterpreter();

    @Override
    protected AccessType interpretAttributes() {
        AccessType accessType = null;
        if (configuration != null && configuration.equals(AccessType.ACCESS_MODEL_BY_NAME.rename(configuration))) {
            accessType = AccessType.ACCESS_MODEL_BY_NAME;
        } else if (configuration != null && configuration.equals(AccessType.ACCESS_VARIABLE_BY_NAME.rename(configuration))) {
            accessType = AccessType.ACCESS_VARIABLE_BY_NAME;
        } else if (configuration != null && configuration.equals(configuration)) {
            accessType = AccessType.ACCESS_ATTR_VALUE;
        } else if (configuration == null) {
            accessType = AccessType.ACCESS_CURRENT_MODEL_NODE;
        } else {
            accessType = AccessType.MISSING_ACCESS;
        }
        return accessType;
    }
}
