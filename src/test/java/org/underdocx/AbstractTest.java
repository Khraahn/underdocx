package org.underdocx;

import org.apache.commons.io.FileUtils;
import org.underdocx.common.tools.Convenience;
import org.underdocx.common.tree.TreeWalker;
import org.underdocx.common.types.Resource;
import org.underdocx.doctypes.DocContainer;
import org.underdocx.environment.UnderdocxEnv;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.underdocx.common.tools.Convenience.buildList;

public abstract class AbstractTest {

    /*

        --- load and create files ---

     */
    protected InputStream getInputStream(String name) {
        return this.getClass().getResourceAsStream(name);
    }

    protected String getResourceAsString(String name) {
        try {
            return new String(getInputStream(name).readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File createTmpFile(String prefix, String extension) {
        try {
            return Files.createTempFile(prefix, "." + extension).toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected File createTmpFile(String extension) {
        return createTmpFile("tmp_", extension);
    }

    protected File createTmpFile(InputStream is, String extension, long lifetime) {
        try {
            File file = createTmpFile(extension);
            file.deleteOnExit();
            FileUtils.copyInputStreamToFile(is, file);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    file.delete();
                }
            }, lifetime);
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String createTmpUri(InputStream is, String extension) {
        return createTmpUri(is, extension, 1000 * 60);
    }

    protected String createTmpUri(InputStream is, String extension, long lifetime) {
        try {
            return createTmpFile(is, extension, lifetime).toURI().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected File createFileInTempDir(String fileName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File result = new File(tmpDir + "/" + fileName);
        System.out.println("Tmp: " + result);
        return result;
    }

    /*

        --- XML---

     */

    protected Document readXML(String xmlContent) {
        InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
        return readXML(is);
    }

    protected Document readXML(InputStream is) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getXMLString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", "4");
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Writer writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] readData(InputStream is) throws IOException {
        return is.readAllBytes();
    }

    protected byte[] readData(String filename) throws IOException {
        InputStream is = getInputStream(filename);
        return is.readAllBytes();
    }

    protected Resource readResource(InputStream is) throws IOException {
        return new Resource.DataResource(readData(is));
    }

    protected Resource readResource(String filename) throws IOException {
        return new Resource.ClassResource(this.getClass(), filename);
    }

    protected List<String> namesOf(Collection<Node> nodes) {
        return buildList(list -> nodes.forEach(element -> list.add(element.getNodeName())));
    }

    protected <T> List<T> listOf(T... elements) {
        return Arrays.asList(elements);
    }

    protected void assertEqualTrees(Node tree1, Node tree2) {
        TreeWalker walker1 = new TreeWalker(tree1, tree1);
        TreeWalker walker2 = new TreeWalker(tree2, tree2);
        while (walker1.hasNext()) {
            assertThat(walker2.hasNext()).isTrue();
            TreeWalker.VisitState state1 = walker1.next();
            TreeWalker.VisitState state2 = walker2.next();
            assertThat(state1.getNode().getNodeName()).isEqualTo(state2.getNode().getNodeName());
            assertThat(state1.isBeginVisit()).isEqualTo(state2.isBeginVisit());
        }
        assertThat(walker2.hasNext()).isFalse();
    }

    protected int countElements(Node node, String elementName) {
        TreeWalker walker = new TreeWalker(node, node);
        int count = 0;
        while (walker.hasNext()) {
            TreeWalker.VisitState state = walker.next();
            if (state.isBeginVisit() && state.getNode().getNodeName().equals(elementName)) {
                count++;
            }
        }
        return count;
    }

    protected List<Node> findTextNode(Node tree, String text) {
        return Convenience.buildList(result -> {
            TreeWalker walker = new TreeWalker(tree, tree, null);
            while (walker.hasNext()) {
                TreeWalker.VisitState state = walker.next();
                if (state.isBeginVisit() && state.getNode().getNodeType() == Node.TEXT_NODE && state.getNode().getTextContent().contains(text)) {
                    result.add(state.getNode());
                }
            }
        });
    }

    protected void show(DocContainer<?> doc) {
        File tmp = createTmpFile(doc.getFileExtension());
        try {
            doc.save(tmp);
            Desktop.getDesktop().open(tmp);
            UnderdocxEnv.getInstance().logger.trace("tmp file saved: " + tmp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
