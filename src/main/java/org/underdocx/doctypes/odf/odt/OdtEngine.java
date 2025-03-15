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

package org.underdocx.doctypes.odf.odt;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.underdocx.doctypes.odf.AbstractOdfEngine;
import org.underdocx.doctypes.odf.commands.*;
import org.underdocx.doctypes.odf.commands.forcommand.ForCommandHandler;
import org.underdocx.doctypes.odf.commands.forcommand.ForListCommandHandler;
import org.underdocx.doctypes.odf.commands.forcommand.ForRowsCommandHandler;
import org.underdocx.doctypes.odf.odt.commands.ExportCommandHandler;
import org.underdocx.doctypes.odf.odt.commands.OdtImportCommandHandler;
import org.underdocx.doctypes.odf.odt.commands.UnderdocxCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;


public class OdtEngine extends AbstractOdfEngine<OdtContainer, OdfTextDocument> {

    private final ModelEngine<OdtContainer, OdfTextDocument> engine;

    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new ModelCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new StringCommandHandler<OdtContainer, OdfTextDocument>(modifiers));
        engine.registerCommandHandler(parameters, new ShortModelStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortVarStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new VariableCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DateCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new TimeCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new CounterCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new IfCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForRowsCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new OdtImportCommandHandler(modifiers));
        engine.registerCommandHandler(imagePlaceholdersProvider, new ImageCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForListCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new PageStyleCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ExportCommandHandler(modifiers));
        engine.registerCommandHandler(parameters, multiCommandHandler);
        engine.registerCommandHandler(parameters, new JoinCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DeleteNodesEodHandler<OdtContainer, OdfTextDocument>(modifiers));
        engine.registerCommandHandler(parameters, new UnderdocxCommandHandler(modifiers));
        engine.registerCommandHandler(parameters, aliasCommandHandler);
        engine.registerCommandHandler(parameters, new NumberCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new RemoveCommandHandler<>(modifiers));
    }

    public OdtEngine(OdtContainer doc) {
        this.engine = new ModelEngine<>(doc);
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<OdtContainer, OdfTextDocument> getEngine() {
        return engine;
    }
}
