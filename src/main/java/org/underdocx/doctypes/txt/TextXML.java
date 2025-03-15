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

package org.underdocx.doctypes.txt;

import org.apache.commons.io.IOUtils;
import org.underdocx.common.tree.TreeWalker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TextXML {

    private final Document doc;

    public TextXML(String content) {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        this.doc = parse(is);
    }

    public TextXML() {
        this.doc = createEmptyDocument();
    }

    public TextXML(InputStream is) {
        this.doc = parse(is);
    }

    public Document getDoc() {
        return doc;
    }

    private static Document createEmptyDocument() {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("root");
            doc.appendChild(root);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return doc;
    }

    private static Document parse(InputStream is) {
        Document doc = createEmptyDocument();
        IOUtils.readLines(is, StandardCharsets.UTF_8).forEach(line -> appendLine(line, doc));
        return doc;
    }

    private static void appendLine(String line, Document doc) {
        Element p = doc.createElement("p");
        p.setTextContent(line);
        doc.getFirstChild().appendChild(p);
    }

    public void appendText(String content) {
        content.lines().forEach(line -> appendLine(line, doc));
    }

    public void save(OutputStream os) throws IOException {
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        save(w::write);
        w.flush();
        w.close();
    }

    public void save(StringBuilder w) {
        try {
            save(w::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(TextWriter w) throws IOException {
        TreeWalker walker = new TreeWalker(doc, doc, null);
        List<String> stack = new ArrayList<>();
        boolean lastWasEndOfP = false;
        while (walker.hasNext()) {
            TreeWalker.VisitState state = walker.next();
            if (state.getNode().getNodeType() != Node.TEXT_NODE) {
                if (state.isBeginVisit()) {
                    stack.add(0, state.getNode().getNodeName());
                } else {
                    stack.remove(0);
                }
            }
            boolean writeActive = !stack.isEmpty() && (stack.get(0).equals("p") || stack.get(0).equals("span"));
            if (writeActive && lastWasEndOfP) {
                w.write("\n");
            }
            lastWasEndOfP = (!state.isBeginVisit() && state.getNode().getNodeName().equals("p"));
            if (state.isBeginVisit() && state.getNode().getNodeType() == Node.TEXT_NODE && writeActive) {
                w.write(state.getNode().getTextContent());
            }
        }
    }

    public String getPlainText() {
        StringBuilder b = new StringBuilder();
        save(b);
        return b.toString();
    }

    public interface TextWriter {
        void write(String s) throws IOException;
    }
}
