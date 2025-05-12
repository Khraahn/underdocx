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

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.TimeCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.tablecell.TableCellModifierData;
import org.underdocx.doctypes.odf.modifiers.tablecell.OdfTableCellModifier;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;

import java.time.LocalDateTime;

public class OdfTimeCommandHandler<C extends DocContainer<D>, D> extends TimeCommandHandler<C, D> {
    private static final PredefinedDataPicker<String> templateCellPicker = new StringConvertDataPicker().asPredefined("templateCell");

    public OdfTimeCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected void handleCell(LocalDateTime time) {
        templateCellPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(templateCell ->
                new OdfTableCellModifier<C, D>().modify(selection, new TableCellModifierData.Simple(time, templateCell)));
    }
}
