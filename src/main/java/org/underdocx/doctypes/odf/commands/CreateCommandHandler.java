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
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDrawingElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.tools.datapicker.AttributeDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

public class CreateCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractTextualCommandHandler<C, D> {


    private static final AttributeDataPicker<String> nameAttr = new StringConvertDataPicker().expectedAttr("name");
    private static final AttributeDataPicker<String> masterAttr = new StringConvertDataPicker().expectedAttr("master");
    private static final AttributeDataPicker<String> beforeAttr = new StringConvertDataPicker().optionalAttr("insertBefore");

    public CreateCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Create", modifiersProvider);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String newName = getAttr(nameAttr);
        String master = getAttr(masterAttr);
        String insertBefore = getAttr(beforeAttr);
        DrawPageElement myPage = (DrawPageElement) Problems.CANT_FIND_DOM_ELEMENT.get(Nodes.findOldestAncestorNode(selection.getNode(), OdfElement.PAGE), "parent page");
        String styleName = myPage.getStyleName();
        Node parent = myPage.getParentNode();
        DrawPageElement newPage = null;
        if (parent instanceof OfficeDrawingElement officeDrawingElement) {
            newPage = officeDrawingElement.newDrawPageElement(master);
        } else {
            newPage = ((OfficePresentationElement) parent).newDrawPageElement(master);
        }
        newPage.setStyleName(styleName);
        newPage.setDrawNameAttribute(newName);
        if (insertBefore != null) {
            Node insertBeforeNode = Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.findMainPage(selection.getDocContainer(), insertBefore), "element to insert before");
            insertBeforeNode.getParentNode().insertBefore(newPage, insertBeforeNode);
        }
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}
