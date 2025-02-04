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

package org.underdocx.enginelayers.baseengine.modifiers.existingimage;

import org.underdocx.common.types.Resource;

public interface ExistingImageModifierData {

    Resource getResource();

    String getFileName();

    boolean isKeepWidth();

    String getTitle();

    class Simple implements ExistingImageModifierData {
        boolean keepWidth;
        Resource resource;
        String newName;
        String title;

        public Simple(boolean keepWidth, Resource resource, String newName, String title) {
            this.keepWidth = keepWidth;
            this.resource = resource;
            this.newName = newName;
            this.title = title;
        }

        @Override
        public Resource getResource() {
            return resource;
        }

        public String getFileName() {
            return newName;
        }

        @Override
        public boolean isKeepWidth() {
            return keepWidth;
        }

        @Override
        public String getTitle() {
            return title;
        }
    }
}
