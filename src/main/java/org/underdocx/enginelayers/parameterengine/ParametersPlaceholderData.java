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

package org.underdocx.enginelayers.parameterengine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public interface ParametersPlaceholderData {

    String getKey();

    void setKey(String key);

    JsonNode getJson();

    void setJson(JsonNode json);

    void clearAttributes();

    void addStringAttribute(String property, String value);

    void addAttribute(String property, JsonNode attributeValue);

    void addIntAttribute(String property, int value);

    class Simple implements ParametersPlaceholderData {
        private String key;
        private JsonNode json;

        public Simple(String key, JsonNode json) {
            this.key = key;
            this.json = json;
        }

        public Simple(String key) {
            this.key = key;
            this.json = JsonNodeFactory.instance.objectNode();
        }

        @Override
        public JsonNode getJson() {
            return json;
        }

        @Override
        public void setJson(JsonNode json) {
            this.json = json;
        }

        @Override
        public void clearAttributes() {
            this.json = JsonNodeFactory.instance.objectNode();
        }

        @Override
        public void addStringAttribute(String property, String value) {
            JsonNode json = ((ObjectNode) getJson());
            if (json == null) {
                json = JsonNodeFactory.instance.objectNode();
            }
            ((ObjectNode) json).put(property, value);
        }

        @Override
        public void addIntAttribute(String property, int value) {
            JsonNode json = ((ObjectNode) getJson());
            if (json == null) {
                json = JsonNodeFactory.instance.objectNode();
            }
            ((ObjectNode) json).put(property, value);
        }

        @Override
        public void addAttribute(String property, JsonNode attributeValue) {
            ((ObjectNode) json).put(property, attributeValue);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return "Simple{" +
                    "key='" + key + '\'' +
                    ", json=" + json +
                    '}';
        }
    }
}
