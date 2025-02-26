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

package org.underdocx.enginelayers.odtengine.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.underdocx.common.codec.JsonCodec;
import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.baseengine.modifiers.deleteplaceholder.DeletePlaceholderModifier;
import org.underdocx.enginelayers.modelengine.model.DataNode;
import org.underdocx.enginelayers.modelengine.model.simple.AbstractPredefinedDataNode;
import org.underdocx.enginelayers.odtengine.commands.internal.AbstractCommandHandler;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.AbstractConvertDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.DataPickerResult;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.odtengine.commands.internal.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.enginelayers.parameterengine.internal.ParametersPlaceholderCodec;
import org.underdocx.environment.err.Problems;

import java.util.*;

public class AliasCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    private static final PredefinedDataPicker<String> aliasKeyPicker = new StringConvertDataPicker().asPredefined("key");
    private static final PredefinedDataPicker<String> replaceKeyPicker = new StringConvertDataPicker().asPredefined("replaceKey");
    private static final PredefinedDataPicker<Set<AttrKeyValue>> attributesPicker = new AliasAttributesPicker().asPredefined("attributes");

    public static class AttrKeyValue {
        public String attrKey;
        public DataNode attrValue;

        public AttrKeyValue(String attrKey, DataNode attrValue) {
            this.attrKey = attrKey;
            this.attrValue = attrValue;
        }
    }

    public static class AliasData {
        public String aliasKey;
        public String replaceKey;
        public Set<AttrKeyValue> attributes = new HashSet<>();

        public AliasData(String aliasKey, String replaceKey) {
            this.aliasKey = aliasKey;
            this.replaceKey = replaceKey;
        }

        public AliasData(String aliasKey, String replaceKey, Set<AttrKeyValue> attributes) {
            this.aliasKey = aliasKey;
            this.attributes = attributes;
            this.replaceKey = replaceKey;
        }
    }

    private static class AliasAttributesPicker extends AbstractConvertDataPicker<Set<AttrKeyValue>> {

        @Override
        protected Optional<Set<AttrKeyValue>> convert(DataNode attributesNode) {
            return Convenience.buildOptional(new HashSet<>(), result -> {
                if (attributesNode != null && attributesNode.getType() != DataNode.ModelNodeType.MAP) {
                    result.value = null;
                    return;
                }
                if (attributesNode != null) {
                    attributesNode.getPropertyNames().forEach(propertyName -> {
                        result.value.add(new AttrKeyValue(propertyName, attributesNode.getProperty(propertyName)));
                    });
                }
            });
        }
    }


    private final Map<String, AliasData> registry = new HashMap<>();

    @Override
    protected CommandHandlerResult tryExecuteCommand() {
        if (selection.getPlaceholderData().getKey().equals("Alias")) {
            return registerAlias();
        }
        if (registry.containsKey(selection.getPlaceholderData().getKey())) {
            return replaceAllAliasNodes();
        }
        return CommandHandlerResult.IGNORED;
    }

    private CommandHandlerResult registerAlias() {
        String aliasKey = Problems.MISSING_VALUE.get(aliasKeyPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue(), "key");
        String replaceKey = Problems.MISSING_VALUE.get(replaceKeyPicker.pickData(dataAccess, placeholderData.getJson()).getOptionalValue(), "replaceKey");
        DataPickerResult<Set<AttrKeyValue>> attributesResult = attributesPicker.pickData(dataAccess, placeholderData.getJson());
        if (!(attributesResult.isResolved() || attributesResult.type == DataPickerResult.ResultType.UNRESOLVED_MISSING_ATTR)) {
            Problems.INVALID_VALUE.fireProperty("attributes");
        }
        registerAlias(new AliasData(aliasKey, replaceKey, attributesResult.getOptionalValue().orElse(new HashSet<>())));
        return CommandHandlerResult.FACTORY.convert(DeletePlaceholderModifier.modify(selection.getNode()));
    }

    private void registerAlias(AliasData data) {
        registry.put(data.aliasKey, data);
    }

    public void registerAlias(String key, ParametersPlaceholderData parametersPlaceholderData) {
        AliasData data = new AliasData(key, parametersPlaceholderData.getKey());
        parametersPlaceholderData.getJson().properties().forEach(entry -> {
            String attrKey = entry.getKey();
            JsonNode jsonValue = entry.getValue();
            Object obj = new JsonCodec().getAsObject(jsonValue);
            DataNode node = AbstractPredefinedDataNode.createRootNode(obj);
            AttrKeyValue attr = new AttrKeyValue(attrKey, node);
            data.attributes.add(attr);
        });
        registerAlias(data);
    }

    public void registerAlias(String key, String placeholderToParse) {
        ParametersPlaceholderData parametersPlaceholderData = Problems.PLACEHOLDER_PARSE_ERROR.exec(() -> ParametersPlaceholderCodec.INSTANCE.parse(placeholderToParse.trim()));
        registerAlias(key, parametersPlaceholderData);
    }

    private CommandHandlerResult replaceAllAliasNodes() {
        CommandHandlerResult result = CommandHandlerResult.IGNORED;
        ParametersPlaceholderData currentData = selection.getPlaceholderData();
        if (tryAliasExchange(currentData)) {
            selection.getPlaceholderToolkit().get().setPlaceholder(selection.getNode(), currentData);
            result = CommandHandlerResult.FACTORY.startAtNode(selection.getNode());
            List<SelectedNode<?>> allWaiting = selection.getEngineAccess().lookAhead(selectedNode -> selectedNode.getPlaceholderData() instanceof ParametersPlaceholderData);
            for (SelectedNode<?> waiting : allWaiting) {
                ParametersPlaceholderData placeholderData = (ParametersPlaceholderData) waiting.getPlaceholderData();
                if (tryAliasExchange(placeholderData)) {
                    selection.getPlaceholderToolkit().get().setPlaceholder(waiting.getNode(), placeholderData);
                }
            }
        }
        return result;
    }

    private boolean tryAliasExchange(ParametersPlaceholderData placeholderData) {
        String oldKey = placeholderData.getKey();
        AliasData replaceData = registry.get(oldKey);
        if (replaceData == null) {
            return false;
        }
        placeholderData.setKey(replaceData.replaceKey);
        JsonNode json = placeholderData.getOrCreateJson();
        replaceData.attributes.forEach(attribute -> {
            Optional<JsonNode> existingJsonNode = AttributeInterpreterFactory.createJsonAttributeInterpreter(true, AccessType.getPureName(attribute.attrKey)).interpretAttributes(json);
            if (existingJsonNode.isEmpty()) {
                Optional<JsonNode> oNode = new JsonCodec().convertMapToJson(AbstractPredefinedDataNode.convert(attribute.attrValue));
                oNode.ifPresent(jsonNode -> ((ObjectNode) json).set(attribute.attrKey, jsonNode));
            }
        });
        return true;
    }


}
