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
import java.util.Base64;
import java.util.Optional;

/**
 * An implementation that provides {@link InputStream}
 */
public interface Resource {

    /**
     * @return A new created {@link InputStream} of a resource
     */
    InputStream openStream() throws IOException;

    String getIdentifier();

    Optional<String> getMimeType();

    byte[] getData() throws IOException;

    class FileResource implements Resource {

        private final File file;
        private final String mimeType;

        public FileResource(File file) {
            this(file, null);
        }

        public FileResource(String file) {
            this(new File(file), null);
        }

        public FileResource(File file, String mimeType) {
            this.file = file;
            this.mimeType = mimeType == null ? MimeType.tryResolveMimeType(file.getName()) : mimeType;
        }

        public FileResource(String file, String mimeType) {
            this(new File(file), mimeType);
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
        public Optional<String> getMimeType() {
            return Optional.ofNullable(mimeType);
        }

        @Override
        public byte[] getData() throws IOException {
            return FileUtils.readFileToByteArray(file);
        }
    }

    class DataResource implements Resource {

        protected String cachedIdentifier = null;

        protected final byte[] data;
        protected final String mimeType;

        public DataResource(byte[] data) {
            this.data = data;
            mimeType = null;
        }

        public DataResource(byte[] data, String identifier) {
            this.data = data;
            this.cachedIdentifier = identifier;
            this.mimeType = MimeType.tryResolveMimeType(identifier);
        }


        public DataResource(String mimeType, byte[] data, String identifier) {
            this.data = data;
            this.cachedIdentifier = identifier;
            this.mimeType = mimeType;
        }

        public DataResource(String mimeType, byte[] data) {
            this.data = data;
            this.cachedIdentifier = null;
            this.mimeType = mimeType;
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
        public Optional<String> getMimeType() {
            return Optional.ofNullable(mimeType);
        }

        @Override
        public byte[] getData() {
            return data;
        }
    }

    class ClassResource implements Resource {

        private final Class<?> clazz;
        private final String resourceName;
        private final String mimeType;

        public ClassResource(Class<?> clazz, String resourceName) {
            this.clazz = clazz;
            this.resourceName = resourceName;
            this.mimeType = MimeType.tryResolveMimeType(resourceName);
        }

        public ClassResource(Class<?> clazz, String resourceName, String mimeType) {
            this.clazz = clazz;
            this.resourceName = resourceName;
            this.mimeType = mimeType;
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
        public Optional<String> getMimeType() {
            return Optional.ofNullable(mimeType);
        }

        @Override
        public byte[] getData() throws IOException {
            return IOUtils.toByteArray(openStream());
        }

    }

    class UriResource implements Resource {

        private final URI uri;
        private final String mimeType;

        public UriResource(URI uri) {
            this.uri = uri;
            this.mimeType = MimeType.tryResolveMimeType(uri.toString());
        }

        public UriResource(String uri) throws URISyntaxException {
            this.uri = new URI(uri);
            this.mimeType = MimeType.tryResolveMimeType(uri.toString());
        }

        public UriResource(URI uri, String mimeType) {
            this.uri = uri;
            this.mimeType = mimeType;
        }

        public UriResource(String uri, String mimeType) throws URISyntaxException {
            this.uri = new URI(uri);
            this.mimeType = mimeType;
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
        public Optional<String> getMimeType() {
            return Optional.ofNullable(mimeType);
        }

        @Override
        public byte[] getData() throws IOException {
            return IOUtils.toByteArray(uri);
        }
    }

    class Base64Resource implements Resource {

        private final DataResource innerResource;

        public Base64Resource(String base64) {
            innerResource = new DataResource(Base64.getDecoder().decode(base64), base64);
        }

        public Base64Resource(String mimeType, String base64, String identifier) {
            innerResource = new DataResource(mimeType, Base64.getDecoder().decode(base64), identifier);
        }


        @Override
        public InputStream openStream() throws IOException {
            return innerResource.openStream();
        }

        @Override
        public String getIdentifier() {
            return innerResource.getIdentifier();
        }

        @Override
        public Optional<String> getMimeType() {
            return innerResource.getMimeType();
        }

        @Override
        public byte[] getData() throws IOException {
            return innerResource.getData();
        }
    }

}
