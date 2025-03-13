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

package org.underdocx.odf.parameterengine;

import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.AbstractOdtTest;
import org.underdocx.doctypes.odf.modifiers.stringmodifier.ReplaceWithTextModifier;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import org.underdocx.doctypes.tools.attrinterpreter.AttributesInterpreter;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class ParametersEngineTest extends AbstractOdtTest {

    @Test
    public void testParametersEngine() {
        OdtContainer doc = new OdtContainer("ABC ${Key att1:\"value1\"} XYZ");
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(
                selection -> {
                    String text = AttributesInterpreter.getStringAttribute(selection.getPlaceholderData().getJson(), "att1").get();
                    new ReplaceWithTextModifier<OdtContainer, ParametersPlaceholderData, OdfTextDocument>().modify(selection, text);
                    return CommandHandlerResult.EXECUTED_PROCEED;
                });
        engine.run();
        assertContains(doc, "ABC value1 XYZ");
        assertNoPlaceholders(doc);
    }

    @Test
    public void testParameterKey() {
        OdtContainer doc = new OdtContainer("ABC ${Key att1:\"value1\"} XYZ");
        OdtEngine engine = new OdtEngine(doc);
        engine.registerParametersCommandHandler(
                selection -> {
                    if (selection.getPlaceholderData().getKey().equals("Key")
                            && new ReplaceWithTextModifier<OdtContainer, ParametersPlaceholderData, OdfTextDocument>()
                            .modify(selection, "Test").getSuccess())
                        return CommandHandlerResult.EXECUTED_PROCEED;
                    else return CommandHandlerResult.IGNORED;
                });
        engine.run();
        assertContains(doc, "ABC Test XYZ");
        assertNoPlaceholders(doc);
    }

}
