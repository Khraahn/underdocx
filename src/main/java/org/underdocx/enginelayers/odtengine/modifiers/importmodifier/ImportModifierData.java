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

package org.underdocx.enginelayers.odtengine.modifiers.importmodifier;

import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Node;

import java.util.Optional;

public interface ImportModifierData {

    Node getRefNode();

    AbstractOdfContainer getSourceDoc();

    AbstractOdfContainer getTargetDoc();

    boolean filterInitialPageStyle();

    String getSourceIdentifier();

    Optional<String> getSourcePageName();

    class Simple implements ImportModifierData {

        private final boolean filterInitialPageStyle;
        private final Node targetRefNodeInsertAfter;
        private final AbstractOdfContainer target;
        private final AbstractOdfContainer source;
        private final String sourceResourceName;
        private final String pageNameOrNull;

        public Simple(String sourceResourceName, AbstractOdfContainer source, AbstractOdfContainer target, Node targetRefNodeInsertAfter, boolean filterInitialPageStyle, String pageNameOrNull) {
            this.sourceResourceName = sourceResourceName;
            this.source = source;
            this.target = target;
            this.targetRefNodeInsertAfter = targetRefNodeInsertAfter;
            this.filterInitialPageStyle = filterInitialPageStyle;
            this.pageNameOrNull = pageNameOrNull;
        }

        @Override
        public Node getRefNode() {
            return targetRefNodeInsertAfter;
        }

        @Override
        public AbstractOdfContainer getSourceDoc() {
            return source;
        }

        @Override
        public AbstractOdfContainer getTargetDoc() {
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

        @Override
        public Optional<String> getSourcePageName() {
            return Optional.ofNullable(pageNameOrNull);
        }
    }
}
