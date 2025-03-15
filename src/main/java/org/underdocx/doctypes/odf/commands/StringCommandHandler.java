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

package org.underdocx.doctypes.odf.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.internal.AbstractStringCommandHandler;
import org.underdocx.doctypes.odf.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessType;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.doctypes.tools.datapicker.DataPickerResult;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class StringCommandHandler<C extends DocContainer<D>, D> extends AbstractStringCommandHandler<C, D> {

    PredefinedDataPicker<String> directDataPicker = new StringConvertDataPicker().asPredefined(null);
    PredefinedDataPicker<String> valueDataPicker = new StringConvertDataPicker().asPredefined("value");
    Predicate<JsonNode> isDirectAccess = jsonNode ->
            (AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(jsonNode, "value") == AccessType.MISSING_ACCESS);

    public final static Regex KEYS = new Regex(Pattern.quote("String"));

    public StringCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected StringOutputModuleConfig getConfig() {
        return new StringOutputModuleConfig() {
            @Override
            public PredefinedDataPicker<String> getDataPicker() {
                return new PredefinedDataPicker<>() {
                    @Override
                    public DataPickerResult<String> pickData(DataAccess dataAccess, JsonNode jsonNode) {
                        return isDirectAccess.test(jsonNode)
                                ? directDataPicker.pickData(dataAccess, jsonNode)
                                : valueDataPicker.pickData(dataAccess, jsonNode);
                    }

                    @Override
                    public String getName() {
                        return "value";
                    }
                };
            }

            @Override
            public ModifiersProvider getModifiers() {
                return modifiers;
            }
        };
    }
}
