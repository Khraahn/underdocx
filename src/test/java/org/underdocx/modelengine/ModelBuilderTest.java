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

import org.underdocx.AbstractTest;
import org.underdocx.enginelayers.modelengine.model.ModelNode;
import org.underdocx.enginelayers.modelengine.model.simple.MapModelNode;
import org.underdocx.enginelayers.modelengine.model.simple.ModelBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModelBuilderTest extends AbstractTest {

    @Test
    public void testModelBuilder() {
        ModelNode data = ModelBuilder
                /*  */.beginMap() //
                /*    */.add("projectName", "Project") //
                /*    */.beginList("members") //
                /*      */.beginMap() //
                /*        */.add("firstName", "Claudia") //
                /*        */.add("lastName", "Schmidt") //
                /*      */.end() //
                /*      */.beginMap() //
                /*        */.add("firstName", "Klaus") //
                /*        */.add("lastName", "Müller") //
                /*      */.end() //
                /*    */.end() //
                /*  */.end() //
                /*  */.build();

        MapModelNode node = new MapModelNode("""
                {
                    persons: [
                        {birthday: "1975-05-14"},
                        {birthday: "2021-03-07"}
                    ]
                }
                """);


        Assertions.assertThat(data.getProperty("projectName").getValue()).isEqualTo("Project");
        Assertions.assertThat(data.getProperty("members").getSize()).isEqualTo(2);
        Assertions.assertThat(data.getProperty("members").getProperty(1).getProperty("lastName").getValue()).isEqualTo("Müller");
    }
}
