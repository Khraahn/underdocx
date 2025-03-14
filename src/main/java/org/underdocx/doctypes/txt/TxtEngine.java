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

package org.underdocx.doctypes.txt;

import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.AbstractEngine;
import org.underdocx.doctypes.odf.commands.AliasCommandHandler;
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;


public class TxtEngine extends AbstractEngine<TextContainer, TextXML> {

    private final ModelEngine<TextContainer, TextXML> engine;

    public final TxtParameterizedPlaceholderFactory parameters = new TxtParameterizedPlaceholderFactory();

    protected void registerDefaultCommandHandlers() {

    }

    public TxtEngine(OdtContainer doc) {
        this.engine = new ModelEngine<>(doc);
        registerDefaultCommandHandlers();
    }

    @Override
    protected ModelEngine<TextContainer, TextXML> getEngine() {
        return engine;
    }

    @Override
    public void registerStringReplacement(String key, String replacement) {

    }

    @Override
    public void registerAlias(String key, String placeholder, Pair<String, String>... attrReplacements) {

    }

    @Override
    public void registerAlias(String key, ParametersPlaceholderData placeholder, Pair<String, String>... attrReplacements) {

    }

    @Override
    public void registerAlias(AliasCommandHandler.AliasData aliasData) {

    }

    @Override
    public void registerParametersCommandHandler(MCommandHandler<TextContainer, ParametersPlaceholderData, TextXML> commandHandler) {

    }
}
