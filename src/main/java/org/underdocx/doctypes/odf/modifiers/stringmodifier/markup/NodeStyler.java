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

package org.underdocx.doctypes.odf.modifiers.stringmodifier.markup;

import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

public class NodeStyler {


    public void styleNodes(Node node, Map<Node, TextStyle> styles) {
        TextStyle styleToApply = styles.get(node);
        if (styleToApply != null) {
            TextSpanElement span = (TextSpanElement) node;
            StyleStyleElement style = span.getOrCreateUnqiueAutomaticStyle(true, OdfStyleFamily.Text);
            StyleTextPropertiesElement textStyle = style.newStyleTextPropertiesElement("true");
            textStyle.removeAttribute("text:display");
            fillTextStyle(textStyle, styleToApply);
        }
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node child = nodeList.item(i);
                if (child.getNodeType() != Node.TEXT_NODE) {
                    styleNodes(child, styles);
                }
            }
        }
    }


    private void fillTextStyle(StyleTextPropertiesElement textStyle, TextStyle styleToApply) {
        if (styleToApply.italic) {
            textStyle.setFoFontStyleAttribute("italic");
            textStyle.setStyleFontStyleComplexAttribute("italic");
        }
        if (styleToApply.bold) {
            textStyle.setFoFontWeightAttribute("bold");
            textStyle.setStyleFontWeightAsianAttribute("bold");
            textStyle.setStyleFontWeightComplexAttribute("bold");
        }
        if (styleToApply.underline) {
            textStyle.setStyleTextUnderlineStyleAttribute("solid");
            textStyle.setStyleTextUnderlineWidthAttribute("auto");
            textStyle.setStyleTextUnderlineColorAttribute("font-color");
        }
        if (styleToApply.fontSize != null) {
            textStyle.setFoFontSizeAttribute(styleToApply.fontSize);
            textStyle.setStyleFontSizeComplexAttribute(styleToApply.fontSize);
            textStyle.setStyleFontStyleAsianAttribute(styleToApply.fontSize);
        }
        if (styleToApply.fontName != null) {
            textStyle.setStyleFontNameAttribute(styleToApply.fontName);
        }
    }
}
