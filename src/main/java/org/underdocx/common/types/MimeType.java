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

import org.underdocx.common.tools.Convenience;

import java.util.*;

public enum MimeType {


    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_JPEG("image/jpeg", "jpg", "jpeg", "jpe"),
    IMAGE_SVG("image/svg+xml", "svg"),
    IMAGE_TIFF("image/tiff", "tif", "tiff"),
    IMAGE_WEBP("image/webp", "webp"),

    APPLICATION_ODP("application/vnd.oasis.opendocument.presentation", "odp"),
    APPLICATION_ODG("application/vnd.oasis.opendocument.graphics", "odg"),
    APPLICATION_ODS("application/vnd.oasis.opendocument.spreadsheet", "ods"),
    APPLICATION_ODT("application/vnd.oasis.opendocument.text", "odt"),

    APPLICATION_PDF("application/pdf", "pdf"),

    TEXT_PLAIN("text/plain", "txt"),
    TEXT_XML("text/xml", "xml");


    private final String mimeType;
    private final String[] extensions;

    MimeType(String mimeType, String... extensions) {
        this.mimeType = mimeType;
        this.extensions = extensions;
        for (String extension : extensions) {
            addInternal(extension.toLowerCase().replace(".", ""), this);
        }
    }

    private void addInternal(String extension, MimeType mimeType) {
        MimeTypeRegistry.register(extension, mimeType);
    }

    public Set<String> getExtensions() {
        return new HashSet<>(Arrays.asList(extensions));
    }

    public String getPreferredExtension() {
        return extensions[0];
    }

    public String getMimeType() {
        return mimeType;
    }

    public static Optional<MimeType> getMimeType(String extension) {
        return Optional.ofNullable(MimeTypeRegistry.lookup(extension.toLowerCase().replace(".", "")));
    }

    public static Optional<MimeType> getMimeTypeByResourceName(String resourceAddress) {
        return getMimeType(getExtension(resourceAddress));
    }

    public static String tryConvertExtensionToMimeType(String extensionOrMimeType) {
        if (extensionOrMimeType == null) {
            return null;
        }
        Optional<MimeType> test = getMimeType(extensionOrMimeType);
        if (test.isPresent()) {
            return test.get().getMimeType();
        }
        return extensionOrMimeType;
    }

    public static String tryResolveMimeType(String resourceAddress) {
        return Convenience.build(null, result -> {
            getMimeTypeByResourceName(resourceAddress).ifPresent(m -> {
                result.value = m.getMimeType();
            });
        });
    }

    public static String getExtension(String resourceAddress) {
        int index = resourceAddress.lastIndexOf(".");
        if (index >= 0 && index != resourceAddress.length() - 1) {
            return resourceAddress.substring(index + 1).toLowerCase();
        } else {
            return resourceAddress.toLowerCase();
        }
    }

    private static class MimeTypeRegistry {
        private static final Map<String, MimeType> extensionToMimeType = new HashMap<>();

        private static void register(String extension, MimeType mimeType) {
            extensionToMimeType.put(extension, mimeType);
        }

        private static MimeType lookup(String extension) {
            return extensionToMimeType.get(extension);
        }
    }
}
