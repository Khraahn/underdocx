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

package de.underdocx.tools.odf.pagestyle;

import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Wrapper;
import de.underdocx.tools.odf.OdfTools;
import de.underdocx.tools.odf.constants.OdfAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.w3c.dom.Node;

import java.util.Optional;

public class PageStyleReader {

    public static PageStyle readPageStyle(Node node) {
        return Convenience.also(new PageStyle(), result -> {
            Optional<OdfStylableElement> ascendantParagraph = OdfTools.findOldestParagraphOrTable(node);
            if (ascendantParagraph.isPresent()) {
                OdfStylableElement p = ascendantParagraph.get();
                OdfStyle style = p.getAutomaticStyle();
                scanRecursive(result, style);
            }
        });
    }

    private static PageStyle scanRecursive(PageStyle pageStyle, OdfStyleBase style) {
        if (style != null) {
            pageStyle = readNonRecursive(pageStyle, style);
            scanRecursive(pageStyle, style.getParentStyle());
        }
        return pageStyle;
    }

    public static PageStyle readNonRecursive(PageStyle pageStyle, OdfStyleBase style) {
        pageStyle = pageStyle == null ? new PageStyle() : pageStyle;
        if (style != null) {
            if (style instanceof StyleStyleElement sse) {
                if (sse.getStyleMasterPageNameAttribute() != null && pageStyle.masterPage == null) {
                    pageStyle.masterPage = new Wrapper<>(sse.getStyleMasterPageNameAttribute());
                }
            }
            OdfStylePropertiesBase paragraphProperties = style.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            if (paragraphProperties == null) {
                paragraphProperties = style.getPropertiesElement(OdfStylePropertiesSet.TableProperties);
            }
            if (paragraphProperties != null) {
                String readPageNumber = OdfAttribute.PARAGRAPH_PROPERTIES_PAGE_NUMBER.getAttributeNS(paragraphProperties);
                String readBreakBefore = OdfAttribute.PARAGRAPH_PROPERTIES_BREAK_BEFORE.getAttributeNS(paragraphProperties);
                String readBreakAfter = OdfAttribute.PARAGRAPH_PROPERTIES_BREAK_AFTER.getAttributeNS(paragraphProperties);
                fillPageStyle(pageStyle, readBreakBefore, readBreakAfter, readPageNumber);
            }
        }
        return pageStyle;
    }

    private static void fillPageStyle(PageStyle pageStyle, String readBreakBefore, String readBreakAfter, String readPageNumber) {
        if (readBreakBefore != null && pageStyle.breakBefore == null) {
            pageStyle.breakBefore = new Wrapper<>(readBreakBefore);
        }
        if (readBreakAfter != null && pageStyle.breakAfter == null) {
            pageStyle.breakAfter = new Wrapper<>(readBreakAfter);
        }
        if (readPageNumber != null && pageStyle.pageNumber == null) {
            pageStyle.pageNumber = new Wrapper<>(readPageNumber);
        }
    }

}
