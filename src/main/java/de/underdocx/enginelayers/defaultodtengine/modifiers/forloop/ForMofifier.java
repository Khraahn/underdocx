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

package de.underdocx.enginelayers.defaultodtengine.modifiers.forloop;

import de.underdocx.common.doc.DocContainer;
import de.underdocx.common.placeholder.TextualPlaceholderToolkit;
import de.underdocx.enginelayers.defaultodtengine.modifiers.internal.AbstractAreaModifier;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.tree.Nodes;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ForMofifier<C extends DocContainer<D>, D> extends AbstractAreaModifier<C, ParametersPlaceholderData, D, ForModifierData> {

    private List<Node> areaNodes;

    @Override
    protected boolean modify() {
        this.areaNodes = getAreaNodes();
        List<Pair<Node, Node>> areas = createAreas();
        replaceBeginEndNodes(areas);
        return true;
    }

    private void replaceBeginEndNodes(List<Pair<Node, Node>> areas) {
        for (int i = 0; i < areas.size(); i++) {
            Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> border = modifierData.getNodeReplacement(i);
            if (border != null) {
                replaceBorderNode(areas.get(i).left, border.left);
                replaceBorderNode(areas.get(i).right, border.right);
            }
        }
    }

    private void replaceBorderNode(Node node, List<ParametersPlaceholderData> replacements) {
        if (replacements == null || replacements.size() == 0) {
            Nodes.deleteNode(node);
        } else {
            List<Node> nodes = TextualPlaceholderToolkit.clonePlaceholder(node, replacements.size());
            for (int i = 0; i < replacements.size(); i++) {
                int ii = i;
                selection.getPlaceholderToolkit().ifPresent(toolkit ->
                        toolkit.setPlaceholder(nodes.get(ii), replacements.get(ii)));
            }
        }
    }

    private List<Pair<Node, Node>> createAreas() {
        return Convenience.also(new ArrayList<>(), result -> {
            int max = modifierData.getRepeats();
            switch (max) {
                case 0 -> Nodes.deleteNodes(areaNodes);
                default -> {
                    result.add(area);
                    Node last = area.right;
                    for (int i = 1; i < max; i++) {
                        Pair<Node, Node> clonedNodes = cloneArea(last);
                        result.add(clonedNodes);
                        last = clonedNodes.right;
                    }
                }
            }
        });
    }

    private Pair<Node, Node> cloneArea(Node refNode) {
        List<Node> clonedNodes = Nodes.cloneNodes(refNode, areaNodes, false, true);
        return new Pair<>(clonedNodes.get(0), clonedNodes.get(clonedNodes.size() - 1));
    }
}
