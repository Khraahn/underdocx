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

package org.underdocx.doctypes.odf.ods;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.underdocx.doctypes.commands.*;
import org.underdocx.doctypes.odf.AbstractOdfEngine;
import org.underdocx.doctypes.odf.commands.*;
import org.underdocx.doctypes.odf.commands.forcommand.ForListCommandHandler;
import org.underdocx.doctypes.odf.commands.forcommand.ForRowsCommandHandler;
import org.underdocx.doctypes.odf.tools.placeholder.OdfDefaultPlaceholderFactory;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.UnderdocxEnv;

public class OdsEngine extends AbstractOdfEngine<OdsContainer, OdfSpreadsheetDocument> {

    private final ModelEngine<OdsContainer, OdfSpreadsheetDocument> engine;

    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new IgnoreCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ModelCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new StringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortModelStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortVarStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new VariableCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new OdfDateCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new OdfTimeCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new CounterCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new IfCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ForRowsCommandHandler<>(modifiers));
        if (!UnderdocxEnv.getInstance().disableImagePlaceholderProvider) {
            engine.registerCommandHandler(imagePlaceholdersProvider, new ImageCommandHandler<>(modifiers));
        }
        engine.registerCommandHandler(parameters, new ForListCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new PageStyleCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, multiCommandHandler);
        engine.registerCommandHandler(parameters, new JoinCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DeleteNodesEodHandler<>(modifiers));
        engine.registerCommandHandler(parameters, aliasCommandHandler);
        engine.registerCommandHandler(parameters, new OdfNumberCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new RemoveCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new OdfCloneCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ConcatCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new CalcCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new CreateImageCommandHandler<>(modifiers));
    }

    public OdsEngine() {
        this(new OdfDefaultPlaceholderFactory<>());
    }

    public OdsEngine(GenericTextualPlaceholderFactory<OdsContainer, ParametersPlaceholderData, OdfSpreadsheetDocument> parameters) {
        super(parameters);
        this.engine = new ModelEngine<>();
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<OdsContainer, OdfSpreadsheetDocument> getEngine() {
        return engine;
    }
}
