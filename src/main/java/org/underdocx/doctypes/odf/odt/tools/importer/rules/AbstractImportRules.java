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

package org.underdocx.doctypes.odf.odt.tools.importer.rules;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of Rules which node have to be copied and which attribute values have to be renamed
 */
public abstract class AbstractImportRules {

    protected List<ProviderDescr> providerDescrs;
    protected List<ConsumerDescr> consumerDescrs;
    protected List<TagPathDescr> copyRules;
    protected TagPathDescr mainCopyRule;
    protected MainCopyExecutor mainCopyExecutor;
    protected boolean initialized = false;

    protected AbstractImportRules() {
    }

    public List<ProviderDescr> getProviderDescrs() {
        return providerDescrs;
    }

    public List<ConsumerDescr> getConsumerDescrs() {
        return consumerDescrs;
    }

    public List<TagPathDescr> getCopyRules() {
        return copyRules;
    }

    public TagPathDescr getMainCopyRule() {
        return mainCopyRule;
    }

    public MainCopyExecutor getMainCopyExecutor() {
        return mainCopyExecutor;
    }

    protected void init() {
        if (!initialized) {
            initialized = true;
            providerDescrs = new ArrayList<>();
            consumerDescrs = new ArrayList<>();
            copyRules = new ArrayList<>();
            createRules();
            providerDescrs = Collections.unmodifiableList(providerDescrs);
            consumerDescrs = Collections.unmodifiableList(consumerDescrs);
            copyRules = Collections.unmodifiableList(copyRules);
        }
    }

    protected abstract void createRules();

    protected static TagDescr t(String s) {
        return new TagDescr(s);
    }

    protected static TagDescr t(Node node) {
        return new TagDescr(node.getNodeName());
    }

    protected static AttrDescr a(String s) {
        return new AttrDescr(s);
    }

}
