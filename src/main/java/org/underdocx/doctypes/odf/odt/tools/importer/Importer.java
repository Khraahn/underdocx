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

package org.underdocx.doctypes.odf.odt.tools.importer;

import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.tools.importer.internal.ConsumerScanner;
import org.underdocx.doctypes.odf.odt.tools.importer.internal.FontImporter;
import org.underdocx.doctypes.odf.odt.tools.importer.internal.ProviderScanner;
import org.underdocx.doctypes.odf.odt.tools.importer.rules.AbstractImportRules;
import org.underdocx.doctypes.odf.odt.tools.importer.rules.AttrDescr;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Imports nodes from one document into an other.
 */
public class Importer {

    private final AbstractImportRules importRules;

    public Importer(AbstractImportRules importRules) {
        this.importRules = importRules;
    }

    public Importer() {
        this(ImportRules.DEFAULT);
    }

    public void importDoc(String sourceResourceName, OdtContainer source, OdtContainer target, Node targetRefNodeInsertAfter) {

        // copy (unchanged) font declarations
        FontImporter.importFonts(target, source);

        // rename provider style names
        ProviderScanner sourceProviders = new ProviderScanner(source, importRules.getProviderDescrs());
        sourceProviders.rename(sourceResourceName);

        // remove provider nodes from source that provide (renamed) style names that have been already imported earlier
        ProviderScanner targetProviders = new ProviderScanner(target, importRules.getProviderDescrs());
        for (Map.Entry<String, Pair<Node, AttrDescr>> entry : sourceProviders.getProviderEntries().entrySet()) {
            targetProviders.findProviderByValue(entry.getKey()).ifPresent(_t ->
                    Nodes.deleteNode(entry.getValue().left));
        }

        // rename all consumer style names
        ConsumerScanner consumers = new ConsumerScanner(source, importRules.getConsumerDescrs());
        consumers.rename(sourceResourceName);

        // copy nodes
        importRules.getCopyRules().forEach(copyRule -> {
            copyRule.copy(source, target);
        });
        importRules.getMainCopyRule().copy(source, targetRefNodeInsertAfter);

        // import images
        importImages(source, target);

    }

    private void importImages(OdtContainer source, OdtContainer target) {
        OdfPackage sourcePackage = source.getDocument().getPackage();
        OdfPackage targetPackage = target.getDocument().getPackage();
        sourcePackage.getFilePaths().forEach(path -> {
            if (path.startsWith("Pictures")) {
                byte[] imageData = sourcePackage.getBytes(path);
                targetPackage.insert(imageData, path, null);
            }
        });
    }
}
