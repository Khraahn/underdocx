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

package org.underdocx.enginelayers.baseengine;

import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.Nodes;
import org.w3c.dom.Node;

public interface CommandHandlerResult {
    CommandHandlerResult IGNORED = new DefaultResult(CommandHandlerResultType.IGNORED);
    CommandHandlerResult EXECUTED_PROCEED = new DefaultResult(CommandHandlerResultType.EXECUTED_PROCEED);
    CommandHandlerResult EXECUTED_FULL_RESCAN = new DefaultResult(CommandHandlerResultType.EXECUTED_FULL_RESCAN);
    CommandHandlerResult EXECUTED_END_OF_DOC = new DefaultResult(CommandHandlerResultType.EXECUTED_END_OF_DOC);
    CommandHandlerResult EXIT = new DefaultResult(CommandHandlerResultType.EXIT);

    Factory FACTORY = new Factory();

    enum CommandHandlerResultType {
        IGNORED,
        EXECUTED_PROCEED,
        EXECUTED_FULL_RESCAN,
        EXECUTED_RESTART_AT_NODE,
        EXECUTED_END_OF_DOC,
        EXIT
    }

    CommandHandlerResultType getResultType();

    Node getRestartNode();

    class Factory {
        public CommandHandlerResult startAtNode(Node node) {
            return new DefaultResult(node);
        }

        public CommandHandlerResult convert(ModifierNodeResult result) {
            if (result.getSuccess()) {
                if (result.isEndOfDoc()) {
                    return new DefaultResult(CommandHandlerResultType.EXECUTED_END_OF_DOC);
                }
                if (result.getEndNode().isPresent()) {
                    return new DefaultResult(result.getEndNode().get());
                } else {
                    return EXECUTED_PROCEED;
                }
            } else {
                return IGNORED;
            }
        }

        public CommandHandlerResult convert(ModifierResult result) {
            return result.getSuccess() ? EXECUTED_PROCEED : IGNORED;
        }

        public CommandHandlerResult startAtNextNode(Node node) {
            return Convenience.getOrDefault(Nodes.findNextNode(node, true), CommandHandlerResult.FACTORY::startAtNode, CommandHandlerResult.EXECUTED_END_OF_DOC);
        }
    }

    class DefaultResult implements CommandHandlerResult {
        private final Node node;
        private final CommandHandlerResultType type;

        public DefaultResult(CommandHandlerResultType type) {
            this.node = null;
            this.type = type;
        }

        public DefaultResult(Node node) {
            this.node = node;
            this.type = CommandHandlerResultType.EXECUTED_RESTART_AT_NODE;
        }

        @Override
        public CommandHandlerResultType getResultType() {
            return type;
        }

        @Override
        public Node getRestartNode() {
            return node;
        }

        @Override
        public String toString() {
            return type == CommandHandlerResultType.EXECUTED_RESTART_AT_NODE ? "CommandHandlerResult:EXECUTED_RESTART_AT_NODE, " + node : "CommandHandlerResult:" + type;
        }
    }
}
