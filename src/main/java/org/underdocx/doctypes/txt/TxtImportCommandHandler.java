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

package org.underdocx.doctypes.txt;

import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.importcommand.AbstractImportCommandHandler;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;

public class TxtImportCommandHandler extends AbstractImportCommandHandler<TxtContainer, TxtXml> {

    private static final PredefinedDataPicker<String> beginFragment = new StringConvertDataPicker().asPredefined("beginFragmentRegex");
    private static final PredefinedDataPicker<String> endFragment = new StringConvertDataPicker().asPredefined("endFragmentRegex");


    public TxtImportCommandHandler(ModifiersProvider<TxtContainer, TxtXml> modifiers) {
        super(modifiers);
    }

    @Override
    protected CommandHandlerResult doImport(String identifier, TxtContainer importDoc) {
        Regex beginFragmentRegex = beginFragment.pickData(dataAccess, placeholderData.getJson()).optional().map(Regex::new).orElse(null);
        Regex endFragmentRegex = endFragment.pickData(dataAccess, placeholderData.getJson()).optional().map(Regex::new).orElse(null);

        return CommandHandlerResult.FACTORY.convert(new TxtImportModifier(modifiers).modify(selection.getNode(), new TxtImportModifierData.Simple(importDoc, beginFragmentRegex, endFragmentRegex)));
    }


    @Override
    protected TxtContainer createContainer(Resource resource) throws Exception {
        return new TxtContainer(resource);
    }

    @Override
    protected TxtContainer createContainer(byte[] data) throws Exception {
        return new TxtContainer(data);
    }


}
