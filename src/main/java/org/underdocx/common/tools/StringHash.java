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

package org.underdocx.common.tools;

import org.apache.commons.codec.digest.MurmurHash3;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Creates a short Base64 string hash value from String or binary data. Optimized for speed and minimal collisions, not for security
 */
public class StringHash {

    public static String createStringHash32(byte[] data) {
        int hash = MurmurHash3.hash32x86(data);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(hash);
        byte[] bytes = buffer.array();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String createStringHash128(byte[] data) {
        long[] hash = MurmurHash3.hash128(data);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * hash.length);
        for (long l : hash) {
            buffer.putLong(l);
        }
        byte[] bytes = buffer.array();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String createStringHash32(String data) {
        return createStringHash32(data.getBytes());
    }

    public static String createStringHash128(String data) {
        return createStringHash128(data.getBytes());
    }
}
