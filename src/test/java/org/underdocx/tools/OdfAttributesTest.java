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

package org.underdocx.tools;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.tree.Nodes;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class OdfAttributesTest extends AbstractOdtTest {


    public String getTextContent(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            String result = stringWriter.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @Test
    public void testRemoveAttrNs() {
        String xmlContent = """
                <root xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0">
                    <table:table table:number-rows-repeated="2">
                    </table:table>
                </root>
                """;

        Document xmlDocument = readXML(xmlContent);
        Node table = Nodes.findDescendantNodes(xmlDocument, "table:table", true).get(0);
        OdfAttribute.TABLE_NUMBER_ROWS_REPEATED.removeAttributeNS((Element) table);

        String newXML = getTextContent(xmlDocument);
        Assertions.assertThat(newXML.indexOf("repeat")).isLessThan(0);

    }
}
