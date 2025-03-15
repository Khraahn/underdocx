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

package org.underdocx.doctypes.odf.odt.commands;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifier;
import org.underdocx.doctypes.odf.modifiers.importmodifier.OdfImportModifierData;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.EngineListener;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.Optional;

public class UnderdocxCommandHandler extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {

    private final static Regex KEYS = new Regex("underdocx|Underdocx");
    private final static Resource resource = new Resource.ClassResource(UnderdocxCommandHandler.class, "underdocx.odt");

    public UnderdocxCommandHandler(ModifiersProvider modifiers) {
        super(KEYS, modifiers);
    }

    @Override
    public void init(OdtContainer container, EngineAccess engineAccess) {
        engineAccess.addListener(new Listener());
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        return CommandHandlerResult.IGNORED;
    }


    private class Listener implements EngineListener<OdtContainer, OdfTextDocument> {
        @Override
        public void eodReached(OdtContainer doc, EngineAccess<OdtContainer, OdfTextDocument> engineAccess) {
            engineAccess.lookBack(node -> {
                Optional<ParametersPlaceholderData> placeholderData = AbstractTextualCommandHandler.examineNode(node);
                return placeholderData.filter(parametersPlaceholderData -> KEYS.matches(parametersPlaceholderData.getKey())).isPresent();
            }).forEach(node -> {
                modify(doc, node);
            });
        }
    }

    private void modify(OdtContainer doc, Node node) {
        OdtContainer importDoc = Problems.IO_EXCEPTION.exec(() -> new OdtContainer(resource));
        OdfImportModifierData modifiedData = new OdfImportModifierData.Simple(resource.getIdentifier(), importDoc, doc, node, true, null);
        new OdfImportModifier().modify(modifiedData);
        new OdfDeletePlaceholderModifier().modify(node, DeletePlaceholderModifierData.DEFAULT);
    }
}
