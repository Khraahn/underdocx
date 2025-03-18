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

package org.underdocx.doctypes.txt;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.AbstractEngine;
import org.underdocx.doctypes.commands.*;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.doctypes.txt.commands.TxtImportCommandHandler;
import org.underdocx.doctypes.txt.modifiers.TxtModifiersProvider;
import org.underdocx.doctypes.txt.placeholders.TxtParameterizedPlaceholderFactory;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;


public class TxtEngine extends AbstractEngine<TxtContainer, TxtXml> {

    private final ModelEngine<TxtContainer, TxtXml> engine;

    protected final TxtModifiersProvider modifiers = new TxtModifiersProvider();
    protected GenericTextualPlaceholderFactory<TxtContainer, ParametersPlaceholderData, TxtXml> parameters;
    protected final MultiCommandHandler<TxtContainer, TxtXml> multiCommandHandler = new MultiCommandHandler<>(modifiers);
    protected final AliasCommandHandler<TxtContainer, TxtXml> aliasCommandHandler = new AliasCommandHandler<>(modifiers);


    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new ModelCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new StringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortModelStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new ShortVarStringCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new VariableCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DeleteNodesEodHandler<>(modifiers));

        engine.registerCommandHandler(parameters, new ForCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new DateCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new TimeCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, aliasCommandHandler);
        engine.registerCommandHandler(parameters, multiCommandHandler);
        engine.registerCommandHandler(parameters, new JoinCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new NumberCommandHandler<>(modifiers));

        engine.registerCommandHandler(parameters, new CounterCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new IfCommandHandler<>(modifiers));
        engine.registerCommandHandler(parameters, new TxtImportCommandHandler(modifiers));
    }

    public TxtEngine(TxtContainer doc) {
        this(doc, new TxtParameterizedPlaceholderFactory());
    }

    public TxtEngine(TxtContainer doc, GenericTextualPlaceholderFactory<TxtContainer, ParametersPlaceholderData, TxtXml> parameters) {
        this.parameters = parameters;
        this.engine = new ModelEngine<>(doc);
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<TxtContainer, TxtXml> getEngine() {
        return engine;
    }

    public void registerStringReplacement(String key, String replacement) {
        multiCommandHandler.registerStringReplacement(key, replacement);
    }


    public void registerAlias(String key, String placeholder, Pair<String, String>... attrReplacements) {
        aliasCommandHandler.registerAlias(key, placeholder, attrReplacements);
    }

    public void registerAlias(String key, ParametersPlaceholderData placeholder, Pair<String, String>... attrReplacements) {
        aliasCommandHandler.registerAlias(key, placeholder, attrReplacements);
    }

    public void registerAlias(AliasCommandHandler.AliasData aliasData) {
        aliasCommandHandler.registerAlias(aliasData);
    }

    public void registerParametersCommandHandler(MCommandHandler<TxtContainer, ParametersPlaceholderData, TxtXml> commandHandler) {
        this.getEngine().registerCommandHandler(parameters, commandHandler);
    }
}
