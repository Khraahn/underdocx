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

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.odf.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.odf.modifiers.deletenode.OdfDeleteParentModifierData;
import org.underdocx.doctypes.odf.modifiers.deletenode.OdfDeleteParentNodeModifier;
import org.underdocx.doctypes.tools.datapicker.BooleanDataPicker;
import org.underdocx.doctypes.tools.datapicker.OneOfDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.EngineListener;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class RemoveCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String PAGE = "page";
    public static final String TABLE = "table";
    public static final String PARAGRAPH = "paragraph";
    public static PredefinedDataPicker<String> typeAttrPicker = new OneOfDataPicker<String>(TABLE, PAGE, PARAGRAPH).asPredefined("type");
    public static PredefinedDataPicker<Boolean> nowAttrPicker = new BooleanDataPicker().asPredefined("now");

    private Set<Pair<Node, String>> toRemoveLater = new HashSet<>();

    @Override
    public void init(C container, EngineAccess<C, D> engineAccess) {
        engineAccess.addListener(new Listener());
    }

    public RemoveCommandHandler(ModifiersProvider modifiers) {
        super("Remove", modifiers);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        String type = typeAttrPicker.expect(dataAccess, placeholderData.getJson());
        boolean now = nowAttrPicker.expect(dataAccess, placeholderData.getJson());
        if (now) {
            return exec(type, selection.getNode());
        } else {
            toRemoveLater.add(new Pair<>(selection.getNode(), type));
            markForEodDeletion(selection.getPlaceholderData());
            return CommandHandlerResult.EXECUTED_PROCEED;
        }
    }

    private CommandHandlerResult exec(String type, Node node) {
        Predicate<Node> filter = switch (type) {
            case PAGE -> OdfDeleteParentModifierData.ODF_PAGE;
            case TABLE -> OdfDeleteParentModifierData.ODF_TABLE;
            case PARAGRAPH -> OdfDeleteParentModifierData.ODF_PARAGRAPH;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
        return CommandHandlerResult.FACTORY.convert(OdfDeleteParentNodeModifier.modify(node, new OdfDeleteParentModifierData.Simple(filter)));
    }

    private class Listener implements EngineListener<C, D> {

        @Override
        public void eodReached(C doc, EngineAccess<C, D> engineAccess) {
            toRemoveLater.forEach(pair -> {
                exec(pair.right, pair.left);
            });
        }

        @Override
        public void rescan(C doc, EngineAccess<C, D> engineAccess) {
            toRemoveLater.clear();
        }
    }
}
