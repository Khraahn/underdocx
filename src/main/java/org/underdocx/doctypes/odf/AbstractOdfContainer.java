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

import org.apache.commons.io.IOUtils;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.underdocx.common.doc.AbstractDocContainer;
import org.underdocx.common.types.Regex;
import org.underdocx.common.types.Resource;
import org.underdocx.environment.UnderdocxEnv;
import org.underdocx.environment.err.Problems;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URI;
import java.util.Scanner;

public abstract class AbstractOdfContainer<T extends OdfDocument> extends AbstractDocContainer<T> {
    private static final Object lock = new Object();

    public AbstractOdfContainer() {
        super();
    }

    public AbstractOdfContainer(Resource resource) throws IOException {
        super(resource);
    }

    public AbstractOdfContainer(InputStream is) throws IOException {
        super(is);
    }

    public AbstractOdfContainer(URI uri) throws IOException {
        super(uri);
    }

    public AbstractOdfContainer(byte[] data) throws IOException {
        super(data);
    }

    public AbstractOdfContainer(File file) throws IOException {
        super(file);
    }

    public AbstractOdfContainer(T doc) {
        super(doc);
    }

    public OdfContentDom getContentDom() {
        try {
            return getDocument().getContentDom();
        } catch (SAXException e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        } catch (IOException e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    public OdfStylesDom getStylesDom() {
        try {
            return getDocument().getStylesDom();
        } catch (SAXException e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        } catch (IOException e) {
            return Problems.ODF_FRAMEWORK_OPERARTION_EXCEPTION.fire(e);
        }
    }

    public void writePDF(OutputStream os) throws IOException {
        try {
            String libreOffice = Problems.LIBREOFFICE_ENV_NOT_SET.notNull(UnderdocxEnv.getInstance().libreOfficeExecutable, "LIBREOFFICE");
            File tmpOdtFile = this.createTmpFile(1000L * 60L * 5L);
            save(tmpOdtFile);
            File tmpDir = tmpOdtFile.getParentFile();
            String tmpOdtFileName = tmpOdtFile.getName();
            ProcessBuilder processBuilder = new ProcessBuilder(libreOffice, "--headless", "--convert-to", "pdf", tmpOdtFileName);
            processBuilder.directory(tmpDir);
            processBuilder.redirectErrorStream(true);
            if (UnderdocxEnv.getInstance().libreOfficeHomePath != null && !UnderdocxEnv.getInstance().libreOfficeHomePath.isBlank()) {
                processBuilder.environment().put("HOME", UnderdocxEnv.getInstance().libreOfficeHomePath);
            }
            synchronized (lock) {
                Process process = processBuilder.start();
                Scanner scanner = new Scanner(process.getInputStream());
                StringBuilder output = new StringBuilder();
                while (scanner.hasNext()) {
                    output.append(scanner.nextLine()).append("\n");
                }
                scanner.close();
                int exitCode = process.waitFor();
                UnderdocxEnv.getInstance().logger.info("PDF generation execution, error code: " + exitCode + " output: " + output);
            }
            String pdfFileName = new Regex("." + getFileExtension()).replaceAll(tmpOdtFileName, ".pdf");
            File pdfFile = new File(tmpDir.getAbsolutePath() + "/" + pdfFileName);
            pdfFile.deleteOnExit();
            try (FileInputStream fis = new FileInputStream(pdfFile)) {
                IOUtils.copy(fis, os);
            }
            pdfFile.delete();
            tmpOdtFile.delete();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public abstract OdfElement getContentRoot();

}