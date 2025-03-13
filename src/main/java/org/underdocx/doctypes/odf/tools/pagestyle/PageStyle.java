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

package org.underdocx.doctypes.odf.tools.pagestyle;

import org.underdocx.common.types.Wrapper;

public class PageStyle {

    public Wrapper<String> pageNumber; // pageNumber==null: no information, pageNumber.value==null: missing attribute

    public Wrapper<String> masterPage; // masterPage==null: no information, masterPage.value==null: missing attribute

    public Wrapper<String> breakBefore; // breakBefore==null: no information, breakBefore.value==null: missing attribute

    public Wrapper<String> breakAfter; // breakAfter==null: no information, breakAfter.value==null: missing attribute

    public PageStyle(Wrapper<String> masterPage, Wrapper<String> pageNumber, Wrapper<String> breakBefore, Wrapper<String> breakAfter) {
        this.pageNumber = pageNumber;
        this.masterPage = masterPage;
        this.breakBefore = breakBefore;
        this.breakAfter = breakAfter;
    }

    public PageStyle() {
        this.pageNumber = null;
        this.masterPage = null;
        this.breakBefore = null;
        this.breakAfter = null;
    }

    public boolean isEmpty() {
        return pageNumber == null && masterPage == null && breakBefore == null && breakAfter == null;
    }
}
