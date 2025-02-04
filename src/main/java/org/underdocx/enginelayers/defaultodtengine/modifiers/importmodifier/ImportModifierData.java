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

package org.underdocx.enginelayers.defaultodtengine.modifiers.importmodifier;

import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.w3c.dom.Node;

public interface ImportModifierData {

    Node getRefNode();

    OdtContainer getSourceDoc();

    OdtContainer getTargetDoc();

    boolean filterInitialPageStyle();

    String getSourceIdentifier();

    class Simple implements ImportModifierData {

        private final boolean filterInitialPageStyle;
        private final Node targetRefNodeInsertAfter;
        private final OdtContainer target;
        private final OdtContainer source;
        private final String sourceResourceName;

        public Simple(String sourceResourceName, OdtContainer source, OdtContainer target, Node targetRefNodeInsertAfter, boolean filterInitialPageStyle) {
            this.sourceResourceName = sourceResourceName;
            this.source = source;
            this.target = target;
            this.targetRefNodeInsertAfter = targetRefNodeInsertAfter;
            this.filterInitialPageStyle = filterInitialPageStyle;
        }

        @Override
        public Node getRefNode() {
            return targetRefNodeInsertAfter;
        }

        @Override
        public OdtContainer getSourceDoc() {
            return source;
        }

        @Override
        public OdtContainer getTargetDoc() {
            return target;
        }

        @Override
        public boolean filterInitialPageStyle() {
            return filterInitialPageStyle;
        }

        @Override
        public String getSourceIdentifier() {
            return sourceResourceName;
        }
    }
}
