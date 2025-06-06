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

package org.underdocx.doctypes.odf.commands.importcommand;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.doctypes.commands.internal.AbstractImportCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.ImportType;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifierData;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.w3c.dom.Node;

/**
 * Implementation of the {{Import uri/data}} command.
 */
public abstract class AbstractOdfImportCommandHandler<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractImportCommandHandler<C, D> {


    public AbstractOdfImportCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

    @Override
    protected CommandHandlerResult doImport(String identifier, C importDoc) {
        OdfImportModifierData modifiedData = new OdfImportModifierData.Simple(getImportType(), identifier, importDoc, selection.getDocContainer(), getRefNode(), true, getPageName());
        new OdfImportModifier().modify(modifiedData);
        return CommandHandlerResult.FACTORY.convert(new OdfDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }

    protected abstract ImportType getImportType();

    protected abstract String getPageName();

    protected abstract Node getRefNode();


}
