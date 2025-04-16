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

package org.underdocx.doctypes.odf;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.underdocx.doctypes.AbstractEngine;
import org.underdocx.doctypes.EngineAPI;
import org.underdocx.doctypes.commands.AliasCommandHandler;
import org.underdocx.doctypes.commands.MultiCommandHandler;
import org.underdocx.doctypes.odf.commands.image.ImageProvider;
import org.underdocx.doctypes.odf.modifiers.OdfModifiersProvider;
import org.underdocx.doctypes.tools.placeholder.GenericTextualPlaceholderFactory;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;

public abstract class AbstractOdfEngine<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractEngine<C, D> implements EngineAPI<C, D> {

    protected final OdfModifiersProvider<C, D> modifiers = new OdfModifiersProvider<>();


    protected GenericTextualPlaceholderFactory<C, ParametersPlaceholderData, D> parameters;
    protected final ImageProvider.NewImagePlaceholdersProviderFactory<C, D> imagePlaceholdersProvider
            = new ImageProvider.NewImagePlaceholdersProviderFactory<>();

    protected final MultiCommandHandler<C, D> multiCommandHandler = new MultiCommandHandler<>(modifiers);
    protected final AliasCommandHandler<C, D> aliasCommandHandler = new AliasCommandHandler<>(modifiers);

    abstract protected ModelEngine<C, D> getEngine();


    public AbstractOdfEngine(GenericTextualPlaceholderFactory<C, ParametersPlaceholderData, D> parameters) {
        this.parameters = parameters;
    }

    public void registerStringReplacement(String key, String replacement) {
        multiCommandHandler.registerStringReplacement(key, replacement);
    }


    public void registerAlias(AliasCommandHandler.AliasData aliasData) {
        aliasCommandHandler.registerAlias(aliasData);
    }


    public void registerParametersCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> commandHandler) {
        this.getEngine().registerCommandHandler(parameters, commandHandler);
    }

    protected void registerCommonOdfHandlers() {
        
    }
}
