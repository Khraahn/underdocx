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

package org.underdocx.enginelayers.baseengine;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.underdocx.common.enumerator.Enumerator;
import org.underdocx.common.types.Pair;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.internal.EngineAccessImpl;
import org.underdocx.enginelayers.baseengine.internal.PlaceholdersEnumerator;
import org.underdocx.enginelayers.baseengine.internal.SelectionImpl;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problem;
import org.underdocx.environment.err.Problems;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.underdocx.common.tools.Convenience.build;

public class BaseEngine<C extends DocContainer<D>, D> {

    protected C doc;

    protected int step = 0;
    protected boolean rescan = true;
    protected boolean initialized = false;

    protected final ArrayListValuedHashMap<PlaceholdersProvider<C, ?, D>, CommandHandler<C, ?, D>> registry = new ArrayListValuedHashMap<>();
    protected final HashMap<Class<?>, PlaceholdersProvider<C, ?, D>> reverseRegistry = new HashMap<>();
    protected final IdentityHashMap<PlaceholdersProvider.Factory<C, ?, D>, PlaceholdersProvider<C, ?, D>> factoryProviderMap = new IdentityHashMap<>();
    protected final LinkedHashSet<EngineListener<C, D>> listeners = new LinkedHashSet<>();
    protected Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> placeholderEnumerator;

    public BaseEngine() {
    }

    private void prepareRun(C doc) {
        this.doc = doc;
        step = 0;
        rescan = true;
        initialized = false;
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider.Factory<C, X, D> providerFactory, CommandHandler<C, X, D> commandHandler) {
        PlaceholdersProvider<C, ?, D> provider = factoryProviderMap.get(providerFactory);
        if (provider == null) {
            provider = providerFactory.createProvider(doc);
            factoryProviderMap.put(providerFactory, provider);
        }
        return registerCommandHandler((PlaceholdersProvider<C, X, D>) provider, commandHandler);
    }

    public <X> BaseEngine<C, D> registerCommandHandler(PlaceholdersProvider<C, X, D> provider, CommandHandler<C, X, D> commandHandler) {
        registry.put(provider, commandHandler);
        reverseRegistry.put(commandHandler.getClass(), provider);
        return this;
    }

    protected Enumerator<Pair<PlaceholdersProvider<C, ?, D>, Node>> createPlaceholdersEnumerator() {
        return new PlaceholdersEnumerator<>(doc, registry.keySet()).cache();
    }

    protected CommandHandlerResult findAndExecCommandHandler(PlaceholdersProvider<C, ?, D> provider, Selection<C, ?, D> selection) {
        return build(CommandHandlerResult.IGNORED, result -> {
            for (CommandHandler<C, ?, D> commandHandler : registry.get(provider)) {
                try {
                    result.value = commandHandler.tryExecuteCommand((Selection) selection);
                } catch (Exception e) {
                    Problems.COMMAND_HANDLER_FAILED
                            .toProblem()
                            .command(commandHandler)
                            .node(selection.getNode())
                            .handle(e)
                            .fire();
                }
                if (result.value != CommandHandlerResult.IGNORED) {
                    if (UnderdocxEnv.getInstance().isDebug) {
                        UnderdocxEnv.getInstance().logger.trace("Step " + step + ": commandHandler " + commandHandler + " returned type: " + result.value.getResultType().name() + ", (changed) node: " + selection.getNode() + ", nextValidNode: " + result.value.getRestartNode());
                    }
                    break;
                }
            }
        });
    }

    protected <P> Selection<C, P, D> createSelection(PlaceholdersProvider<C, P, D> provider, Node node, EngineAccess<C, D> engineAccess) {
        return new SelectionImpl<>(doc, node, provider, engineAccess);
    }

