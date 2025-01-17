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

package de.underdocx.tools.odf.imports.internal;

import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Convenience;
import de.underdocx.tools.tree.Nodes;
import org.odftoolkit.odfdom.dom.element.office.OfficeFontFaceDeclsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.w3c.dom.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FontImporter {

    public static void importFonts(OdtContainer target, OdtContainer src) {
        OfficeFontFaceDeclsElement targetDecl = findDecl(target);
        OfficeFontFaceDeclsElement srcDecl = findDecl(src);
        if (srcDecl == null) {
            return;
        }
        if (targetDecl == null) {
            targetDecl = createDecl(target);
        }
        Set<String> registeredNames = getFontNames(targetDecl);
        importUnregisteredFonts(targetDecl, srcDecl, registeredNames);
    }

    private static void importUnregisteredFonts(OfficeFontFaceDeclsElement targetDecl, OfficeFontFaceDeclsElement srcDecl, Set<String> registeredNames) {
        Set<StyleFontFaceElement> fontsToImport = getFonts(srcDecl);
        fontsToImport.forEach(fontToImport -> {
            String name = fontToImport.getStyleNameAttribute();
            if (!registeredNames.contains(name)) {
                targetDecl.appendChild(targetDecl.getOwnerDocument().importNode(fontToImport, true));
            }
        });
    }

    private static Set<String> getFontNames(OfficeFontFaceDeclsElement targetDecl) {
        return Convenience.build(new HashSet<>(), result ->
                getFonts(targetDecl).forEach(font -> result.value.add(font.getStyleNameAttribute())));
    }

    private static Set<StyleFontFaceElement> getFonts(OfficeFontFaceDeclsElement decl) {
        return Convenience.build(new HashSet<>(), result -> {
            List<Node> nodes = Nodes.findDescendantNodes(decl, "style:font-face", true);
            for (Node node : nodes) {
                StyleFontFaceElement font = (StyleFontFaceElement) node;
                result.value.add(font);
            }
        });
    }

    private static OfficeFontFaceDeclsElement createDecl(OdtContainer doc) {
        return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.exec(() -> doc.getDocument().getContentDom().getRootElement().newOfficeFontFaceDeclsElement());
    }

    private static OfficeFontFaceDeclsElement findDecl(OdtContainer doc) {
        Optional<Node> ofd = Nodes.findFirstDescendantNode(doc.getContentDom(), "office:font-face-decls");
        if (ofd.isPresent())
            return (OfficeFontFaceDeclsElement) ofd.get();
        else return null;
    }

    private static void copyDecl(OdtContainer target, OdtContainer src) {

    }


}
