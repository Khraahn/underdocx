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

package org.underdocx.doctypes.odf.modifiers.images.createimage;

import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.commands.image.types.*;
import org.underdocx.doctypes.odf.constants.OdfLengthUnit;

public interface CreateImageModifierData {

    String getName();

    String getDescr();

    Wrap getWrap();

    Anchor getAnchor();

    HorizontalPos getHorizontalPos();

    HorizontalRel getHorizontalRel();

    VerticalPos getVerticalPos();

    VerticalRel getVerticalRel();

    OdfLengthUnit getUnit();

    Integer getX();

    Integer getY();

    Integer getWidth();

    Integer getHeight();

    Resource getResource();

    class Simple implements CreateImageModifierData {
        private Resource resource;
        private String name;
        private String descr;
        private Wrap wrap;
        private Anchor anchor;
        private HorizontalPos horizontalPos;
        private HorizontalRel horizontalRel;
        private VerticalPos verticalPos;
        private VerticalRel verticalRel;
        private OdfLengthUnit unit;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;

        public Simple(
                Resource resource,
                String name,
                String descr,
                Wrap wrap,
                Anchor anchor,
                HorizontalPos horizontalPos,
                HorizontalRel horizontalRel,
                VerticalPos verticalPos,
                VerticalRel verticalRel,
                OdfLengthUnit unit,
                Integer x,
                Integer y,
                Integer width,
                Integer height) {
            this.resource = resource;
            this.name = name;
            this.descr = descr;
            this.wrap = wrap;
            this.anchor = anchor;
            this.horizontalPos = horizontalPos;
            this.horizontalRel = horizontalRel;
            this.verticalPos = verticalPos;
            this.verticalRel = verticalRel;
            this.unit = unit;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public String getName() {
            return name;
        }

        public String getDescr() {
            return descr;
        }

        public Wrap getWrap() {
            return wrap;
        }

        public Anchor getAnchor() {
            return anchor;
        }

        public HorizontalPos getHorizontalPos() {
            return horizontalPos;
        }

        public HorizontalRel getHorizontalRel() {
            return horizontalRel;
        }

        public VerticalPos getVerticalPos() {
            return verticalPos;
        }

        public VerticalRel getVerticalRel() {
            return verticalRel;
        }

        public OdfLengthUnit getUnit() {
            return unit;
        }

        public Integer getX() {
            return x;
        }

        public Integer getY() {
            return y;
        }

        public Integer getWidth() {
            return width;
        }

        public Integer getHeight() {
            return height;
        }

        @Override
        public Resource getResource() {
            return resource;
        }
    }
}
