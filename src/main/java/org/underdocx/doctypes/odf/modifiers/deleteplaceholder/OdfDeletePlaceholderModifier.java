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

package org.underdocx.doctypes.odf.modifiers.deleteplaceholder;

import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.common.placeholder.basic.textnodeinterpreter.OdfTextNodeInterpreter;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.nodepath.TextNodePath;
import org.underdocx.common.tree.nodepath.TreeNodeCollector;
import org.underdocx.common.types.Wrapper;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.odf.tools.OdfNodes;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyle;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyleReader;
import org.underdocx.doctypes.odf.tools.pagestyle.PageStyleWriter;
import org.underdocx.enginelayers.baseengine.ModifierNodeResult;
import org.underdocx.enginelayers.baseengine.ModifierResult;
import org.underdocx.enginelayers.baseengine.SelectionModifier;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class OdfDeletePlaceholderModifier implements SelectionModifier<Node, DeletePlaceholderModifierData, ModifierNodeResult> {


    @Override
    public ModifierNodeResult modify(Node selection, DeletePlaceholderModifierData modifierData) {
        return modifyStatic(selection, modifierData);
    }

    private static ModifierNodeResult modifyStatic(Node placeholderNode, DeletePlaceholderModifierData modifierData) {
        return Convenience.build(ModifierResult.IGNORED, result -> {
            OdfNodes.findAscendantParagraph(placeholderNode, false).ifPresent(p -> {
                result.value = ModifierNodeResult.FACTORY.success(placeholderNode, true);
                TextualPlaceholderToolkit.deletePlaceholder(placeholderNode);
                if (modifierData.getDeleteStrategy() != DeletePlaceholderModifierData.Strategy.KEEP_PARAGRAPH) {
                    if (modifierData.getDeleteStrategy() == DeletePlaceholderModifierData.Strategy.DELETE_PARAGRAPH) {
                        removeParagraph(result, p, modifierData.copyPageStyle());
                    } else {
                        List<Node> paragraphNodes = new TreeNodeCollector(p, p, null, new ArrayList<>()).collect();
                        TextNodePath path = new TextNodePath(paragraphNodes, OdfTextNodeInterpreter.INSTANCE);
                        if (path.isTextRealtedOnly()) {
                            String textContent = path.fetchTextContent();
                            if (textContent.isEmpty() || (textContent.isBlank() && modifierData.getDeleteStrategy() == DeletePlaceholderModifierData.Strategy.DELETE_BLANK_PARAGRAPH)) {
                                removeParagraph(result, p, modifierData.copyPageStyle());
                            }
                        }
                    }
                }
            });
        });
    }

    private static void removeParagraph(Wrapper<ModifierNodeResult> result, Node p, boolean copyStyle) {
        if (copyStyle) {
            PageStyle style = PageStyleReader.readPageStyle(p);
            if (style.breakAfter != null && style.breakAfter.value != null && !style.breakAfter.value.equals("auto")) {
                Node prevP = p.getPreviousSibling();
                while (prevP != null && !PageStyleWriter.isPageStyleable(prevP)) {
                    prevP = prevP.getPreviousSibling();
                }
                if (PageStyleWriter.isPageStyleable(prevP)) {
                    PageStyleWriter.writePageStyle(prevP, style, true);
                }
            } else {
                Node nextP = p.getNextSibling();
                while (nextP != null && !PageStyleWriter.isPageStyleable(nextP)) {
                    nextP = nextP.getNextSibling();
                }
                if (PageStyleWriter.isPageStyleable(nextP)) {
                    PageStyleWriter.writePageStyle(nextP, style, true);
                }
            }
        }

        result.value = ModifierNodeResult.FACTORY.success(p, true);
        Node parent = p.getParentNode();
        parent.removeChild(p);
        if (parent instanceof DrawTextBoxElement && !parent.hasChildNodes()) {
            parent.getParentNode().removeChild(parent);
        }
    }


}
