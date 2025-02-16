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

package org.underdocx.enginelayers.defaultodtengine.commands.forcommand;

import com.fasterxml.jackson.databind.JsonNode;
import org.underdocx.common.doc.DocContainer;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.defaultodtengine.commands.ModelCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.VariableCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.AreaTools;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.PredefinedAttributesInterpreter;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessType;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.accesstype.AccessTypeJsonNameInterpreter;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.single.AttributeInterpreterFactory;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.DataPickerResult;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.ListDataPicker;
import org.underdocx.enginelayers.defaultodtengine.commands.internal.datapicker.PredefinedDataPicker;
import org.underdocx.enginelayers.defaultodtengine.modifiers.formodifier.ForModifierData;
import org.underdocx.enginelayers.modelengine.model.ModelNode;
import org.underdocx.enginelayers.modelengine.model.simple.ListModelNode;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.underdocx.common.tools.Convenience.also;

public abstract class AbstractForCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {

    public static final String BEGIN_KEY = "For";
    public static final String END_KEY = "EndFor";
    public static final Regex KEYS = new Regex(BEGIN_KEY + "|" + END_KEY);

    public static final String VALUE_ATTR = "value";
    public static final String AS_ATTR = "as";
    public static final String TABLEROW_ATTR = "tablerow";
    public static final String LISTITEM_ATTR = "listitem";

    protected static final PredefinedAttributesInterpreter<Optional<String>> getValueStringAttr = AttributeInterpreterFactory.createStringAttributeInterpreter(VALUE_ATTR);
    protected static final PredefinedAttributesInterpreter<Optional<JsonNode>> getValueJsonAttr = AttributeInterpreterFactory.createJsonAttributeInterpreter(VALUE_ATTR);
    protected static final PredefinedAttributesInterpreter<Optional<String>> getAsStrAttr = AttributeInterpreterFactory.createStringAttributeInterpreter(AS_ATTR);

    protected static final PredefinedDataPicker<ModelNode> listPicker = new ListDataPicker().asPredefined(VALUE_ATTR);

    public static final String INDEX = "index";

    // ---------------------------------------

    protected Node endNode;

    protected AccessType asAttributeType;
    protected String asAttrValue;

    protected ModelNode listNode;
    protected DataPickerResult.ResultSource source;

    protected JsonNode attributes;
    protected int index;
    protected Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> replaceData;

    // ---------------------------------------

    protected AbstractForCommandHandler() {
        super(KEYS);
    }


    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        attributes = placeholderData.getJson();
        if (selection.getPlaceholderData().getKey().equals(END_KEY) || !isResponsible(attributes)) {
            return CommandHandlerResult.IGNORED;
        }
        Optional<Pair<SelectedNode<ParametersPlaceholderData>, SelectedNode<ParametersPlaceholderData>>> area =
                AreaTools.findArea(selection.getEngineAccess().lookAhead(null), selection, END_KEY);
        endNode = Problems.INVALID_PLACEHOLDER_STRUCTURE.get(area, "EndFor").right.getNode();
        DataPickerResult<ModelNode> listNodeLookup = listPicker.pickData(modelAccess, attributes);
        source = listNodeLookup.source;
        if (listNodeLookup.isResolvedNotNull()) {
            listNode = listNodeLookup.value;
        } else {
            listNode = new ListModelNode();
        }

        asAttributeType = AccessTypeJsonNameInterpreter.DEFAULT.interpretAttributes(placeholderData.getJson(), AS_ATTR);
        checkAsAttr();
        asAttrValue = getAsStrAttr.interpretAttributes(placeholderData.getJson()).orElse(null);
        callModifier(createModifierData());

        return CommandHandlerResult.EXECUTED_FULL_RESCAN;
    }

    protected abstract void callModifier(ForModifierData forModifierData);

    protected abstract boolean isResponsible(JsonNode attributes);

    private void checkAsAttr() {
        if (source == DataPickerResult.ResultSource.UNKNOWN) {
            Problems.UNEXPECTED_TYPE_DETECTED.fireProperty(source.name());
        }
        if ((source == DataPickerResult.ResultSource.ATTR_VALUE || source == DataPickerResult.ResultSource.VAR) &&
                asAttributeType != AccessType.ACCESS_VARIABLE_BY_NAME
        ) {
            Problems.MISSING_VALUE.fireProperty("$as");
        }
    }


    private ForModifierData createModifierData() {
        return new ForModifierData.AbstractForModifiedData(
                new Pair<>(selection.getNode(), endNode),
                listNode.getSize()) {
            @Override
            public Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> getNodeReplacement(int index) {
                AbstractForCommandHandler.this.index = index;
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

