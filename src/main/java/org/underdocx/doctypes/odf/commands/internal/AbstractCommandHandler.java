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

package org.underdocx.doctypes.odf.commands.internal;

import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.CommandHandlerResult;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.modelengine.MCommandHandler;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;
import org.underdocx.environment.err.Problems;


public abstract class AbstractCommandHandler<C extends DocContainer<D>, P, D> implements MCommandHandler<C, P, D> {

    protected MSelection<C, P, D> selection = null;
    protected DataAccess dataAccess = null;
    protected EngineAccess<C, D> engineAccess = null;
    protected P placeholderData = null;


    @Override
    public CommandHandlerResult tryExecuteCommand(MSelection<C, P, D> selection) {
        this.selection = selection;
        this.dataAccess = Problems.MISSING_VALUE.get(selection.getDataAccess(), "modelAccess");
        this.engineAccess = selection.getEngineAccess();
        this.placeholderData = selection.getPlaceholderData();
        return tryExecuteCommand();
    }

    abstract protected CommandHandlerResult tryExecuteCommand();

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
