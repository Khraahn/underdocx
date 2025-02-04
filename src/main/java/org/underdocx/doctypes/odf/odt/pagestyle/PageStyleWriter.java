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

package org.underdocx.doctypes.odf.odt.pagestyle;

import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.w3c.dom.Node;

import java.util.Optional;

public class PageStyleWriter {

    public static void writePageStyle(Node node, PageStyle pageStyleToSet, boolean force) {
        Optional<OdfStylableElement> ascendantParagraph = OdfNodes.findOldestParagraphOrTable(node);
        if (ascendantParagraph.isPresent()) {
            OdfStylableElement p = ascendantParagraph.get();
            StyleStyleElement style = p.getOrCreateUnqiueAutomaticStyle();
            PageStyle currentPageStyle = PageStyleReader.readNonRecursive(null, style);
            if (pageStyleToSet.masterPage != null && (force || currentPageStyle.masterPage == null)) {
                OdfAttribute.STYLE_MASTER_PAGE_NAME.setAttributeNS(style, pageStyleToSet.masterPage.value);
            }
            StyleParagraphPropertiesElement paragraphProperties =
                    (StyleParagraphPropertiesElement) style
                            .getOrCreatePropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            if (pageStyleToSet.breakBefore != null && (force || currentPageStyle.breakBefore == null)) {
                OdfAttribute.PARAGRAPH_PROPERTIES_BREAK_BEFORE.setAttributeNS(paragraphProperties, pageStyleToSet.breakBefore.value);
            }
            if (pageStyleToSet.breakAfter != null && (force || currentPageStyle.breakAfter == null)) {
                OdfAttribute.PARAGRAPH_PROPERTIES_BREAK_AFTER.setAttributeNS(paragraphProperties, pageStyleToSet.breakAfter.value);
            }
            if (pageStyleToSet.pageNumber != null && (force || currentPageStyle.pageNumber == null)) {
                OdfAttribute.PARAGRAPH_PROPERTIES_PAGE_NUMBER.setAttributeNS(paragraphProperties, pageStyleToSet.pageNumber.value);
            }
        }
    }

    public static boolean isPageStyleable(Node node) {
        boolean result = node != null && (
                node instanceof TableTableElement ||
                        node instanceof TextParagraphElementBase || OdfNodes.findOldestParagraphOrTable(node).isPresent()
        );
        return result;
    }
}
