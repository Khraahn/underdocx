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
import org.underdocx.common.types.Pair;
import org.underdocx.common.types.Regex;
import org.underdocx.doctypes.AbstractEngine;
import org.underdocx.doctypes.EngineAPI;
import org.underdocx.doctypes.odf.commands.AliasCommandHandler;
import org.underdocx.doctypes.odf.commands.MultiCommandHandler;
import org.underdocx.doctypes.odf.commands.SimpleDollarImageReplaceCommand;
import org.underdocx.doctypes.odf.commands.SimpleReplaceFunctionCommand;
import org.underdocx.doctypes.odf.commands.image.ImagePlaceholdersProvider;
import org.underdocx.doctypes.odf.modifiers.OdfModifiersProvider;
import org.underdocx.doctypes.odf.placeholdersprovider.dollar.OdfSimpleDollarPlaceholderFactory;
import org.underdocx.doctypes.odf.placeholdersprovider.dollar.image.SimpleDollarImagePlaceholdersProvider;
import org.underdocx.doctypes.odf.tools.placeholder.OdfParameterizedPlaceholderFactory;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.ModelEngine;
import org.underdocx.enginelayers.parameterengine.ParametersPlaceholderData;
import org.underdocx.environment.err.Problems;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class AbstractOdfEngine<C extends AbstractOdfContainer<D>, D extends OdfDocument> extends AbstractEngine<C, D> implements EngineAPI {

    protected final OdfModifiersProvider modifiers = new OdfModifiersProvider();

    public final SimpleDollarImagePlaceholdersProvider.SimpleDollarImagePlaceholdersProviderFactory<C, D> simpleDollarImage
            = new SimpleDollarImagePlaceholdersProvider.SimpleDollarImagePlaceholdersProviderFactory<>();
    public final OdfSimpleDollarPlaceholderFactory<C, D> simpleDollar
            = new OdfSimpleDollarPlaceholderFactory<>();
    public final OdfParameterizedPlaceholderFactory<C, D> parameters
            = new OdfParameterizedPlaceholderFactory<>();
    public final ImagePlaceholdersProvider.ImagePlaceholdersProviderFactory<C, D> imagePlaceholdersProvider
            = new ImagePlaceholdersProvider.ImagePlaceholdersProviderFactory();

    protected final MultiCommandHandler<C, D> multiCommandHandler = new MultiCommandHandler<>(modifiers);
    protected final AliasCommandHandler<C, D> aliasCommandHandler = new AliasCommandHandler<>(modifiers);

    abstract protected ModelEngine<C, D> getEngine();

    public void registerStringReplacement(String key, String replacement) {
        multiCommandHandler.registerStringReplacement(key, replacement);
    }

    public void registerAlias(String key, String placeholder, Pair<String, String>... attrReplacements) {
        aliasCommandHandler.registerAlias(key, placeholder, attrReplacements);
    }

    public void registerAlias(String key, ParametersPlaceholderData placeholder, Pair<String, String>... attrReplacements) {
        aliasCommandHandler.registerAlias(key, placeholder, attrReplacements);
    }

    public void registerAlias(AliasCommandHandler.AliasData aliasData) {
        aliasCommandHandler.registerAlias(aliasData);
    }


    public void registerParametersCommandHandler(MCommandHandler<C, ParametersPlaceholderData, D> commandHandler) {
        this.getEngine().registerCommandHandler(parameters, commandHandler);
    }

    public void registerSimpleDollarReplaceFunction(Function<String, Optional<String>> replaceFunction) {
        registerCommandHandler(simpleDollar, new SimpleReplaceFunctionCommand<C, D>(replaceFunction));
    }


    public void registerSimpleDollarReplacement(String variableName, String replacement) {
        this.registerSimpleDollarReplacement(Pattern.compile(Pattern.quote(variableName)), replacement);
    }

    public void registerSimpleDollarReplacement(Pattern pattern, String replacement) {
        this.registerSimpleDollarReplaceFunction(variableName ->
                new Regex(pattern).matches(variableName) ? Optional.of(replacement) : Optional.empty()
        );
    }

    public void registerSimpleDollarImageReplaceFunction(Function<String, Optional<URI>> replaceFunction, boolean keepWidth) {
        registerCommandHandler(simpleDollarImage, new SimpleDollarImageReplaceCommand<>(replaceFunction, keepWidth));
    }

    public void registerSimpleDollarImageReplacement(String variableName, String imageURL, boolean keepWidth) {
        this.registerSimpleDollarImageReplacement(Pattern.compile(Pattern.quote(variableName)), imageURL, keepWidth);
    }

    public void registerSimpleDollarImageReplacement(Pattern pattern, String imageURI, boolean keepWidth) {
        this.registerSimpleDollarImageReplaceFunction(variableName ->
                {
                    try {
                        return new Regex(pattern).matches(variableName) ? Optional.of(new URI(imageURI)) : Optional.empty();
                    } catch (Exception e) {
                        return Problems.INVALID_VALUE.toProblem().value(imageURI).exception(e).fire();
                    }
                }, keepWidth
        );
    }


}
