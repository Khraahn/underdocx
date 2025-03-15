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

package org.underdocx.doctypes.txt;

import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Resource;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.importcommand.AbstractImportCommandHandler;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.w3c.dom.Node;

public class TxtImportCommandHandler extends AbstractImportCommandHandler<TxtContainer, TxtXml> {
    public TxtImportCommandHandler(ModifiersProvider modifiers) {
        super(modifiers);
    }

    @Override
    protected CommandHandlerResult doImport(String identifier, TxtContainer importDoc) {
        Wrapper<Node> first = new Wrapper<>();
        Nodes.findAscendantNode(selection.getNode(), "p").ifPresent(p -> {
            Nodes.findFirstDescendantNode(importDoc.getDocument().getDoc(), "root").ifPresent(root -> {
                Nodes.getChildren(root).forEach(nodeToClone -> {
                    Node clone = nodeToClone.cloneNode(true);
                    clone = p.getOwnerDocument().adoptNode(clone);
                    if (first.value == null) {
                        first.value = clone;
                    }
                    p.getParentNode().insertBefore(clone, p);
                });
            });
            modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT);
        });
        if (first.value == null) {
            return CommandHandlerResult.IGNORED;
        } else {
            return CommandHandlerResult.FACTORY.startAtNode(first.value);
        }
    }

    @Override
    protected TxtContainer createContainer(Resource resource) throws Exception {
        return new TxtContainer(resource);
    }

    @Override
    protected TxtContainer createContainer(byte[] data) throws Exception {
        return new TxtContainer(data);
    }

}
