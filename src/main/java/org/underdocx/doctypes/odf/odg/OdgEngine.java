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

package org.underdocx.doctypes.odf.odg;

import org.odftoolkit.odfdom.doc.OdfGraphicsDocument;
import org.underdocx.doctypes.commands.*;
import org.underdocx.doctypes.odf.AbstractOdfEngine;
import org.underdocx.doctypes.odf.commands.*;
import org.underdocx.doctypes.odf.commands.forcommand.ForListCommandHandler;
import org.underdocx.doctypes.odf.commands.forcommand.ForRowsCommandHandler;
import org.underdocx.doctypes.odf.odg.commands.OdgImportCommandHandler;
import org.underdocx.doctypes.odf.tools.placeholder.OdfParameterizedPlaceholderFactory;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public class OdgEngine extends AbstractOdfEngine<OdgContainer, OdfGraphicsDocument> {

    private final ModelEngine<OdgContainer, OdfGraphicsDocument> engine;

    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new ModelCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new StringCommandHandler<OdgContainer, OdfGraphicsDocument>(modifiers));
        engine.registerCommandHandler(parameters, new ShortModelStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortVarStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new VariableCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForCommandHandler<OdgContainer, OdfGraphicsDocument>(modifiers));
        engine.registerCommandHandler(parameters, new OdfDateCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new OdfTimeCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new CounterCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new IfCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForRowsCommandHandler<OdgContainer, OdfGraphicsDocument>(modifiers));
        engine.registerCommandHandler(imagePlaceholdersProvider, new ImageCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForListCommandHandler<OdgContainer, OdfGraphicsDocument>(modifiers));
        engine.registerCommandHandler(parameters, new PageStyleCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, multiCommandHandler);
        engine.registerCommandHandler(parameters, new JoinCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DeleteNodesEodHandler<OdgContainer, OdfGraphicsDocument>(modifiers));
        engine.registerCommandHandler(parameters, aliasCommandHandler);
        engine.registerCommandHandler(parameters, new OdgImportCommandHandler(modifiers));
        engine.registerCommandHandler(parameters, new OdfNumberCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new RemoveCommandHandler<>(modifiers));
    }

    public OdgEngine(OdgContainer doc) {
        this(doc, new OdfParameterizedPlaceholderFactory<>());
    }

    public OdgEngine(OdgContainer doc, GenericTextualPlaceholderFactory<OdgContainer, ParametersPlaceholderData, OdfGraphicsDocument> parameters) {
        super(parameters);
        this.engine = new ModelEngine<>(doc);
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<OdgContainer, OdfGraphicsDocument> getEngine() {
        return engine;
    }
}
