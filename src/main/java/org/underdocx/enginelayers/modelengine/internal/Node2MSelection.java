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

package org.underdocx.enginelayers.modelengine.internal;

import org.underdocx.common.placeholder.TextualPlaceholderToolkit;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.enginelayers.baseengine.EngineAccess;
import org.underdocx.enginelayers.baseengine.SelectedNode;
import org.underdocx.enginelayers.modelengine.MSelection;
import org.underdocx.enginelayers.modelengine.dataaccess.DataAccess;
import org.w3c.dom.Node;

import java.util.Optional;

public class Node2MSelection {

    public static <C extends DocContainer<D>, P, D> MSelection<C, P, D> createMSelection(MSelection<C, P, D> origin, SelectedNode<P> newNode) {
        return new MSelection<C, P, D>() {
            @Override
            public Optional<DataAccess> getDataAccess() {
                return origin.getDataAccess();
            }

            @Override
            public C getDocContainer() {
                return origin.getDocContainer();
            }

            @Override
            public EngineAccess<C, D> getEngineAccess() {
                return origin.getEngineAccess();
            }

            @Override
            public Node getNode() {
                return newNode.getNode();
            }

            @Override
            public P getPlaceholderData() {
                return newNode.getPlaceholderData();
            }

            @Override
            public Optional<TextualPlaceholderToolkit<P>> getPlaceholderToolkit() {
                return newNode.getPlaceholderToolkit();
            }
        };
    }
}
