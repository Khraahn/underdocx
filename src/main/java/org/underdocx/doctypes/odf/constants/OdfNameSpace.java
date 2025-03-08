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

public enum OdfNameSpace {
    STYLE("style", "urn:oasis:names:tc:opendocument:xmlns:style:1.0"),
    FO("fo", "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"),
    TEXT("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0"),
    DRAW("draw", "urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"),
    SVG("svg", "urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"),
    OFFICE("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0"),
    XLINK("xlink", "http://www.w3.org/1999/xlink"),
    TABLE("table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0"),
    CALCEXT("calcext", "urn:org:documentfoundation:names:experimental:calc:xmlns:calcext:1.0");

    private final String ns;
    private final String uri;

    OdfNameSpace(String ns, String uri) {
        this.ns = ns;
        this.uri = uri;
    }

    public String getNs() {
        return ns;
    }

    public String getUri() {
        return uri;
    }

    public String getQualifiedName(String pureName) {
        return getNs() + ":" + pureName;
    }
}
