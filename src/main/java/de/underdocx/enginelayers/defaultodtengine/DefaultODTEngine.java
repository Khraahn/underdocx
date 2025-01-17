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

package de.underdocx.enginelayers.defaultodtengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.underdocx.common.doc.odf.OdtContainer;
import de.underdocx.enginelayers.baseengine.CommandHandler;
import de.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.commands.SimpleDollarImageReplaceCommand;
import de.underdocx.enginelayers.baseengine.commands.SimpleReplaceFunctionCommand;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import de.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholdersProvider;
import de.underdocx.enginelayers.defaultodtengine.commands.*;
import de.underdocx.enginelayers.defaultodtengine.commands.forcommand.ForCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.forcommand.ForListCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.forcommand.ForRowsCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.image.ImagePlaceholdersProvider;
import de.underdocx.enginelayers.modelengine.MCommandHandler;
import de.underdocx.enginelayers.modelengine.ModelEngine;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.AbstractPredefinedModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.LeafModelNode;
import de.underdocx.enginelayers.modelengine.model.simple.ReflectionModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderProvider;
import de.underdocx.enginelayers.parameterengine.commands.CurrentDateCommand;
import de.underdocx.environment.err.Problem;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Regex;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DefaultODTEngine {

    private final SimpleDollarImagePlaceholdersProvider simpleDollarImage;
    private final SimpleDollarPlaceholdersProvider<OdtContainer, OdfTextDocument> simpleDollar;
    private final ParametersPlaceholderProvider<OdtContainer, OdfTextDocument> parameters;
    private final ImagePlaceholdersProvider imagePlaceholdersProvider;
    private final MultiCommandHandler<OdtContainer, OdfTextDocument> multiCommandHandler = new MultiCommandHandler<>();

    private final ModelEngine<OdtContainer, OdfTextDocument> engine;


    protected void registerDefaultCommandHandlers() {
        engine.registerCommandHandler(parameters, new CurrentDateCommand<>());
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
        engine.registerCommandHandler(parameters, new ImportCommandHander());
        engine.registerCommandHandler(imagePlaceholdersProvider, new ImageCommandHandler());
        engine.registerCommandHandler(parameters, new ForListCommandHandler<>());
        engine.registerCommandHandler(parameters, new PageStyleCommandHandler<>());
        engine.registerCommandHandler(parameters, new ExportCommandHandler());
        engine.registerCommandHandler(parameters, multiCommandHandler);
    }

    public void registerStringReplacement(Regex key, String replacement, boolean forceRescan) {
        multiCommandHandler.registerStringReplacement(key, replacement, forceRescan);
    }

    public void registerStringReplacement(String key, String replacement) {
        multiCommandHandler.registerStringReplacement(key, replacement);
    }

    public DefaultODTEngine(OdtContainer doc) {
        this.engine = new ModelEngine<>(doc);
        simpleDollar = new SimpleDollarPlaceholdersProvider<>(doc);
        simpleDollarImage = new SimpleDollarImagePlaceholdersProvider(doc);
        parameters = new ParametersPlaceholderProvider<>(doc);
        imagePlaceholdersProvider = new ImagePlaceholdersProvider(doc);
        registerDefaultCommandHandlers();
    }

    public <P> void registerCommandHandler(PlaceholdersProvider<OdtContainer, P, OdfTextDocument> provider, CommandHandler<OdtContainer, P, OdfTextDocument> commandHandler) {
        this.engine.registerCommandHandler(provider, commandHandler);
    }

    public void registerParametersCommandHandler(MCommandHandler<OdtContainer, ParametersPlaceholderData, OdfTextDocument> commandHandler) {
        this.engine.registerCommandHandler(parameters, commandHandler);
    }

    public void registerSimpleDollarReplaceFunction(Function<String, Optional<String>> replaceFunction) {
        registerCommandHandler(simpleDollar, new SimpleReplaceFunctionCommand<>(replaceFunction));
    }

    public void registerSimpleDollarReplacement(String variableName, String replacement) {
        this.registerSimpleDollarReplacement(Pattern.compile(Pattern.quote(variableName)), replacement);
    }

    public void registerSimpleDollarReplacement(Pattern pattern, String replacement) {
        this.registerSimpleDollarReplaceFunction(variableName ->
                new Regex(pattern).matches(variableName) ? Optional.of(replacement) : Optional.empty()
        );
    }

    public void registerSimpleDollarImageReplaceFunction(Function<String, Optional<URI>> replaceFunction, boolean keepWidth) {
        registerCommandHandler(simpleDollarImage, new SimpleDollarImageReplaceCommand<>(replaceFunction, keepWidth));
    }

    public void registerSimpleDollarImageReplacement(String variableName, String imageURL, boolean keepWidth) {
        this.registerSimpleDollarImageReplacement(Pattern.compile(Pattern.quote(variableName)), imageURL, keepWidth);
    }

    public void registerSimpleDollarImageReplacement(Pattern pattern, String imageURI, boolean keepWidth) {
        this.registerSimpleDollarImageReplaceFunction(variableName ->
                {
                    try {
                        return new Regex(pattern).matches(variableName) ? Optional.of(new URI(imageURI)) : Optional.empty();
                    } catch (Exception e) {
                        return Problems.INVALID_VALUE.toProblem().value(imageURI).exception(e).fire();
                    }
                }, keepWidth
        );
    }

    public void setModel(ModelNode tree) {
        engine.setModelRoot(tree);
    }


    public void setModel(Object object) {
        engine.setModelRoot(new ReflectionModelNode(object));
    }

    public void setModel(Object object, ReflectionModelNode.Resolver resolver) {
        engine.setModelRoot(new ReflectionModelNode(object, resolver));
    }

    public void pushVariable(String name, ModelNode tree) {
        engine.pushVariable(name, tree);
    }

    public void pushVariable(String name, Object object) {
        engine.pushVariable(name, new ReflectionModelNode(object));
    }

    public void pushVariable(String name, Object object, ReflectionModelNode.Resolver resolver) {
        engine.pushVariable(name, new ReflectionModelNode(object, resolver));
    }

    public void pushLeafVariable(String name, Object value) {
        engine.pushVariable(name, new LeafModelNode<>(value));
    }

    public void pushJsonVariable(String name, String json) throws JsonProcessingException {
        pushVariable(name, AbstractPredefinedModelNode.createFromJson(json));
    }


    public Optional<Problem> run() {
        return engine.run();
    }
}
