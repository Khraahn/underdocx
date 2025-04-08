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

package org.underdocx.cli;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.AbstractOdtTest;
import org.underdocx.common.cli.UnderdocxEngineRunner;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class CLITest extends AbstractOdtTest {

    private static String importData = """
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
                        },
                        attrReplacements: {
                            of:"outputFormat",
                            v:"value"
                        }
                    }
                ]
            }   
            """;

    private static String docContent = """
            ${*title}
            My Name is ${name}
            Today is ${myDate v:"2022-05-14"}
            My age is ${$age}
            """;


    @Test
    public void testCLI() throws Exception {
        File jsonFile = createTmpFile(new ByteArrayInputStream(importData.getBytes()), "json", 10000L);
        OdtContainer doc = new OdtContainer(docContent);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        doc.save(os);
        File docFile = createTmpFile(new ByteArrayInputStream(os.toByteArray()), "odt", 10000L);
        File targetFile = createTmpFile("odt");
        String[] args = new String[]{
                "odt",
                docFile.getAbsolutePath(),
                targetFile.getAbsolutePath(),
                jsonFile.getAbsolutePath()
        };

        UnderdocxEngineRunner.Runner<?, ?> runner = new UnderdocxEngineRunner.Runner<>(args);
        int code = runner.run();
        Assertions.assertThat(code).isEqualTo(0);
        OdtContainer newDoc = new OdtContainer(targetFile);
        //show(newDoc);
        assertNoPlaceholders(newDoc);
        assertContains(newDoc, "Hello World");
        assertContains(newDoc, "My Name is Hans Müller");
        assertContains(newDoc, "Today is 14.05.2022");
        assertContains(newDoc, "My age is 49");
    }

    @Test
    public void testImport() {
        OdtContainer doc = new OdtContainer(docContent);
        OdtEngine engine = new OdtEngine();
        engine.importData(importData);
        engine.run(doc);
        //show(doc);
        assertNoPlaceholders(doc);
        assertContains(doc, "Hello World");
        assertContains(doc, "My Name is Hans Müller");
        assertContains(doc, "Today is 14.05.2022");
        assertContains(doc, "My age is 49");
    }

}
