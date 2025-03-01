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

package org.underdocx.modelengine;

import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImportDataTest extends AbstractOdtTest {

    @Test
    public void testImport() {
        String importData = """
                {
                    variables: {
                        "age": "49"
                    },
                    model: {
                        title: "Hello World"
                    },
                    stringReplacements: {
                        name: "Hans Müller"
                    },
                    alias: [
                        {
                            key: "myDate", 
                            replaceKey: "Date", 
                            attributes: {
                                outputFormat: "dd.MM.yyyy"
                            }
                        }
                    ]
                }   
                """;
        String docContent = """
                ${*title}
                My Name is ${name}
                Today is ${myDate}
                My age is ${$age}
                """;
        OdtContainer doc = new OdtContainer(docContent);
        OdtEngine engine = new OdtEngine(doc);
        engine.importData(importData);
        engine.run();
        //show(doc);
        String ddmmyyyy = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        assertNoPlaceholders(doc);
        assertContains(doc, "Hello World");
        assertContains(doc, "My Name is Hans Müller");
        assertContains(doc, "Today is " + ddmmyyyy);
        assertContains(doc, "My age is 49");
    }
}
