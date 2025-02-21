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

package org.underdocx.common.types;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.underdocx.common.tools.StringHash;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * An implementation that provides {@link InputStream}
 */
public interface Resource {

    /**
     * @return A new created {@link InputStream} of a resource
     * @throws IOException
     */
    InputStream openStream() throws IOException;

    String getIdentifier();

    byte[] getData() throws IOException;

    Optional<URI> getURI();

    class FileResource implements Resource {

        private final File file;

        public FileResource(File file) {
            this.file = file;
        }

        public FileResource(String file) {
            this.file = new File(file);
        }

        @Override
        public InputStream openStream() throws IOException {
            return new FileInputStream(file);
        }

        @Override
        public String getIdentifier() {
            return file.getAbsolutePath();
        }

        @Override
        public Optional<URI> getURI() {
            return Optional.of(file.toURI());
        }

        @Override
        public byte[] getData() throws IOException {
            return FileUtils.readFileToByteArray(file);
        }
    }

    class DataResource implements Resource {

        protected String cachedIdentifier = null;

        protected final byte[] data;

        public DataResource(byte[] data) {
            this.data = data;
        }

        public DataResource(byte[] data, String identifier) {
            this.data = data;
            this.cachedIdentifier = identifier;
        }

        @Override
        public InputStream openStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        @Override
        public String getIdentifier() {
            if (cachedIdentifier == null) {
                cachedIdentifier = StringHash.createStringHash32(data);
            }
            return cachedIdentifier;
        }

        @Override
        public Optional<URI> getURI() {
            return Optional.empty();
        }

        @Override
        public byte[] getData() {
            return data;
        }
    }

    class ClassResource implements Resource {

        private Class clazz;
        private String resourceName;

        public ClassResource(Class clazz, String resourceName) {
            this.clazz = clazz;
            this.resourceName = resourceName;
        }

        @Override
        public InputStream openStream() throws IOException {
            return clazz.getResourceAsStream(resourceName);
        }

        @Override
        public String getIdentifier() {
            return clazz.getName() + ":" + resourceName;
        }

        @Override
        public byte[] getData() throws IOException {
            return IOUtils.toByteArray(openStream());
        }

        @Override
        public Optional<URI> getURI() {
            return Optional.empty();
        }
    }

    class UriResource implements Resource {

        private final URI uri;

        public UriResource(URI uri) {
            this.uri = uri;
        }

        public UriResource(String uri) throws URISyntaxException {
            this.uri = new URI(uri);
        }

        @Override
        public InputStream openStream() throws IOException {
            return uri.toURL().openStream();
        }

        @Override
        public String getIdentifier() {
            return uri.toString();
        }

        @Override
        public byte[] getData() throws IOException {
            return IOUtils.toByteArray(uri);
        }

        @Override
        public Optional<URI> getURI() {
            return Optional.of(uri);
        }
    }

}
