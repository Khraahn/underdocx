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
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.modifiermodule.stringoutput.StringOutputCommandModule;
import org.underdocx.doctypes.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.tools.datapicker.ExtendedDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;

public abstract class AbstractStringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public AbstractStringCommandHandler(Regex KEYS, ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    protected CommandHandlerResult tryExecuteTextualCommand() {
        StringOutputCommandModule<C, D> module = new StringOutputCommandModule<>(getConfig());
        return module.execute(selection).left;
    }

    protected abstract StringOutputModuleConfig<C, D> getConfig();

    protected StringOutputModuleConfig<C, D> buildConfig(String path, ExtendedDataPicker<DataNode> picker) {
        return new StringOutputModuleConfig<>() {
            @Override
            public PredefinedDataPicker<String> getDataPicker() {
                return new StringConvertDataPicker(picker, new StringConvertDataPicker.DefaultModel2StringConverter()).asPredefined(path);
            }

            @Override
            public ModifiersProvider<C, D> getModifiers() {
                return modifiers;
            }
        };
    }

}
