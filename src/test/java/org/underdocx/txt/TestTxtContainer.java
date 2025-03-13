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

package org.underdocx.txt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.underdocx.common.codec.XMLCodec;
import org.underdocx.doctypes.txt.TextContainer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestTxtContainer {

    @Test
    public void readConvertAndWriteBack2() throws IOException {
        TextContainer doc = new TextContainer("" +
                "A B CDE<>   F\n" +
                "G   HIJ!  \n" +
                " ---");
        String xml = XMLCodec.DEFAULT.getTextContent(doc.getDocument().getDoc());
        System.out.println(xml);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        doc.save(bos);
        String string = bos.toString();

        String[] split = string.split("\n");
        Assertions.assertThat(split[0]).isEqualTo("A B CDE<>   F");
        Assertions.assertThat(split[1]).isEqualTo("G   HIJ!  ");
        Assertions.assertThat(split[2]).isEqualTo(" ---");
        Assertions.assertThat(split.length).isEqualTo(3);
    }
}
