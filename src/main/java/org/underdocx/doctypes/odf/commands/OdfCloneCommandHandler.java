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
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.ods.OdsContainer;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.Optional;

public class OdfCloneCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractTextualCommandHandler<C, D> {

    private static PredefinedDataPicker<String> nameAttr = new StringConvertDataPicker().asPredefined("name");
    private static PredefinedDataPicker<String> newNameAttr = new StringConvertDataPicker().asPredefined("newName");
    private static PredefinedDataPicker<String> beforeAttr = new StringConvertDataPicker().asPredefined("insertBefore");

    public OdfCloneCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Clone", modifiersProvider);
    }


    private Optional<Node> findElement(String name) {
        if (selection.getDocContainer() instanceof OdsContainer odsContainer) {
            return OdfNodes.findMainTable(odsContainer, name);
        } else {
            Optional<Node> tmp = OdfNodes.findMainPage(selection.getDocContainer(), name);
            return tmp;
        }
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String pageNameToClone = Problems.MISSING_VALUE.get(nameAttr.pickData(dataAccess, placeholderData.getJson()).optional(), "name");
        String newPageName = Problems.MISSING_VALUE.get(newNameAttr.pickData(dataAccess, placeholderData.getJson()).optional(), "newName");
        String insertBeforeName = beforeAttr.pickData(dataAccess, placeholderData.getJson()).optional().orElse(null);
        Node toClone = Problems.CANT_FIND_DOM_ELEMENT.get(findElement(pageNameToClone), "element to clone");
        Node cloned = toClone.cloneNode(true);
        if (selection.getDocContainer() instanceof OdsContainer) {
            OdfAttribute.TABLE_NAME.setAttributeNS(cloned, newPageName);
        } else {
            OdfAttribute.DRAW_NAME.setAttributeNS(cloned, newPageName);
        }
        if (insertBeforeName == null) {
            Node main = Problems.CANT_FIND_DOM_ELEMENT.get(OdfNodes.getMainContentNode(selection.getDocContainer()), "main content node");
            main.appendChild(cloned);
        } else {
            Node insertBeforeNode = Problems.CANT_FIND_DOM_ELEMENT.get(findElement(insertBeforeName), "element to insert before");
            insertBeforeNode.getParentNode().insertBefore(cloned, insertBeforeNode);
        }
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}