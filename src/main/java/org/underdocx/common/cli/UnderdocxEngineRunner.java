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

package org.underdocx.common.cli;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.AbstractDocContainer;
import org.underdocx.doctypes.EngineAPI;
import org.underdocx.doctypes.odf.odg.OdgEngineProvider;
import org.underdocx.doctypes.odf.odp.OdpEngineProvider;
import org.underdocx.doctypes.odf.ods.OdsEngineProvider;
import org.underdocx.doctypes.odf.odt.OdtEngineProvider;
import org.underdocx.environment.err.Problem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UnderdocxEngineRunner {

    private static final Map<String, EngineProvider<?, ?>> predefinedProviders;

    static {
        predefinedProviders = new HashMap<>();
        predefinedProviders.put("odt", new OdtEngineProvider());
        predefinedProviders.put("odg", new OdgEngineProvider());
        predefinedProviders.put("odp", new OdpEngineProvider());
        predefinedProviders.put("ods", new OdsEngineProvider());
    }

    public static void main(String[] args) {
        int result = new Runner<>(args).run();
        if (result < 0) {
            System.err.println("Sorry, execution failed!");
            System.exit(result);
        } else {
            System.out.println("Execution was successful");
        }
    }

    public static class Runner<C extends AbstractDocContainer<D>, D> {

        private EngineProvider<C, D> engineProvider;
        private EngineAPI<C, D> engine;
        private C doc;
        String engineProviderArg;
        String inputFileArg;
        String outputFileArg;
        String[] dataFilesArgs;

        public static String getArg(String[] args, int index) {
            if (index >= args.length) {
                return null;
            }
            return args[index];
        }

        public static String[] copyOfRange(String[] args, int from) {
            if (from >= args.length) {
                return new String[]{};
            }
            String[] subArray = Arrays.copyOfRange(args, from, args.length);
            return subArray;
        }

        public Runner(String[] args) {
            this(getArg(args, 0), getArg(args, 1), getArg(args, 2), copyOfRange(args, 3));
        }

        public Runner(String engineProviderArg, String inputFileArg, String outputFileArg, String... dataFilesArgs) {
            if (engineProviderArg == null || inputFileArg == null || outputFileArg == null) {
                System.err.println("engineProvider: " + engineProviderArg);
                System.err.println("inputFile: " + inputFileArg);
                System.err.println("outputFile: " + outputFileArg);
                throw new RuntimeException("UnderdocxEngineRunner <EngineProviderClassName|PredefinedType> <DocInputFile> <DocOutputFile> [DataJsonFile]");
            }
            this.engineProviderArg = engineProviderArg;
            this.inputFileArg = inputFileArg;
            this.outputFileArg = outputFileArg;
            this.dataFilesArgs = dataFilesArgs;
        }

        public int run() {
            return Convenience.build(-1, result -> {
                try {

                    engineProvider = createEngineProvider(engineProviderArg);
                    doc = loadDocFile(engineProvider, inputFileArg);
                    engine = engineProvider.createEngine();
                    for (String dataFile : dataFilesArgs) {
                        loadData(dataFile);
                    }
                    Optional<Problem> egnineResult = engine.run(doc);
                    saveData(outputFileArg);
                    if (egnineResult.isPresent()) {
                        String problem = egnineResult.get().toString();
                        throw new RuntimeException(problem);
                    }
                    result.value = 0;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        }

        private void saveData(String file) {
            try (FileOutputStream os = new FileOutputStream(new File(file))) {
                doc.save(os);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Can't save file", e);
            }
        }

        private void loadData(String file) {
            try {
                engine.importData(new FileInputStream(file));
            } catch (IOException e) {
                throw new RuntimeException("Can't load json data file", e);
            }
        }

        private C loadDocFile(EngineProvider<C, D> engineProvider, String file) {
            return Convenience.build(result -> {
                try {
                    result.value = engineProvider.load(new Resource.FileResource(file));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Can't load doc file", e);
                }
            });
        }
    }

    private static <C extends AbstractDocContainer<D>, D> EngineProvider<C, D> createEngineProvider(String arg) {
        return Convenience.build(result -> {
            if (predefinedProviders.containsKey(arg)) {
                result.value = (EngineProvider<C, D>) predefinedProviders.get(arg);
            } else {
                try {
                    Class<?> clazz = Class.forName(arg);
                    if (EngineProvider.class.isAssignableFrom(clazz)) {
                        result.value = (EngineProvider<C, D>) clazz.getDeclaredConstructor().newInstance();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("can't intialize Engine", e);
                }
            }
        });
    }

}
