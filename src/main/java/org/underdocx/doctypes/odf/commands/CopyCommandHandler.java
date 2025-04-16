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

package org.underdocx.doctypes.odf.commands;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

public class CopyCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractTextualCommandHandler<C, D> {


    private static final PredefinedDataPicker<String> fromAttr = new StringConvertDataPicker().asPredefined("from");
    private static final PredefinedDataPicker<String> toAttr = new StringConvertDataPicker().asPredefined("to");


    public CopyCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Copy", modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String from = fromAttr.expect(dataAccess, placeholderData.getJson());
        String to = toAttr.expect(dataAccess, placeholderData.getJson());
        Node toCopy = Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.findMainPage(selection.getDocContainer(), from), "element to copy from");
        Node target = Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.findMainPage(selection.getDocContainer(), to), "element to copy into");
        Nodes.getChildren(toCopy).forEach(child -> {
            Node clone = child.cloneNode(true);
            target.appendChild(clone);
        });
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}