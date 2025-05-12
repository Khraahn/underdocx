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

package org.underdocx.doctypes.odf.tools.importer.rules;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A path description to navigate through the DOM tree, e.g. office:automatic-styles ->  style:style
 * It also provides method to find all matching nodes or copy them to an other document
 */
public class TagPathDescr {

    public boolean isContent = true;
    public final List<NodeFilter> path;

    public TagPathDescr(boolean isContent, List<NodeFilter> path) {
        this.isContent = isContent;
        this.path = path;
    }

    public TagPathDescr(boolean isContent, NodeFilter... path) {
        this(isContent, Arrays.asList(path));
    }

    public List<Node> findAll(AbstractOdfContainer<?> container) {
        return Convenience.buildList(result -> {
            if (isContent) {
                findAll(result, container.getContentDom(), new ArrayList<>(path));
            } else {
                findAll(result, container.getStylesDom(), new ArrayList<>(path));
            }
        });
    }

    private void findAll(List<Node> resultList, Node currentNode, List<Predicate<Node>> remaining) {
        if (remaining.isEmpty()) {
            resultList.add(currentNode);
        } else {
            Predicate<Node> tagDescr = remaining.remove(0);
            Nodes.children(currentNode).forEach(child -> {
                if (tagDescr.test(child)) {
                    findAll(resultList, child, remaining);
                }
            });
        }
    }

    public TagPathDescr getParent() {
        return new TagPathDescr(this.isContent, Convenience.also(new ArrayList<>(this.path), list -> list.remove(list.size() - 1)));
    }

    public Optional<Node> findFirst(AbstractOdfContainer<?> container) {
        return Convenience.buildOptional(result -> {
            if (isContent) {
                result.value = findFirst(container.getContentDom(), new ArrayList<>(path));
            } else {
                result.value = findFirst(container.getStylesDom(), new ArrayList<>(path));
            }
        });
    }

    private Node findFirst(Node currentNode, List<Predicate<Node>> remaining) {
        if (remaining.isEmpty()) {
            return currentNode;
        } else {
            Predicate<Node> tagDescr = remaining.remove(0);
            for (Node child : Nodes.children(currentNode)) {
                if (tagDescr.test(child)) {
                    return findFirst(child, remaining);
                }
            }
        }
        return null;
    }

    public void copy(AbstractOdfContainer<?> source, AbstractOdfContainer<?> target) {
        List<Node> allToCopy = findAll(source);
        getParent().findFirst(target).ifPresent(targetParent -> {
            Document targetOwnerDocument = targetParent.getOwnerDocument();
            allToCopy.forEach(toCopy -> {
                //UnderdocxEnv.getInstance().logger.trace("copy src : " + new NodePrinter(toCopy));
                Node clone = targetOwnerDocument.importNode(toCopy, true);
                targetParent.appendChild(clone);
                //UnderdocxEnv.getInstance().logger.trace("copy trgt: " + new NodePrinter(clone));
            });
        });
    }


}
