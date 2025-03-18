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

package org.underdocx.doctypes;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.underdocx.doctypes.commands.AliasCommandHandler;
import org.underdocx.enginelayers.baseengine.CommandHandler;
import org.underdocx.enginelayers.baseengine.PlaceholdersProvider;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.AbstractPredefinedDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.MapDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.ReflectionDataNode;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problem;
import org.underdocx.environment.err.Problems;

import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class AbstractEngine<C extends DocContainer<D>, D> implements EngineAPI {


    abstract protected ModelEngine<C, D> getEngine();

    public abstract void registerStringReplacement(String key, String replacement);

    public abstract void registerAlias(AliasCommandHandler.AliasData aliasData);

    private <P> void registerCommandHandler(PlaceholdersProvider<C, P, D> provider, CommandHandler<C, P, D> commandHandler) {
        getEngine().registerCommandHandler(provider, commandHandler);
    }

    public <P> void registerCommandHandler(PlaceholdersProvider.Factory<C, P, D> provider, CommandHandler<C, P, D> commandHandler) {
        this.getEngine().registerCommandHandler(provider, commandHandler);
    }

    public abstract void registerParametersCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> commandHandler);


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
                parseMapWithKeyValueProperties(aliasListItem, "attributes", aliasData::addAttribute);
                parseMapWithKeyValueProperties(aliasListItem, "attrReplacements", (propName, propVal) -> {
                    aliasData.addAttrReplacement(propName, parseString(propVal));
                });
                registerAlias(aliasData);
            }
        }
    }

    public void importData(String json) {
        importData(new MapDataNode(json));
    }

}
