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

package org.underdocx.doctypes;

import org.underdocx.common.tools.TmpFile;
import org.underdocx.environment.UnderdocxEnv;

import java.awt.*;
import java.io.*;

public interface DocContainer<D> {

    D getDocument();

    void setDocument(D doc);

    void load(InputStream is) throws IOException;

    default void load(File file) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            load(is);
        }
    }

    void save(OutputStream os) throws IOException;

    default void save(File file) throws IOException {
        UnderdocxEnv.getInstance().logger.trace("Saving tmp file: " + file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            save(fos);
        }
    }

    String getFileExtension();

    default File createTmpFile() throws IOException {
        return createTmpFile("tmp_", true, null);
    }

    default File createTmpFile(Long lifetime) throws IOException {
        return createTmpFile("tmp_", true, lifetime);
    }

    default void createDebugFile(String prefix) {
        try {
            createTmpFile(System.currentTimeMillis() + prefix, false, null);
        } catch (IOException e) {
            UnderdocxEnv.getInstance().logger.error(e);
        }
    }


    default File createTmpFile(String prefix, boolean deleteOnExit, Long lifetime) throws IOException {
        File tmpFile = TmpFile.createTmpFile(prefix, "." + getFileExtension(), deleteOnExit, lifetime);
        save(tmpFile);
        return tmpFile;
    }


    default String createURI(Long lifetime) throws IOException {
        return createTmpFile(lifetime).toURI().toString();
    }

    default String createURI() throws IOException {
        return createURI(null);
    }

    void appendText(String content);

    default void show() throws IOException {
        File tmp = createTmpFile("preview_", false, null);
        Desktop.getDesktop().open(tmp);
    }

}
