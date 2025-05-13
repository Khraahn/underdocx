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

package org.underdocx.doctypes.odf.odt.commands;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyleWriter;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.IntegerDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;

import java.util.regex.Pattern;

public class OdtPageStyleCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY = "Page";
    public static final Regex KEYS = new Regex(Pattern.quote(KEY));
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_MASTER_PAGE = "masterPage";
    public static final String ATTR_PB_BEFORE = "pageBreakBefore";
    public static final String ATTR_PB_AFTER = "pageBreakAfter";

    private static final PredefinedDataPicker<String> masterPagePicker = new StringConvertDataPicker().asPredefined(ATTR_MASTER_PAGE);
    private static final PredefinedDataPicker<Integer> pageNumberPicker = new IntegerDataPicker().asPredefined(ATTR_PAGE_NUMBER);
    private static final PredefinedDataPicker<Boolean> pageBreakAfterPicker = new BooleanDataPicker().asPredefined(ATTR_PB_AFTER);
    private static final PredefinedDataPicker<Boolean> pageBreakBeforePicker = new BooleanDataPicker().asPredefined(ATTR_PB_BEFORE);

    public OdtPageStyleCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        PageStyle pageStyle = createPageStyle();
        PageStyleWriter.writePageStyle(selection.getNode(), pageStyle, true);
        new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        return CommandHandlerResult.EXECUTED_PROCEED;
    }

    private PageStyle createPageStyle() {
        return Convenience.build(new PageStyle(), pageStyle -> {
            masterPagePicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(masterPage -> {
                pageStyle.value.masterPage = new Wrapper<>(masterPage);
                pageStyle.value.breakBefore = new Wrapper<>("page");
            });
            pageNumberPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(pageNumber ->
                    pageStyle.value.pageNumber = new Wrapper<>("" + pageNumber));
            pageBreakAfterPicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(pageBreakAfter -> {
                if (pageBreakAfter) {
                    pageStyle.value.breakAfter = new Wrapper<>("page");
                }
            });
            pageBreakBeforePicker.pickData(dataAccess, placeholderData.getJson()).optional().ifPresent(pageBreakBefore -> {
                if (pageBreakBefore) {
                    pageStyle.value.breakBefore = new Wrapper<>("page");
                }
            });
        });
    }
}
