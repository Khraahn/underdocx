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

package de.underdocx.enginelayers.defaultodtengine.commands.internal;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.ExtendedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.stringoutput.StringOutputCommandModule;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.stringoutput.StringOutputModuleConfig;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.tools.common.Regex;

public abstract class AbstractStringCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public AbstractStringCommandHandler(Regex KEYS) {
        super(KEYS);
    }

    protected CommandHandlerResult tryExecuteTextualCommand() {
        StringOutputCommandModule<C, D> module = new StringOutputCommandModule<>(getConfig());
        return module.execute(selection);
    }

    protected abstract StringOutputModuleConfig getConfig();

    protected static StringOutputModuleConfig buildConfig(String path, ExtendedDataPicker<ModelNode> picker) {
        return () -> new StringConvertDataPicker(picker,
                new StringConvertDataPicker.DefaultModel2StringConverter()).asPredefined(path);
    }

}
