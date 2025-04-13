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

import org.w3c.dom.Node;

import java.util.function.Predicate;

public enum OdfElement implements Predicate<Node> {
    DOCUMENT_CONTENT(OdfNameSpace.OFFICE, "document-content"),
    OFFICE_BODY(OdfNameSpace.OFFICE, "body"),
    OFFICE_SPREADSHEET(OdfNameSpace.OFFICE, "spreadsheet"),
    OFFICE_DRAWING(OdfNameSpace.OFFICE, "drawing"),
    OFFICE_PRESENTATION(OdfNameSpace.OFFICE, "presentation"),
    OFFICE_TEXT(OdfNameSpace.OFFICE, "text"),

    SPAN(OdfNameSpace.TEXT, "span"),
    PARAGRAPH(OdfNameSpace.TEXT, "p"),
    TAB(OdfNameSpace.TEXT, "tab"),
    LINEBREAK(OdfNameSpace.TEXT, "line-break"),
    A(OdfNameSpace.TEXT, "a"),

    SPACE(OdfNameSpace.TEXT, "s"),

    TEXT(OdfNameSpace.OFFICE, "text"),

    FRAME(OdfNameSpace.DRAW, "frame"),

    IMAGE(OdfNameSpace.DRAW, "image"),
    CUSTOM_SHAPE(OdfNameSpace.DRAW, "custom-shape"),

    DESC(OdfNameSpace.SVG, "desc"),

    TABLE(OdfNameSpace.TABLE, "table"),
    TABLE_ROW(OdfNameSpace.TABLE, "table-row"),
    TABLE_COLUMN(OdfNameSpace.TABLE, "table-column"),
    TABLE_CELL(OdfNameSpace.TABLE, "table-cell"),

    LIST(OdfNameSpace.TEXT, "list"),
    LIST_ITEM(OdfNameSpace.TEXT, "list-item"),

    PAGE(OdfNameSpace.DRAW, "page");

    private final String pureName;
    private final OdfNameSpace ns;
    private final String qualifiedName;

    OdfElement(OdfNameSpace odfNameSpace, String name) {
        this.pureName = name;
        this.ns = odfNameSpace;
        this.qualifiedName = ns.getQualifiedName(pureName);
    }

    public OdfNameSpace getNs() {
        return ns;
    }

    public String getPureName() {
        return pureName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public boolean is(Node node) {
        return node.getNodeName().equals(getQualifiedName());
    }

    public Predicate<Node> createFilter() {
        return node -> is(node);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }

    @Override
    public boolean test(Node node) {
        return is(node);
    }
}
