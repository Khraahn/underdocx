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

package org.underdocx.doctypes.odf.constants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public enum OdfAttribute {
    SPACE_COUNT(OdfNameSpace.TEXT, "c"),

    FRAME_NAME(OdfNameSpace.DRAW, "name"),
    FRAME_X(OdfNameSpace.SVG, "x"),
    FRAME_Y(OdfNameSpace.SVG, "y"),
    FRAME_WIDTH(OdfNameSpace.SVG, "width"),
    FRAME_HEIGHT(OdfNameSpace.SVG, "height"),

    IMAGE_HREF(OdfNameSpace.XLINK, "href"),

    STYLE_MASTER_PAGE_NAME(OdfNameSpace.STYLE, "master-page-name"),

    PARAGRAPH_PROPERTIES_BREAK_BEFORE(OdfNameSpace.FO, "break-before"),
    PARAGRAPH_PROPERTIES_BREAK_AFTER(OdfNameSpace.FO, "break-after"),
    PARAGRAPH_PROPERTIES_PAGE_NUMBER(OdfNameSpace.STYLE, "page-number"),

    TEXT_PROPERTIES_TEXT_DISPLAY(OdfNameSpace.TEXT, "display"),

    TABLE_NAME(OdfNameSpace.TABLE, "name"),

    TABLE_NUMBER_ROWS_REPEATED(OdfNameSpace.TABLE, "number-rows-repeated"),
    TABLE_NUMBER_COLUMNS_REPEATED(OdfNameSpace.TABLE, "number-columns-repeated"),
    TABLE_DEFAULT_CELL_STYLE_NAME(OdfNameSpace.TABLE, "default-cell-style-name"),
    TABLE_STYLE_NAME(OdfNameSpace.TABLE, "style-name"),

    OFFICE_VALUE(OdfNameSpace.OFFICE, "value"),
    OFFICE_DATE_VALUE(OdfNameSpace.OFFICE, "date-value"),
    OFFICE_TIME_VALUE(OdfNameSpace.OFFICE, "time-value"),
    OFFICE_VALUE_TYPE(OdfNameSpace.OFFICE, "value-type"),
    OFFICE_CURRENCY(OdfNameSpace.OFFICE, "currency"),
    CALCEXT_VALUE_TYPE(OdfNameSpace.CALCEXT, "value-type"),

    TEXT_STYLE_NAME(OdfNameSpace.TEXT, "style-name"),
    ;

    private final String pureName;
    private final OdfNameSpace ns;
    private final String qualifiedName;

    OdfAttribute(OdfNameSpace odfNameSpace, String localName) {
        this.pureName = localName;
        this.ns = odfNameSpace;
        this.qualifiedName = ns.getQualifiedName(pureName);
    }

    public OdfNameSpace getNs() {
        return ns;
    }

    public String getPureName() {
        return pureName;
    }

    public String getAttributeName() {
        return qualifiedName;
    }

    public String getAttributeNS(Node node) {
        Element element = (Element) node;
        if (!element.hasAttributeNS(getNs().getUri(), getPureName())) {
            return null;
        }
        return element.getAttributeNS(getNs().getUri(), getPureName());
    }

    public void setAttributeNS(Node node, String value) {
        Element element = (Element) node;
        if (value != null)
            element.setAttributeNS(getNs().getUri(), qualifiedName, value);
        else
            removeAttributeNS(element);
    }

    public void copy(Node srcNode, Node targetNode) {
        Element src = (Element) srcNode;
        Element target = (Element) targetNode;
        removeAttributeNS(target);
        String value = getAttributeNS(src);
        if (value != null) {
            setAttributeNS(target, value);
        }
    }

    public boolean isIn(Node src) {
        return getAttributeNS(src) != null;
    }

    public void removeAttributeNS(Node node) {
        Element element = (Element) node;
        // element.removeAttributeNS(getNs().getNs(), getPureName());
        element.removeAttribute(qualifiedName);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
