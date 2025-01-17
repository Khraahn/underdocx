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

package de.underdocx.tools.odf.imports;

import de.underdocx.tools.odf.imports.rules.*;

import java.util.Arrays;

/**
 * Default Rules which nodes have to be copied and which attribute values have to be renamed
 */
public class ImportRules extends AbstractImportRules {
    private static final TagDescr DOCUMENT_STYLES = t("office:document-styles");
    private static final TagDescr DOCUMENT_CONTENT = t("office:document-content");
    private static final TagDescr OFFICE_BODY = t("office:body");
    private static final TagDescr OFFICE_STYLES = t("office:styles");
    private static final TagDescr STYLE_STYLE = t("style:style");
    private static final TagDescr OFFICE_AUTOMATIC_STYLES = t("office:automatic-styles");
    private static final TagDescr OFFICE_TEXT = t("office:text");

    public static ImportRules DEFAULT = ImportRules.createRules(NodeFilter.ACCEPT_ALL);

    private NodeFilter copyContentNodeFilter;

    private ImportRules() {
    }

    protected void createRules() {
        providerDescrs.addAll(Arrays.asList(
                new ProviderDescr(
                        new TagPathDescr(true,
                                DOCUMENT_CONTENT, OFFICE_AUTOMATIC_STYLES, STYLE_STYLE
                        ), new AttrDescr("style:name")),
                new ProviderDescr(
                        new TagPathDescr(false,
                                DOCUMENT_STYLES, OFFICE_STYLES, STYLE_STYLE
                        ), new AttrDescr("style:name"))));

        consumerDescrs.add(new ConsumerDescr(new AttrDescr("style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("parent-style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("text-style-name")));
        consumerDescrs.add(new ConsumerDescr(new AttrDescr("marker-style-name")));

        copyRules.add(new TagPathDescr(false, DOCUMENT_STYLES, OFFICE_STYLES, STYLE_STYLE));
        copyRules.add(new TagPathDescr(false, DOCUMENT_STYLES, OFFICE_AUTOMATIC_STYLES, NodeFilter.ACCEPT_ALL));
        copyRules.add(new TagPathDescr(true, DOCUMENT_CONTENT, OFFICE_AUTOMATIC_STYLES, NodeFilter.ACCEPT_ALL));

        mainCopyRule = new TagPathDescr(true, DOCUMENT_CONTENT, OFFICE_BODY, OFFICE_TEXT, copyContentNodeFilter);
    }

    public static ImportRules createRules(NodeFilter copyContentNodeFilter) {
        ImportRules result = new ImportRules();
        result.copyContentNodeFilter = copyContentNodeFilter;
        result.init();
        return result;
    }


}
