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

package de.underdocx.enginelayers.defaultodtengine.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.underdocx.common.doc.DocContainer;
import de.underdocx.enginelayers.baseengine.SelectedNode;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.AreaTools;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.PredefinedAttributesInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataConfig;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataStrategy;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata.MissingDataSzenario;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.AttributeNodeDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.DataPickerResult;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.DataPickerResult.ResultSource;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.ExtendedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModule;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleConfig;
import de.underdocx.enginelayers.defaultodtengine.commands.internal.modifiermodule.missingdata.MissingDataCommandModuleResult;
import de.underdocx.enginelayers.defaultodtengine.modifiers.forloop.ForModifierData;
import de.underdocx.enginelayers.defaultodtengine.modifiers.forloop.ForMofifier;
import de.underdocx.enginelayers.modelengine.model.ModelNode;
import de.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import de.underdocx.environment.err.Problems;
import de.underdocx.tools.common.Pair;
import de.underdocx.tools.common.Regex;
import de.underdocx.tools.odf.OdfTools;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.underdocx.tools.common.Convenience.*;


public class ForCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String BEGIN_KEY = "For";
    public static final String END_KEY = "EndFor";
    public static final Regex KEYS = new Regex(BEGIN_KEY + "|" + END_KEY);

    public static final String VALUE_ATTR = "value";
    public static final String AS_ATTR = "as";

    private static final PredefinedAttributesInterpreter<Optional<String>> getValueStringAttr = AttributeInterpreterFactory.createStringAttributeInterpreter(VALUE_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<JsonNode>> getValueJsonAttr = AttributeInterpreterFactory.createJsonAttributeInterpreter(VALUE_ATTR);
    private static final PredefinedAttributesInterpreter<Optional<String>> getAsStrAttr = AttributeInterpreterFactory.createStringAttributeInterpreter(AS_ATTR);

    public static final String INDEX = "index";


    private Node endNode;

    private AccessType asAttributeType;
    private String asAttrValue;

    private ModelNode listNode;
    private ResultSource source;

    private JsonNode attributes;
    private int index;
    private Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> replaceData;


    public ForCommandHandler() {
        super(KEYS);
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        if (selection.getPlaceholderData().getKey().equals(END_KEY)) {
            return CommandHandlerResult.IGNORED;
        }
        Optional<Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>>> area =
                AreaTools.findArea(selection.getEngineAccess().lookAhead(null), selection, END_KEY);
        endNode = Problems.INVALID_PLACEHOLDER_STRUCTURE.get(area, "EndFor").right.getNode();
        MissingDataCommandModuleResult<ModelNode> missingResult =
                new MissingDataCommandModule<C, D, ModelNode>(
                        getMissingDataModuleConfig(endNode)
                ).execute(selection);
        return switch (missingResult.resultType) {
            case SKIP -> CommandHandlerResult.IGNORED;
            case STRATEGY_EXECUTED -> CommandHandlerResult.EXECUTED_RESCAN_REQUIRED;
            case VALUE_RECEIVED -> {
                source = missingResult.source;
                listNode = missingResult.value;
                attributes = placeholderData.getJson();
                asAttributeType = AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(placeholderData.getJson(), AS_ATTR);
                checkAsAttr();
                asAttrValue = getAsStrAttr.interpretAttributes(placeholderData.getJson()).orElse(null);
                new ForMofifier<C, D>().modify(selection, createModifierData());
                yield CommandHandlerResult.EXECUTED_RESCAN_REQUIRED;
            }
        };
    }

    private void checkAsAttr() {
        if (source == ResultSource.UNKNOWN) {
            Problems.UNEXPECTED_TYPE_DETECTED.fireProperty(source.name());
        }
        if ((source == ResultSource.ATTR_VALUE || source == ResultSource.VAR) &&
                asAttributeType != AccessType.ACCESS_VARIABLE_BY_NAME
        ) {
            Problems.MISSING_VALUE.fireProperty("$as");
        }
    }

    private MissingDataCommandModuleConfig<ModelNode> getMissingDataModuleConfig(Node endNode) {
        return new MissingDataCommandModuleConfig<>() {
            @Override
            public PredefinedDataPicker<ModelNode> getDataPicker() {
                return ((ExtendedDataPicker<ModelNode>) (name, modelAccess, jsonNode) -> {
                    DataPickerResult<ModelNode> tmpResult = new AttributeNodeDataPicker().pickData(name, modelAccess, jsonNode);
                    if (!tmpResult.isResolved()) {
                        return tmpResult;
                    } else {
                        if (tmpResult.value == null || tmpResult.value.getType() != ModelNode.ModelNodeType.LIST) {
                            return DataPickerResult.unresolvedInvalidAttrValue(tmpResult.source);
                        } else {
                            return tmpResult;
                        }
                    }
                }).asPredefined(VALUE_ATTR);
            }

            @Override
            public Predicate<ModelNode> getIsEmptyPredicate() {
                return test -> test == null || test.isNull() || test.getSize() == 0;
            }

            @Override
            public MissingDataConfig getMissingDataConfig() {
                MissingDataConfig config = new MissingDataConfig();
                config.setStrategy(MissingDataSzenario.NULL, MissingDataStrategy.DELETE_PLACEHOLDER_DELETE_AREA);
                config.setStrategy(MissingDataSzenario.EMPTY, MissingDataStrategy.DELETE_PLACEHOLDER_DELETE_AREA);
                return config;
            }

            @Override
            public Node getAreaEnd() {
                return endNode;
            }

            @Override
            public Node getCommonAncestor() {
                return OdfTools.findOldestParagraphOrTable(getAreaEnd()).get();
            }
        };
    }


    private ForModifierData createModifierData() {
        return new ForModifierData.AbstractForModifiedData(
                new Pair<>(selection.getNode(), endNode),
                listNode.getSize()) {
            @Override
            public Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> getNodeReplacement(int index) {
                ForCommandHandler.this.index = index;
                replaceData = new Pair<>(new ArrayList<>(), new ArrayList<>());
                switch (source) {
                    case ATTR_VALUE -> a2v();
                    case VAR -> v2v();
                    case MODEL -> {
                        switch (asAttributeType) {
                            case ACCESS_VARIABLE_BY_NAME -> m2v();
                            case ACCESS_MODEL_BY_NAME -> m2m();
                            case MISSING_ACCESS -> m2n();
                            default -> Problems.UNEXPECTED_LOOP_CONDITION.fire();
                        }
                    }
                    case UNKNOWN -> Problems.UNEXPECTED_LOOP_CONDITION.fire();
                }
                return replaceData;
            }
        };
    }


    /*
    ${For value:["1", "2", "3"], $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", value:"1"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", value:"2"}
    ${Push key:"index", value:1}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", value:"3"}
    ${Push key:"index", value:2}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */
    private void a2v() {
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_PUSH), placeholder -> {
            JsonNode listElement = getValueJsonAttr.interpretAttributes(attributes).get().get(index);
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, asAttrValue);
            placeholder.addAttribute(VariableCommandHandler.VALUE_ATTR, listElement);
        }));
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_PUSH), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, INDEX);
            placeholder.addIntAttribute(VariableCommandHandler.VALUE_ATTR, index);
        }));
        replaceData.right.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_POP), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, asAttrValue);
        }));
        replaceData.right.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_POP), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, INDEX);
        }));
    }

    /*
    ${For $value:"aList", $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", $value:"aList[0]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", $value:"aList[1]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", $value:"aList[2]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */
    private void v2v() {
        x2v(AccessType.ACCESS_VARIABLE_BY_NAME);
    }


    /*

    ${For @value:"aList", $as:"element"}
      ${$element}
    ${EndFor}

    =>

    ${Push key:"element", @value:"aList[0]"}
    ${Push key:"index", value:0}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", @value:"aList[1]"}
    ${Push key:"index", value:1}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}

    ${Push key:"element", @value:"aList[2]"}
    ${Push key:"index", value:2}
      ${$element}
    ${Pop key:"element"}
    ${Pop key:"index"}
     */
    private void m2v() {
        x2v(AccessType.ACCESS_MODEL_BY_NAME);
    }

    private void x2v(AccessType valueAccessType) {
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_PUSH), placeholder -> {
            String listElement = getValueStringAttr.interpretAttributes(attributes).get() + "[" + index + "]";
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, asAttrValue);
            placeholder.addStringAttribute(valueAccessType.rename(VariableCommandHandler.VALUE_ATTR), listElement);
        }));
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_PUSH), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, INDEX);
            placeholder.addIntAttribute(VariableCommandHandler.VALUE_ATTR, index);
        }));
        replaceData.right.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_POP), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, asAttrValue);
        }));
        replaceData.right.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_POP), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, INDEX);
        }));
    }

    /*

    ${For @value:"aList", @as:"element"}
      ${@element}
    ${EndFor}

    =>

    ${Model value:"current.path.aList[0]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:0}
      ${@element}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[1]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:1}
      ${@element}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[2]", activeModelPathPrefix:"element"}
    ${Push key:"index", value:2}
      ${@element}
    ${Pop key:"index"}
    ${Model value:"current.path"}
     */
    private void m2m() {
        m2x(true);
    }

    /*

    ${For @value:"aList"}
      ${@}
    ${EndFor}

    =>

    ${Model value:"current.path.aList[0]"}
    ${Push key:"index", value:0}
      ${@}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[1]"}
    ${Push key:"index", value:1}
      ${@}
    ${Pop key:"index"}

    ${Model value:"current.path.aList[2]"}
    ${Push key:"index", value:2}
      ${@}
    ${Pop key:"index"}
    ${Model value:"current.path"}
     */
    private void m2n() {
        m2x(false);
    }

    private void m2x(boolean createPrefix) {
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(ModelCommandHandler.KEY), placeholder -> {
            String subPath = modelAccess.interpret(getValueStringAttr.interpretAttributes(attributes).get(), false).left + "[" + index + "]";
            placeholder.addStringAttribute(ModelCommandHandler.ATTR_VALUE, subPath);
            if (createPrefix) {
                placeholder.addStringAttribute(ModelCommandHandler.ATTR_PREFIX, asAttrValue);
            }
        }));
        replaceData.left.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_PUSH), placeholder -> {
            placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, INDEX);
            placeholder.addIntAttribute(VariableCommandHandler.VALUE_ATTR, index);
        }));
        replaceData.right.add(also(new ParametersPlaceholderData.Simple(VariableCommandHandler.KEY_POP), placeholder ->
                placeholder.addStringAttribute(VariableCommandHandler.KEY_ATTR, asAttrValue)));
        if (index == listNode.getSize() - 1) {
            replaceData.right.add(also(new ParametersPlaceholderData.Simple(ModelCommandHandler.KEY), placeholder -> {
                String path = modelAccess.getCurrentModelPath().toString();
                placeholder.addStringAttribute(ModelCommandHandler.ATTR_VALUE, path);
            }));
        }
    }
}

/*

        A2M:

        ${For value:["1", "2", "3"], @as:"element"}
          ${$element}
        ${EndFor}

        => Verboten

        -----------------

        A2N:

        ${For value:["1", "2", "3"]}
          ${$element}
        ${EndFor}

        => Verboten

        -----------------

        V2M:

        ${For $value:"aList", @as:"element"}
          ${$element}
        ${EndFor}

        => Verboten

        -----------------

        V2N:

        ${For $value:"aList"}
          ${$}
        ${EndFor}

        => Verboten
 */