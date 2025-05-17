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

package org.underdocx.doctypes.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.underdocx.common.codec.JsonCodec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.ignore.IgnoreStateHandler;
import org.underdocx.doctypes.commands.internal.AbstractCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.attrinterpreter.accesstype.AccessType;
import org.underdocx.doctypes.tools.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.doctypes.tools.datapicker.AbstractConvertDataPicker;
import org.underdocx.doctypes.tools.datapicker.DataPickerResult;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.AbstractPredefinedDataNode;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

import java.util.*;

public class AliasCommandHandler<C extends DocContainer<D>, D> extends AbstractCommandHandler<C, ParametersPlaceholderData, D> {

    private static final PredefinedDataPicker<String> aliasKeyPicker = new StringConvertDataPicker().asPredefined("key");
    private static final PredefinedDataPicker<String> replaceKeyPicker = new StringConvertDataPicker().asPredefined("replaceKey");
    private static final PredefinedDataPicker<Set<AttrKeyValue>> attributesPicker = new AliasAttributesPicker().asPredefined("attributes");
    private static final PredefinedDataPicker<Map<String, String>> attrReplacementsPocker = new AliasAttrReplacePicker().asPredefined("attrReplacements");

    public static class AttrKeyValue {
        protected final String attrKey;
        protected final DataNode<?> attrValue;

        public AttrKeyValue(String attrKey, DataNode<?> attrValue) {
            this.attrKey = attrKey;
            this.attrValue = attrValue;
        }
    }

    public static class AliasData {
        protected final String aliasKey;
        protected final String replaceKey;
        protected final Set<AttrKeyValue> attributes = new HashSet<>();
        protected final Map<String, String> attrReplacements = new HashMap<>();

        public AliasData(String aliasKey, String replaceKey) {
            this.aliasKey = aliasKey;
            this.replaceKey = replaceKey;
        }

        public AliasData(String aliasKey, String replaceKey, Set<AttrKeyValue> attributes) {
            this.aliasKey = aliasKey;
            this.attributes.addAll(attributes);
            this.replaceKey = replaceKey;
        }

        public AliasData(String aliasKey, String replaceKey, Set<AttrKeyValue> attributes, Map<String, String> attrReplacements) {
            this.aliasKey = aliasKey;
            this.attributes.addAll(attributes);
            this.replaceKey = replaceKey;
            this.attrReplacements.putAll(attrReplacements);
        }

        public AliasData addAttribute(String attrName, DataNode<?> value) {
            attributes.add(new AttrKeyValue(attrName, value));
            return this;
        }

        public AliasData addAttribute(String attrName, String value) {
            attributes.add(new AttrKeyValue(attrName, new LeafDataNode<>(value)));
            return this;
        }

        public AliasData addAttrReplacement(String attr, String attrReplace) {
            attrReplacements.put(attr, attrReplace);
            return this;
        }
    }

    private static class AliasAttributesPicker extends AbstractConvertDataPicker<Set<AttrKeyValue>> {

        @Override
        protected Optional<Set<AttrKeyValue>> convert(DataNode<?> attributesNode) {
            return Convenience.buildOptional(new HashSet<>(), result -> {
                if (attributesNode != null && attributesNode.getType() != DataNode.DataNodeType.MAP) {
                    result.value = null;
                    return;
                }
                if (attributesNode != null) {
                    attributesNode.getPropertyNames().forEach(propertyName ->
                            result.value.add(new AttrKeyValue(propertyName, attributesNode.getProperty(propertyName))));
                }
            });
        }
    }

    private static class AliasAttrReplacePicker extends AbstractConvertDataPicker<Map<String, String>> {

        @Override
        protected Optional<Map<String, String>> convert(DataNode<?> attrReplacements) {
            return Convenience.buildOptional(new HashMap<>(), result -> {
                if (attrReplacements != null && attrReplacements.getType() != DataNode.DataNodeType.MAP) {
                    result.value = null;
                    return;
                }
                if (attrReplacements != null) {
                    attrReplacements.getPropertyNames().forEach(propertyName -> {
                        DataNode<?> value = attrReplacements.getProperty(propertyName);
                        if (value.getValue() != null && value.getValue() instanceof String) {
                            result.value.put(propertyName, attrReplacements.getProperty(propertyName).getValue().toString());
                        }
                    });
                }
            });
        }
    }

