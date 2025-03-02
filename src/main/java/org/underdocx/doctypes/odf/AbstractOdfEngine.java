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

package org.underdocx.doctypes.odf;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.EngineAPI;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.baseengine.commands.SimpleDollarImageReplaceCommand;
import org.underdocx.enginelayers.baseengine.commands.SimpleReplaceFunctionCommand;
import org.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.SimpleDollarPlaceholdersProvider;
import org.underdocx.enginelayers.baseengine.internal.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholdersProvider;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.modelengine.model.DataNode;
import org.underdocx.enginelayers.modelengine.model.simple.AbstractPredefinedDataNode;
import org.underdocx.enginelayers.modelengine.model.simple.LeafDataNode;
import org.underdocx.enginelayers.modelengine.model.simple.MapDataNode;
import org.underdocx.enginelayers.modelengine.model.simple.ReflectionDataNode;
import org.underdocx.enginelayers.odtengine.commands.AliasCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.MultiCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.image.ImagePlaceholdersProvider;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderProvider;
import org.underdocx.environment.err.Problem;
import org.underdocx.environment.err.Problems;

import java.net.URI;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class AbstractOdfEngine<C extends AbstractOdfContainer<D>, D extends OdfDocument> implements EngineAPI {

    public final SimpleDollarImagePlaceholdersProvider.SimpleDollarImagePlaceholdersProviderFactory<C, D> simpleDollarImage
            = new SimpleDollarImagePlaceholdersProvider.SimpleDollarImagePlaceholdersProviderFactory<>();
    public final SimpleDollarPlaceholdersProvider.SimpleDollarPlaceholdersProviderFactory<C, D> simpleDollar
            = new SimpleDollarPlaceholdersProvider.SimpleDollarPlaceholdersProviderFactory<>();
    public final ParametersPlaceholderProvider.ParametersPlaceholderProviderFactory<C, D> parameters
            = new ParametersPlaceholderProvider.ParametersPlaceholderProviderFactory<>();
    public final ImagePlaceholdersProvider.ImagePlaceholdersProviderFactory<C, D> imagePlaceholdersProvider
            = new ImagePlaceholdersProvider.ImagePlaceholdersProviderFactory();

    protected final MultiCommandHandler<C, D> multiCommandHandler = new MultiCommandHandler<>();
    protected final AliasCommandHandler<C, D> aliasCommandHandler = new AliasCommandHandler<>();

    abstract protected ModelEngine<C, D> getEngine();

    public void registerStringReplacement(String key, String replacement) {
        multiCommandHandler.registerStringReplacement(key, replacement);
    }

    public void registerAlias(String key, String placeholder) {
        aliasCommandHandler.registerAlias(key, placeholder);
    }

    public void registerAlias(String key, ParametersPlaceholderData placeholder) {
        aliasCommandHandler.registerAlias(key, placeholder);
    }

    public void registerAlias(AliasCommandHandler.AliasData aliasData) {
        aliasCommandHandler.registerAlias(aliasData);
    }


    private <P> void registerCommandHandler(PlaceholdersProvider<C, P, D> provider, CommandHandler<C, P, D> commandHandler) {
        getEngine().registerCommandHandler(provider, commandHandler);
    }

    public <P> void registerCommandHandler(PlaceholdersProvider.Factory<C, P, D> provider, CommandHandler<C, P, D> commandHandler) {
        this.getEngine().registerCommandHandler(provider, commandHandler);
    }

    public void registerParametersCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> commandHandler) {
        this.getEngine().registerCommandHandler(parameters, commandHandler);
    }

    public void registerSimpleDollarReplaceFunction(Function<String, Optional<String>> replaceFunction) {
        registerCommandHandler(simpleDollar, new SimpleReplaceFunctionCommand<C, D>(replaceFunction));
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

    public void setModel(DataNode tree) {
        getEngine().setModelRoot(tree);
    }


    public void setModel(Object object) {
        getEngine().setModelRoot(new ReflectionDataNode(object));
    }

    public void setModel(Object object, ReflectionDataNode.Resolver resolver) {
        getEngine().setModelRoot(new ReflectionDataNode(object, resolver));
    }

    public void pushVariable(String name, DataNode tree) {
        getEngine().pushVariable(name, tree);
    }

    public void pushVariable(String name, Object object) {
        getEngine().pushVariable(name, new ReflectionDataNode(object));
    }

    public void pushVariable(String name, Object object, ReflectionDataNode.Resolver resolver) {
        getEngine().pushVariable(name, new ReflectionDataNode(object, resolver));
    }

    public void pushLeafVariable(String name, Object value) {
        getEngine().pushVariable(name, new LeafDataNode<>(value));
    }

    public void pushJsonVariable(String name, String json) throws JsonProcessingException {
        pushVariable(name, AbstractPredefinedDataNode.createFromJson(json));
    }

    public Optional<Problem> run() {
        return getEngine().run();
    }

    private void parseMapWithKeyValueProperties(DataNode mainNode, String propertyName, BiConsumer<String, DataNode> consumer) {
        DataNode variablesNode = mainNode.getProperty(propertyName);
        if (variablesNode != null) {
            Problems.IMPORT_DATA_SCHEMA.checkNot(variablesNode.isNull() || variablesNode.getType() != DataNode.DataNodeType.MAP);
            variablesNode.getPropertyNames().forEach(key -> {
                consumer.accept(key, variablesNode.getProperty(key));
            });
        }
    }

    private String parseString(DataNode node, String propertyName) {
        DataNode stringNode = node.getProperty(propertyName);
        return parseString(stringNode);
    }

    private String parseString(DataNode node) {
        Problems.IMPORT_DATA_SCHEMA.checkNot(node.isNull() || node.getType() != DataNode.DataNodeType.LEAF || !(node.getValue() instanceof String));
        return node.getValue().toString();
    }

    public void importData(DataNode importData) {
        parseMapWithKeyValueProperties(importData, "variables", this::pushVariable);
        parseMapWithKeyValueProperties(importData, "stringReplacements", (key, value) -> {
            registerStringReplacement(key, parseString(value));
        });
        DataNode model = importData.getProperty("model");
        if (model != null) {
            Problems.IMPORT_DATA_SCHEMA.checkNot(model.isNull() || model.getType() != DataNode.DataNodeType.MAP);
            setModel(model);
        }
        DataNode aliasListNode = importData.getProperty("alias");
        if (aliasListNode != null) {
            Problems.IMPORT_DATA_SCHEMA.checkNot(aliasListNode.isNull() || aliasListNode.getType() != DataNode.DataNodeType.LIST);
            for (int i = 0; i < aliasListNode.getSize(); i++) {
                DataNode aliasListItem = aliasListNode.getProperty(i);
                Problems.IMPORT_DATA_SCHEMA.checkNot(aliasListNode.isNull() || model.getType() != DataNode.DataNodeType.MAP);
                String key = parseString(aliasListItem, "key");
                String newKey = parseString(aliasListItem, "replaceKey");
                AliasCommandHandler.AliasData aliasData = new AliasCommandHandler.AliasData(key, newKey);
                parseMapWithKeyValueProperties(aliasListItem, "attributes", (propName, propVal) -> {
                    aliasData.attributes.add(new AliasCommandHandler.AttrKeyValue(propName, propVal));
                });
                registerAlias(aliasData);
            }
        }
    }

    public void importData(String json) {
        importData(new MapDataNode(json));
    }


}