    protected void stepExecuted(Selection<C, ?, D> selection) {
        if (UnderdocxEnv.getInstance().isDebug) {
            try {
                String fileName = "Debug_" + step + "_" + System.currentTimeMillis() + "." + selection.getDocContainer().getFileExtension();
                String fileDir = System.getProperty("java.io.tmpdir");
                File tmpFile = new File(fileDir + "/" + fileName);
                UnderdocxEnv.getInstance().logger.trace("save debug file: " + tmpFile);
                selection.getDocContainer().save(tmpFile);
            } catch (IOException e) {
                UnderdocxEnv.getInstance().logger.error("Failed to create debug file", e);
            }
        }
    }

    public Optional<Problem> run(C doc) {
        prepareRun(doc);
        long startTime = System.currentTimeMillis();
        Problem detectedError = null;
        try {
            runUncatched();
        } catch (Exception e) {
            UnderdocxEnv.getInstance().logger.error(e);
            detectedError = Problems.UNEXPECTED_EXCEPION_CAUGHT.toProblem().handle(e);
        }
        long endTime = System.currentTimeMillis();
        UnderdocxEnv.getInstance().logger.trace("Engine execution: " + ((double) (endTime - startTime)) / 1000 + " sec");
        if (detectedError != null && UnderdocxEnv.getInstance().appendErrorReport) {
            doc.appendText(
                    "\n---------------------------\n" +
                            "Problem Report: \n" +
                            "---------------------------\n" +
                            detectedError);
        }
        if (detectedError != null) {
            UnderdocxEnv.getInstance().logger.error(detectedError.toString());
        }
        return Optional.ofNullable(detectedError);
    }

    protected void reactOnExecutionResult(CommandHandlerResult executionResult, Selection<C, ?, D> selection) {
        switch (executionResult.getResultType()) {
            case IGNORED ->
                    UnderdocxEnv.getInstance().logger.warn("No Command handler found four " + selection.getNode(), null);
            case EXECUTED_PROCEED -> stepExecuted(selection);
            case EXECUTED_FULL_RESCAN -> {
                stepExecuted(selection);
                registry.keySet().forEach(provider -> provider.restartAt(null, false));
                rescan = true;
            }
            case EXECUTED_RESTART_AT_NODE -> {
                stepExecuted(selection);
                registry.keySet().forEach(provider -> provider.restartAt(executionResult.getRestartNode(), false));
                placeholderEnumerator = createPlaceholdersEnumerator();
            }
            case EXECUTED_END_OF_DOC -> {
                stepExecuted(selection);
                registry.keySet().forEach(provider -> provider.restartAt(null, true));
                placeholderEnumerator = createPlaceholdersEnumerator();
            }
            default -> Problems.UNEXPECTED_TYPE_DETECTED.fireValue(executionResult.getResultType().name());
        }
    }

    protected void runUncatched() {
        while (rescan) {
            placeholderEnumerator = createPlaceholdersEnumerator();
            List<Node> visited = new ArrayList<>();
            rescan = false;
            EngineAccess<C, D> engineAccess = new EngineAccessImpl<>(listeners, () -> rescan = true, () -> placeholderEnumerator, () -> visited, reverseRegistry);
            if (initialized) {
                listeners.forEach(listener -> listener.rescan(doc, engineAccess));
            }
            if (!initialized) {
                registry.values().forEach(handler -> handler.init(doc, engineAccess));
                initialized = true;
            }
            while (!rescan && placeholderEnumerator.hasNext()) {
                step++;
                Pair<PlaceholdersProvider<C, ?, D>, Node> placeholder = placeholderEnumerator.next();
                if (UnderdocxEnv.getInstance().isDebug) {
                    UnderdocxEnv.getInstance().logger.trace("Processing placeholder: " + placeholder.right);
                }
                Selection<C, ?, D> selection = createSelection(placeholder.left, placeholder.right, engineAccess);
                CommandHandlerResult executionResult = findAndExecCommandHandler(placeholder.left, selection);
                reactOnExecutionResult(executionResult, selection);
                visited.add(selection.getNode());
            }

            listeners.forEach(listener -> {
                if (!rescan) {
                    listener.eodReached(doc, engineAccess);
                }
            });
        }
    }
}