    private final Map<String, AliasData> registry = new HashMap<>();

    public AliasCommandHandler(ModifiersProvider<C, D> modifiers) {
        super(modifiers);
    }

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
        String aliasKey = Problems.MISSING_VALUE.get(aliasKeyPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "key");
        String replaceKey = Problems.MISSING_VALUE.get(replaceKeyPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "replaceKey");
        DataPickerResult<Set<AttrKeyValue>> attributesResult = attributesPicker.pickData(dataAccess, placeholderData.getJson());
        if (!(attributesResult.isResolved() || attributesResult.type == DataPickerResult.ResultType.UNRESOLVED_MISSING_ATTR)) {
            Problems.INVALID_VALUE.fireProperty("attributes");
        }
        DataPickerResult<Map<String, String>> attrReplacements = attrReplacementsPocker.pickData(dataAccess, placeholderData.getJson());
        if (!(attrReplacements.isResolved() || attrReplacements.type == DataPickerResult.ResultType.UNRESOLVED_MISSING_ATTR)) {
            Problems.INVALID_VALUE.fireProperty("attrReplacements");
        }
        registerAlias(new AliasData(
                aliasKey,
                replaceKey,
                attributesResult.optional().orElse(new HashSet<>()),
                attrReplacements.optional().orElse(new HashMap<>())
        ));
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }

    public void registerAlias(AliasData data) {
        registry.put(data.aliasKey, data);
    }

    public void registerAlias(String key, ParametersPlaceholderData parametersPlaceholderData, Pair<String, String>... attrReplacements) {
        AliasData data = new AliasData(key, parametersPlaceholderData.getKey());
        parametersPlaceholderData.getJson().properties().forEach(entry -> {
            String attrKey = entry.getKey();
            JsonNode jsonValue = entry.getValue();
            Object obj = new JsonCodec().getAsObject(jsonValue);
            DataNode<?> node = AbstractPredefinedDataNode.createRootNode(obj);
            AttrKeyValue attr = new AttrKeyValue(attrKey, node);
            data.attributes.add(attr);
        });
        Arrays.stream(attrReplacements).forEach(pair -> data.attrReplacements.put(pair.left, pair.right));
        registerAlias(data);
    }

    public void registerAlias(String key, String placeholderToParse, Pair<String, String>... attrReplacements) {
        selection.getPlaceholderToolkit().ifPresent(toolkit -> {
            ParametersPlaceholderData parametersPlaceholderData = Problems.PLACEHOLDER_PARSE_ERROR.exec(() -> toolkit.parsePlaceholder(placeholderToParse.trim()));
            registerAlias(key, parametersPlaceholderData, attrReplacements);
        });
    }

    private CommandHandlerResult replaceAllAliasNodes() {
        CommandHandlerResult result = CommandHandlerResult.IGNORED;
        ParametersPlaceholderData currentData = selection.getPlaceholderData();
        if (tryAliasExchange(currentData)) {
            selection.getPlaceholderToolkit().get().setPlaceholder(selection.getNode(), currentData);
            result = CommandHandlerResult.FACTORY.startAtNode(selection.getNode());

            // Replace future placeholder (if not ignored)
            IgnoreStateHandler ignoreState = new IgnoreStateHandler();
            Enumerator<SelectedNode<?>> allWaiting = selection.getEngineAccess().lookAhead(selectedNode -> selectedNode.getPlaceholderData() instanceof ParametersPlaceholderData);
            for (SelectedNode<?> waiting : allWaiting) {
                ParametersPlaceholderData placeholderData = (ParametersPlaceholderData) waiting.getPlaceholderData();
                if (ignoreState.interpretAndCheckExecution(placeholderData).left && tryAliasExchange(placeholderData)) {
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
        new HashSet<>(json.properties()).forEach(property -> {
            String propertyName = property.getKey();
            String purePropertyName = AccessType.getPureName(property.getKey());
            AccessType accessType = AccessType.getTypeOf(propertyName);
            String newPurePropertyName = replaceData.attrReplacements.get(purePropertyName);
            if (newPurePropertyName != null) {
                ((ObjectNode) json).put(accessType.rename(newPurePropertyName), property.getValue());
                ((ObjectNode) json).remove(propertyName);
            }
        });
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
