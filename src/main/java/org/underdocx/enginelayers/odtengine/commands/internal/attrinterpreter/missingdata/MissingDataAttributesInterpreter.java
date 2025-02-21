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

package org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.missingdata;

import org.underdocx.common.tools.Convenience;
import org.underdocx.enginelayers.odtengine.commands.internal.attrinterpreter.AbstractAttributeInterpreter;

import java.util.Optional;

public class MissingDataAttributesInterpreter extends AbstractAttributeInterpreter<MissingDataConfig, MissingDataConfig> {
    @Override
    protected MissingDataConfig interpretAttributes() {
        return Convenience.also(new MissingDataConfig(configuration), result -> {
            getStringAttribute("fallback").ifPresent(f -> result.fallback = f);
            getStrategy(MissingDataSzenario.ERROR).ifPresent(f -> result.setStrategy(MissingDataSzenario.ERROR, f));
            getStrategy(MissingDataSzenario.EMPTY).ifPresent(f -> result.setStrategy(MissingDataSzenario.EMPTY, f));
            getStrategy(MissingDataSzenario.NULL).ifPresent(f -> result.setStrategy(MissingDataSzenario.NULL, f));
        });
    }

    private Optional<MissingDataStrategy> getStrategy(MissingDataSzenario szenario) {
        Optional<String> attrValue = getStringAttribute(szenario.getValue());
        if (attrValue.isPresent())
            return Optional.ofNullable(MissingDataStrategy.getByString(attrValue.get()));
        return Optional.empty();
    }
}
