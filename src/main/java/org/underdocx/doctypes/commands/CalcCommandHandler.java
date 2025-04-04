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

import org.underdocx.doctypes.DocContainer;
import org.underdocx.doctypes.commands.internal.AbstractTextualCommandHandler;
import org.underdocx.doctypes.modifiers.ModifiersProvider;
import org.underdocx.doctypes.modifiers.deleteplaceholder.DeletePlaceholderModifierData;
import org.underdocx.doctypes.tools.datapicker.AttributeNodeDataPicker;
import org.underdocx.doctypes.tools.datapicker.OneOfDataPicker;
import org.underdocx.doctypes.tools.datapicker.PredefinedDataPicker;
import org.underdocx.doctypes.tools.datapicker.StringConvertDataPicker;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.modelengine.data.DataNode;
import org.underdocx.enginelayers.modelengine.data.simple.LeafDataNode;
import org.underdocx.environment.err.Problems;

public class CalcCommandHandler<C extends DocContainer<D>, D> extends AbstractTextualCommandHandler<C, D> {
    private static final PredefinedDataPicker<DataNode<?>> value1DataPicker = new AttributeNodeDataPicker().asPredefined("a");
    private static final PredefinedDataPicker<DataNode<?>> value2DataPicker = new AttributeNodeDataPicker().asPredefined("b");
    private static final PredefinedDataPicker<String> keyDataPicker = new StringConvertDataPicker().asPredefined("key");
    private static final PredefinedDataPicker<String> operantDataPicker = new OneOfDataPicker<>("+", "-", "/", "%", "*").asPredefined("operator");


    public CalcCommandHandler(ModifiersProvider<C, D> modifiersProvider) {
        super("Calc", modifiersProvider);
    }

    private double toDouble(DataNode<?> data) {
        Object value = data.getValue();
        if (value instanceof Double d) {
            return d;
        } else if (value instanceof Integer i) {
            return Double.valueOf(i);
        } else if (value instanceof Long l) {
            return Double.valueOf(l);
        } else return Problems.INVALID_VALUE.fireValue(String.valueOf(value));
    }

    private long toLong(DataNode<?> data) {
        Object value = data.getValue();
        if (value instanceof Integer i) {
            return Long.valueOf(i);
        } else if (value instanceof Long l) {
            return l;
        } else return Problems.INVALID_VALUE.fireValue(String.valueOf(value));
    }

    private int toInt(DataNode<?> data) {
        Object value = data.getValue();
        if (value instanceof Integer i) {
            return i;
        } else return Problems.INVALID_VALUE.fireValue(String.valueOf(value));
    }

    private int calcInt(int a, int b, String op) {
        return switch (op) {
            case "*" -> a * b;
            case "/" -> a / b;
            case "%" -> a % b;
            case "-" -> a - b;
            default -> a + b;
        };
    }

    private double calcDouble(double a, double b, String op) {
        return switch (op) {
            case "*" -> a * b;
            case "/" -> a / b;
            case "%" -> a % b;
            case "-" -> a - b;
            default -> a + b;
        };
    }

    private long calcLong(long a, long b, String op) {
        return switch (op) {
            case "*" -> a * b;
            case "/" -> a / b;
            case "%" -> a % b;
            case "-" -> a - b;
            default -> a + b;
        };
    }


    private LeafDataNode<?> calc(DataNode<?> a, DataNode<?> b, String op) {
        LeafDataNode<?> result;
        if (a.getValue() instanceof Double || b.getValue() instanceof Double) {
            result = new LeafDataNode<>(calcDouble(toDouble(a), toDouble(b), op));
        } else if (a.getValue() instanceof Long || b.getValue() instanceof Long) {
            result = new LeafDataNode<>(calcLong(toLong(a), toLong(b), op));
        } else {
            result = new LeafDataNode<>(calcInt(toInt(a), toInt(b), op));
        }
        return result;
    }

    @Override
    protected CommandHandlerResult tryExecuteTextualCommand() {
        DataNode<?> aNode = Problems.MISSING_VALUE.get(value1DataPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "a");
        DataNode<?> bNode = Problems.MISSING_VALUE.get(value2DataPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "b");
        String op = operantDataPicker.pickData(dataAccess, placeholderData.getJson()).optional().orElse("+");
        String key = Problems.MISSING_VALUE.get(keyDataPicker.pickData(dataAccess, placeholderData.getJson()).optional(), "key");
        LeafDataNode<?> result = calc(aNode, bNode, op);
        dataAccess.pushVariable(key, result);
        return CommandHandlerResult.FACTORY.convert(modifiers.getDeletePlaceholderModifier().modify(selection.getNode(), DeletePlaceholderModifierData.DEFAULT));
    }
}
