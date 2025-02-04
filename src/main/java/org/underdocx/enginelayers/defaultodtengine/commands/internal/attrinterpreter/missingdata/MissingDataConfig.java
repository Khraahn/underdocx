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

package org.underdocx.enginelayers.defaultodtengine.commands.internal.attrinterpreter.missingdata;

import java.util.HashMap;

public class MissingDataConfig {
    private HashMap<MissingDataSzenario, MissingDataStrategy> map = new HashMap<>();
    public String fallback;

    public MissingDataConfig() {
        map.put(MissingDataSzenario.NULL, MissingDataStrategy.DELETE_PLACEHOLDER_DELETE_EMPTY_PARAGRAPH);
        map.put(MissingDataSzenario.EMPTY, MissingDataStrategy.EMPTY);
        map.put(MissingDataSzenario.ERROR, MissingDataStrategy.FAIL);
    }

    public MissingDataConfig(MissingDataConfig config) {
        map.putAll(config.map);
        fallback = config.fallback;
    }

    public MissingDataConfig(MissingDataStrategy onNull, MissingDataStrategy onEmpty, MissingDataStrategy onError) {
        map.put(MissingDataSzenario.NULL, onNull);
        map.put(MissingDataSzenario.EMPTY, onEmpty);
        map.put(MissingDataSzenario.ERROR, onError);
    }

    public void setStrategy(MissingDataSzenario szenario, MissingDataStrategy strategy) {
        map.put(szenario, strategy);
    }

    public MissingDataStrategy getStrategy(MissingDataSzenario szenario) {
        return map.get(szenario);
    }


}
