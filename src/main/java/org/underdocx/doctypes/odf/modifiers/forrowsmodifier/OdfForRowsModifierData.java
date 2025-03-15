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

package org.underdocx.doctypes.odf.modifiers.forrowsmodifier;

import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Range;
import org.underdocx.doctypes.odf.modifiers.formodifier.ForModifierData;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.w3c.dom.Node;

import java.util.List;

public interface OdfForRowsModifierData extends ForModifierData {
    Range getRepeatRows();

    int getRowGroupSize();

    boolean isInTable();

    String getTableName();

    class DefaultOdfForRowsModifierData implements OdfForRowsModifierData {

        private final Range rows;
        private final ForModifierData wrapped;
        private final int rowGroupSize;
        private final boolean isInTable;
        private final String tableName;

        public DefaultOdfForRowsModifierData(ForModifierData wrapped, Range repeatRows, int rowGroupSize, boolean isInTable, String tableName) {
            this.wrapped = wrapped;
            this.rows = repeatRows;
            this.rowGroupSize = rowGroupSize;
            this.isInTable = isInTable;
            this.tableName = tableName;
        }

        @Override
        public Pair<Node, Node> getAreaPlaceholderNodes() {
            return wrapped.getAreaPlaceholderNodes();
        }

        @Override
        public Pair<List<ParametersPlaceholderData>, List<ParametersPlaceholderData>> getNodeReplacement(int index) {
            return wrapped.getNodeReplacement(index);
        }

        @Override
        public int getRepeats() {
            return wrapped.getRepeats();
        }

        @Override
        public Range getRepeatRows() {
            return rows;
        }

        @Override
        public int getRowGroupSize() {
            return rowGroupSize;
        }

        @Override
        public boolean isInTable() {
            return isInTable;
        }

        @Override
        public String getTableName() {
            return tableName;
        }
    }
}
