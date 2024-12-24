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

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractStringCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;
import de.underdocx.tools.common.Regex;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringCommandHandler<C extends DocContainer<D>, D> extends AbstractStringCommandHandler<C, D> {

    PredefinedDataPicker<String> directDataPicker = new StringConvertDataPicker().asPredefined(null);
    PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    Predicate<JsonNode> isDirectAccess = jsonNode ->
            (AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(jsonNode, "value") == AccessType.MISSING_ACCESS);

    public final static Regex KEYS = new Regex(Pattern.quote("String"));

    public StringCommandHandler() {
        super(KEYS);
    }

    @Override
    protected StringOutputModuleConfig getConfig() {
        return () -> (modelAccess, jsonNode) -> isDirectAccess.test(jsonNode)
                ? directDataPicker.pickData(modelAccess, jsonNode)
                : valueDataPicker.pickData(modelAccess, jsonNode);
    }
}
