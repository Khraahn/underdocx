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
import org.underdocx.common.codec.Codec;
import org.underdocx.common.codec.JsonCodec;
import org.underdocx.environment.err.Problems;

public class GenericParametersPlaceholderCodec implements Codec<ParametersPlaceholderData> {

    public final static JsonCodec JSON_CODEC = new JsonCodec(false, true, true);
    private final String prefix;
    private final String suffix;
    private final int pLength;
    private final int sLength;
    private final int minLength;


    public GenericParametersPlaceholderCodec(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.pLength = prefix.length();
        this.sLength = suffix.length();
        this.minLength = pLength + sLength;
    }

    @Override
    public ParametersPlaceholderData parse(String string) throws Exception {
        if (string.startsWith(prefix) && string.endsWith(suffix) && string.length() > minLength) {
            String trimmedContent = string.substring(pLength, string.length() - (sLength)).trim();
            int spaceIndex = trimmedContent.indexOf(' ');
            if (spaceIndex >= 0) {
                final String key = trimmedContent.substring(0, spaceIndex).trim();
                final String jsonString = "{" + trimmedContent.substring(spaceIndex).trim() + "}";
                JsonNode json = JSON_CODEC.parse(jsonString);
                return new ParametersPlaceholderData.Simple(key, json);
            } else {
                return new ParametersPlaceholderData.Simple(trimmedContent, null);
            }
        } else {
            return Problems.PLACEHOLDER_PARSE_ERROR.fireValue(string);
        }
    }

    @Override
    public String getTextContent(ParametersPlaceholderData data) {
        if (data.getJson() != null) {
            String jsonStr = JSON_CODEC.getTextContent(data.getJson());
            String paramsJson = jsonStr.substring(1, jsonStr.length() - 1);
            return prefix + data.getKey() + " " + paramsJson + suffix;
        } else {
            return prefix + data.getKey() + suffix;
        }
    }
}
