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

package org.underdocx.enginelayers.modelengine.datapath.parser;

import org.underdocx.common.codec.Codec;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Wrapper;
import org.underdocx.enginelayers.modelengine.datapath.DataPath;
import org.underdocx.enginelayers.modelengine.datapath.elements.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class DataPathCodec implements Codec<DataPath> {

    @Override
    public DataPath parse(String string) throws DataPathParseException {
        return new DataPath(ModelPathParser.parse(string));
    }

    @Override
    public String getTextContent(DataPath data) {
        return Convenience.buildString(builder -> {
            Wrapper<String> delim = new Wrapper<>("");
            data.getElements().forEach(element -> {
                builder.append(delim);
                builder.append(element.toString());
                delim.value = ".";
            });
        });
    }

    private static class ModelPathParser {

        private enum State {
            NORMAL,
            INDEX_STARTED,
            INDEX_ENDED
        }

        private final String toParse;
        private String lastString = null;
        private final List<DataPathElement> result = new ArrayList<>();
        private State state = State.NORMAL;


        private ModelPathParser(String toParse) {
            this.toParse = toParse;
        }

        public static List<DataPathElement> parse(String toParse) throws DataPathParseException {
            return new ModelPathParser(toParse).parse();
        }

        private List<DataPathElement> parse() throws DataPathParseException {
            if (!toParse.isBlank()) {
                StringTokenizer t = new StringTokenizer(toParse, "<^[].", true);
                while (t.hasMoreTokens()) {
                    String token = t.nextToken();
                    switch (token) {
                        case "<" -> parseBack();
                        case "^" -> parseRoot();
                        case "[" -> parseBeginIndex();
                        case "]" -> parseEndIndex();
                        case "." -> parseSeparator();
                        default -> parseToken(token);
                    }
                }
                storeProperty();
            }
            return result;
        }

        private void storeProperty() {
            if (lastString != null) {
                result.add(new PropertyDataPathElement(lastString));
                lastString = null;
            }
        }

        public void storeIndex() throws DataPathParseException {
            if (lastString != null) {
                try {
                    result.add(new IndexDataPathElement(Integer.parseInt(lastString)));
                    lastString = null;
                } catch (Exception e) {
                    throw new DataPathParseException("Expected index number", e);
                }
            }
        }

        private void parseBack() throws DataPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    result.add(new BackDataPathElement());
                    state = State.NORMAL;
                }
                default -> throw new DataPathParseException("Unexpected back character");
            }
        }

        private void parseRoot() throws DataPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    result.add(new RootDataPathElement());
                    state = State.NORMAL;
                }
                default -> throw new DataPathParseException("Unexpected root character");
            }
        }

        private void parseBeginIndex() throws DataPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    state = State.INDEX_STARTED;
                }
                default -> throw new DataPathParseException("Unexpected index start");
            }
        }

        private void parseEndIndex() throws DataPathParseException {
            if (Objects.requireNonNull(state) == State.INDEX_STARTED) {
                storeIndex();
                state = State.INDEX_ENDED;
            } else {
                throw new DataPathParseException("Unexpected index end");
            }
        }

        private void parseSeparator() throws DataPathParseException {
            switch (state) {
                case NORMAL, INDEX_ENDED -> {
                    storeProperty();
                    state = State.NORMAL;
                }
                default -> throw new DataPathParseException("Unexpected separator");
            }
        }

        private void parseToken(String token) {
            lastString = token;
        }
    }
}
