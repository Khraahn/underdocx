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

package org.underdocx.doctypes.odf.commands.exportcommand;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.ImportType;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

public abstract class AbstractOdgOdpExportCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractExportCommandHandler<C, D> {
    public AbstractOdgOdpExportCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected Pair<Node, Boolean> getUnnamedRefNode(C targetRefNode) {
        Node mainContentNode = Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.getMainContentNode(targetRefNode), "pages container node");
        Node lastPage = Problems.CANT_FIND_DOM_ELEMENT.get(Convenience.last(Nodes.getChildren(mainContentNode, OdfElement.PAGE::is).collect()), "target page");
        return new Pair<>(lastPage, true);
    }

    @Override
    protected ImportType getType() {
        return ImportType.COPY_PAGES;
    }

    @Override
    protected void cleanUpRefNode(Node refNode) {
        if (!(refNode instanceof DrawPageElement)) {
            // Keep page, remove textual placeholder
            new OdfDeletePlaceholderModifier().modify(refNode, DeletePlaceholderModifierData.DEFAULT);
        }
    }
}
