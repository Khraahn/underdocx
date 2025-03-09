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

package org.underdocx.doctypes.odf.tools;

import org.underdocx.common.codec.IntCodec;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Range;
import org.underdocx.common.types.Tripple;
import org.underdocx.doctypes.odf.constants.OdfAttribute;
import org.underdocx.doctypes.odf.constants.OdfElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OdfTables {

    private final static String LETTERS = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public static Optional<Node> findTable(Node tree, String name) {
        return Convenience.buildOptional(result -> {
            Predicate<Node> predicate = (node) -> (OdfElement.TABLE.is(node));
            List<Node> tables = Nodes.findDescendantNodes(tree, predicate, true);
            for (Node table : tables) {
                if (name.equals(OdfAttribute.TANLE_NAME.getAttributeNS((Element) table))) {
                    result.value = table;
                    break;
                }
            }
        });
    }

    public static Optional<String> getCellStyle(Pair<Node, Node> styleNodes) {
        return Convenience.buildOptional(result -> {
            if (styleNodes.left != null) {
                result.value = OdfAttribute.TABLE_DEFAULT_CELL_STYLE_NAME.getAttributeNS(styleNodes.left);
            }
            if (styleNodes.right != null) {
                if (OdfAttribute.TABLE_STYLE_NAME.isIn(styleNodes.right)) {
                    result.value = OdfAttribute.TABLE_STYLE_NAME.getAttributeNS(styleNodes.right);
                }
            }
        });
    }

    public static Pair<Node, Node> getStyleNodes(Node table, int x, int y) {
        return Convenience.build(new Pair<>(), result -> {
            getDefaultColumnStyleNode(table, x).ifPresent(defaultStyleNode ->
                    result.value.left = defaultStyleNode);
            getRow(table, y).ifPresent(row -> {
                getCellStyleNode(row, x).ifPresent(styleNode ->
                        result.value.right = styleNode);
            });
        });
    }

    private static Optional<Node> getCellStyleNode(Node row, int x) {
        return Convenience.buildOptional(result ->
                getTableElement(row, x, OdfElement.TABLE_CELL::is, OdfAttribute.TABLE_NUMBER_COLUMNS_REPEATED).ifPresent(node ->
                        result.value = node));
    }

    private static Optional<Node> getRow(Node table, int y) {
        return Convenience.buildOptional(result ->
                getTableElement(table, y, OdfElement.TABLE_ROW::is, OdfAttribute.TABLE_NUMBER_ROWS_REPEATED).ifPresent(node ->
                        result.value = node));
    }

    private static Optional<Node> getDefaultColumnStyleNode(Node table, int x) {
        return Convenience.buildOptional(result ->
                getTableElement(table, x, OdfElement.TABLE_COLUMN::is, OdfAttribute.TABLE_NUMBER_COLUMNS_REPEATED).ifPresent(node ->
                        result.value = node));
    }

    private static Optional<Node> getTableElement(Node parent, int index, Predicate<Node> filter, OdfAttribute repeatAttr) {
        return Convenience.buildOptional(result -> {
            int count = 0;
            Enumerator<Node> children = Nodes.getChildren(parent, filter);
            while (children.hasNext()) {
                Node child = children.next();
                String repeatStr = repeatAttr.getAttributeNS(((Element) child));
                int repeat = IntCodec.DEFAULT.tryParse(repeatStr).orElse(1);
                Range range = new Range(count, count + repeat);
                if (range.contains(index)) {
                    result.value = child;
                    break;
                }
                count = count + repeat;
            }
        });
    }

    public static Optional<Tripple<String, Integer, Integer>> parseCellReference(String tableCellReference) {
        return Convenience.buildOptional(result -> {
            Tripple<String, Integer, Integer> tripple = new Tripple<>();
            if (tableCellReference.contains(".")) {
                String[] split = tableCellReference.split(Pattern.quote("."));
                if (split.length == 2) {
                    tripple.left = split[0];
                    String letterNumber = split[1];
                    parseCellCoordinates(letterNumber).ifPresent(pair -> {
                        tripple.middle = pair.left;
                        tripple.right = pair.right;
                        result.value = tripple;
                    });
                }
            } else {
                parseCellCoordinates(tableCellReference).ifPresent(pair -> {
                    tripple.middle = pair.left;
                    tripple.right = pair.right;
                    result.value = tripple;
                });
            }
        });
    }

    public static Optional<Pair<Integer, Integer>> parseCellCoordinates(String cellReference) {
        return Convenience.buildOptional(result -> {
            Pair<Integer, Integer> pair = new Pair<>();
            if (cellReference.length() >= 2) {
                char letter = cellReference.charAt(0);
                pair.left = LETTERS.indexOf(letter);
                if (pair.left >= 0) {
                    String numbers = cellReference.substring(1);
                    pair.right = IntCodec.DEFAULT.tryParse(numbers).orElse(-1);
                    if (pair.right >= 0) {
                        result.value = pair;
                    }
                }
            }
        });
    }
}
