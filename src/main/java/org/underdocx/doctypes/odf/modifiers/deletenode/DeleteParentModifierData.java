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

package org.underdocx.doctypes.odf.modifiers.deletenode;

import org.underdocx.doctypes.odf.constants.OdfElement;
import org.w3c.dom.Node;

import java.util.function.Predicate;

public interface DeleteParentModifierData {

    Predicate<Node> ODF_TABLE = OdfElement.TABLE;
    Predicate<Node> ODF_PARAGRAPH = OdfElement.PARAGRAPH;
    Predicate<Node> ODF_PAGE = OdfElement.PAGE;


    Predicate<Node> getParentFilter();

    class Simple implements DeleteParentModifierData {

        private final Predicate<Node> filter;

        public Simple(Predicate<Node> filter) {
            this.filter = filter;
        }

        @Override
        public Predicate<Node> getParentFilter() {
            return filter;
        }
    }
}
