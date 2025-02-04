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

package org.underdocx.enginelayers.defaultodtengine.commands;

import org.underdocx.common.doc.DocContainer;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.BooleanDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.IntegerDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.StringConvertDataPicker;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.odf.odt.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.odt.pagestyle.PageStyleWriter;

import java.util.regex.Pattern;

public class PageStyleCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String KEY = "Page";
    public static final Regex KEYS = new Regex(Pattern.quote(KEY));
    public static final String ATTR_PAGE_NUMBER = "pageNumber";
    public static final String ATTR_MASTER_PAGE = "masterPage";
    public static final String ATTR_PB_BEFORE = "pageBreakBefore";
    public static final String ATTR_PB_AFTER = "pageBreakAfter";

    private static PredefinedDataPicker<String> masterPagePicker = new StringConvertDataPicker().asPredefined(ATTR_MASTER_PAGE);
    private static PredefinedDataPicker<Integer> pageNumberPicker = new IntegerDataPicker().asPredefined(ATTR_PAGE_NUMBER);
    private static PredefinedDataPicker<Boolean> pageBreakAfterPicker = new BooleanDataPicker().asPredefined(ATTR_PB_AFTER);
    private static PredefinedDataPicker<Boolean> pageBreakBeforePicker = new BooleanDataPicker().asPredefined(ATTR_PB_BEFORE);

    public PageStyleCommandHandler() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        PageStyle pageStyle = createPageStyle();
        PageStyleWriter.writePageStyle(selection.getNode(), pageStyle, true);
        DeletePlaceholderModifier.modify(selection.getNode());
        return CommandHandlerResult.EXECUTED;
    }

    private PageStyle createPageStyle() {
        return Convenience.build(new PageStyle(), pageStyle -> {
            masterPagePicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().ifPresent(masterPage -> {
                pageStyle.value.masterPage = new Wrapper<>(masterPage);
                pageStyle.value.breakBefore = new Wrapper<>("page");
            });
            pageNumberPicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().ifPresent(pageNumber ->
                    pageStyle.value.pageNumber = new Wrapper<>("" + pageNumber));
            pageBreakAfterPicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().ifPresent(pageBreakAfter -> {
                if (pageBreakAfter) {
                    pageStyle.value.breakAfter = new Wrapper<>("page");
                }
            });
            pageBreakBeforePicker.pickData(modelAccess, placeholderData.getJson()).getOptionalValue().ifPresent(pageBreakBefore -> {
                if (pageBreakBefore) {
                    pageStyle.value.breakBefore = new Wrapper<>("page");
                }
            });
        });
    }
}
