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

package org.underdocx.doctypes.odf.modifiers.deletenode;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.tree.SimpleTreeWalker;
import org.underdocx.doctypes.modifiers.deletenode.DeleteNodeModifier;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.underdocx.doctypes.odf.modifiers.deleteplaceholder.OdfDeletePlaceholderModifier;
import org.underdocx.doctypes.odf.ods.OdsContainer;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.Selection;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Optional;
import java.util.function.Predicate;

public class OdfDeleteNamedNodeModifier<C extends AbstractOdfContainer<D>, P, D extends OdfDocument> implements SelectionModifier<Selection<C, P, D>, OdfDeleteNamedNodeModifierData, ModifierResult> {


    private static Optional<Node> findElement(AbstractOdfContainer<?> doc, String name) {
        if (doc instanceof OdsContainer odsContainer) {
            return OdfNodes.findMainTable(odsContainer, name);
        } else {
            Optional<Node> tmp = OdfNodes.findMainPage(doc, name);
            return tmp;
        }
    }

    private static Predicate<Node> createTableFilter(String name) {
        return node -> OdfElement.TABLE.is(node) && OdfAttribute.TABLE_NAME.hasValue(node, name);
    }

    private static Predicate<Node> createPageFilter(String name) {
        return node -> OdfElement.PAGE.is(node) && OdfAttribute.DRAW_NAME.hasValue(node, name);
    }


    private static Optional<Node> findNode(Document doc, String name, OdfDeleteNamedNodeModifierData.Type type) {
        Predicate<Node> filter = type == OdfDeleteNamedNodeModifierData.Type.PAGE ? createPageFilter(name) : createTableFilter(name);
        SimpleTreeWalker walker = new SimpleTreeWalker(doc, null, null, SimpleTreeWalker.buildFilter(filter), true);
        return walker.inspectNext();
    }

    public static ModifierResult modify(Node node, OdfDeleteNamedNodeModifierData modifierData) {
        Node toRemove = Problems.CANT_FIND_DOM_ELEMENT.get(findNode(node.getOwnerDocument(), modifierData.getName(), modifierData.getNodeType()), "named node");
        DeleteNodeModifier.modify(toRemove);
        return new OdfDeletePlaceholderModifier().modify(node, DeletePlaceholderModifierData.DEFAULT);
    }

    @Override
    public ModifierResult modify(Selection<C, P, D> selection, OdfDeleteNamedNodeModifierData modifierData) {
        return modify(selection.getNode(), modifierData);
    }

}
