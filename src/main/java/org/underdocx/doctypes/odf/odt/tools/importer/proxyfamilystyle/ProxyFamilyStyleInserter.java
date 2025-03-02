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

package org.underdocx.doctypes.odf.odt.tools.importer.proxyfamilystyle;

import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.AbstractOdfContainer;
import org.w3c.dom.Node;

import java.util.Collection;
import java.util.HashSet;

public class ProxyFamilyStyleInserter {

    private AbstractOdfContainer<?> doc;
    private HashSet<Node> proxyNodes;

    public ProxyFamilyStyleInserter(AbstractOdfContainer<?> doc) {
        this.doc = doc;
    }

    public void insertProxyStyleNodes() {
        proxyNodes = new HashSet<>();
        createProxyNodes();
        changeParentStyles();
    }

    private void updateStyleNodes(Collection<Node> nodes) {
        nodes.forEach(node -> {
            if (node instanceof StyleStyleElement style && !proxyNodes.contains(node)) {
                if (style.getStyleParentStyleNameAttribute() == null || style.getStyleParentStyleNameAttribute().isBlank()) {
                    String family = style.getFamilyName();
                    if (family != null && !family.isBlank()) {
                        style.setStyleParentStyleNameAttribute("proxy_" + family);
                    }
                }
            }
        });
    }

    private void changeParentStyles() {
        updateStyleNodes(Nodes.children(doc.getStylesDom().getOfficeStyles()));
        updateStyleNodes(Nodes.children(doc.getStylesDom().getAutomaticStyles()));
        updateStyleNodes(Nodes.children(doc.getContentDom().getAutomaticStyles()));
    }

    private void createProxyNodes() {
        doc.getStylesDom().getOfficeStyles().getDefaultStyles().forEach(defaultStyle -> {
            String family = defaultStyle.getFamilyName();
            StyleStyleElement proxyNode = doc.getStylesDom().getOfficeStyles().newStyleStyleElement(family, "proxy_" + family);
            Nodes.children(defaultStyle).forEach(childToClone -> {
                Node clone = childToClone.cloneNode(true);
                proxyNode.appendChild(clone);
            });
            proxyNodes.add(proxyNode);
        });
    }
}
