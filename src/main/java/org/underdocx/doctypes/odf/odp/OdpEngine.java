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

package org.underdocx.doctypes.odf.odp;

import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.underdocx.doctypes.odf.AbstractOdfEngine;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.odtengine.commands.*;
import org.underdocx.enginelayers.odtengine.commands.forcommand.ForCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.forcommand.ForListCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.forcommand.ForRowsCommandHandler;

public class OdpEngine extends AbstractOdfEngine<OdpContainer, OdfPresentationDocument> {

    private final ModelEngine<OdpContainer, OdfPresentationDocument> engine;

    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new ModelCommandHandler<>());
        engine.registerCommandHandler(parameters, new StringCommandHandler<>());
        engine.registerCommandHandler(parameters, new ShortModelStringCommandHandler<>());
        engine.registerCommandHandler(parameters, new ShortVarStringCommandHandler<>());
        engine.registerCommandHandler(parameters, new VariableCommandHandler<>());
        engine.registerCommandHandler(parameters, new ForCommandHandler<>());
        engine.registerCommandHandler(parameters, new DateCommandHandler<>());
        engine.registerCommandHandler(parameters, new CounterCommandHandler<>());
        engine.registerCommandHandler(parameters, new IfCommandHandler<>());
        engine.registerCommandHandler(parameters, new ForRowsCommandHandler<>());
        engine.registerCommandHandler(imagePlaceholdersProvider, new ImageCommandHandler<>());
        engine.registerCommandHandler(parameters, new ForListCommandHandler<>());
        engine.registerCommandHandler(parameters, new PageStyleCommandHandler<>());
        engine.registerCommandHandler(parameters, multiCommandHandler);
        engine.registerCommandHandler(parameters, new JoinCommandHandler<>());
        engine.registerCommandHandler(parameters, new DeleteNodesEodHandler<>());
        engine.registerCommandHandler(parameters, aliasCommandHandler);
        engine.registerCommandHandler(parameters, new OdpImportCommandHandler());
        engine.registerCommandHandler(parameters, new NumberCommandHandler<>());
    }

    public OdpEngine(OdpContainer doc) {
        this.engine = new ModelEngine<>(doc);
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<OdpContainer, OdfPresentationDocument> getEngine() {
        return engine;
    }
}
