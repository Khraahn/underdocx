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

package org.underdocx.enginelayers.odtengine.commands;

import org.odftoolkit.odfdom.doc.OdfGraphicsDocument;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.odf.odg.OdgContainer;
import org.underdocx.enginelayers.odtengine.commands.importcommand.AbstractImportCommandHander;
import org.underdocx.environment.err.Problems;

import java.io.IOException;

public class OdgImportCommandHandler extends AbstractImportCommandHander<OdgContainer, OdfGraphicsDocument> {

    @Override
    protected OdgContainer createContainer(Resource resource) throws IOException {
        return new OdgContainer(resource);
    }

    @Override
    protected OdgContainer createContainer(byte[] data) throws IOException {
        return new OdgContainer(data);
    }

    @Override
    protected void checkPackageExistence(String page) {
        Problems.MISSING_VALUE.notNull(page, PAGE_ATTR);
    }
}
