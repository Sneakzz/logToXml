package models;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utils.LogUtils;

/**
 *
 * @author Kenny
 */
public class XmlCreator {

    private final DocumentBuilderFactory dbFactory;
    private DocumentBuilder docBuilder;
    ArrayList<Rendering> renderings;

    public XmlCreator(ArrayList<Rendering> renderings) {
        this.dbFactory = DocumentBuilderFactory.newInstance();
        this.renderings = renderings;
    }

    public void run() {
        // the completed xml document will be created in a seperate folder
        // where the programs execution originates from
        File xmlFilepath = new File("output//renderings.xml");
        xmlFilepath.getParentFile().mkdirs();

        LogUtils.printMessage("Creating the xml document...");
        try {
            // create the document builder and the document itself
            this.docBuilder = this.dbFactory.newDocumentBuilder();
            Document doc = this.docBuilder.newDocument();

            // report node (root)
            Element reportNode = doc.createElement("report");
            doc.appendChild(reportNode);

            // variable for updating the progress
            long amountDone = 0;
            for (Rendering rendering : this.renderings) {
                createRenderNode(doc, rendering, reportNode);
                amountDone++;

                System.out.print("\rProgress creating xml file: " + updateProgress(amountDone, this.renderings.size()) + "%");
            }

            createSummaryNode(doc, reportNode);

            // transformer to handle xml formatting
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "7");

            // source for the transformer (this is the xml to transform)
            DOMSource source = new DOMSource(doc);

            // output for the transformer 
            StreamResult output = new StreamResult(xmlFilepath);

            transformer.transform(source, output);

            System.out.println();
            LogUtils.printMessage("Done constructing the XML file!");

        } catch (ParserConfigurationException ex) {
            LogUtils.printMessage("ERROR", ex.getMessage());
        } catch (TransformerConfigurationException ex) {
            LogUtils.printMessage("ERROR", ex.getMessage());
        } catch (TransformerException ex) {
            LogUtils.printMessage("ERROR", ex.getMessage());
        }
    }

    private void createRenderNode(Document doc, Rendering rendering, Element reportNode) {
        Element renderingNode = doc.createElement("rendering");

        createDocIdNode(doc, rendering, renderingNode);
        createPageNumNode(doc, rendering, renderingNode);
        createUidNode(doc, rendering, renderingNode);
        createStartTimestampNode(doc, rendering, renderingNode);
        createGetTimestampNode(doc, rendering, renderingNode);

        reportNode.appendChild(renderingNode);
    }

    private void createDocIdNode(Document doc, Rendering rendering, Element renderingNode) {
        Comment comment = doc.createComment("Document id");
        Element documentNode = doc.createElement("document");
        documentNode.appendChild(doc.createTextNode(Integer.toString(rendering.getDocumentId())));

        renderingNode.appendChild(comment);
        renderingNode.appendChild(documentNode);
    }

    private void createPageNumNode(Document doc, Rendering rendering, Element renderingNode) {
        Element pageNode = doc.createElement("page");
        pageNode.appendChild(doc.createTextNode(Integer.toString(rendering.getPageNumber())));

        renderingNode.appendChild(pageNode);
    }

    private void createUidNode(Document doc, Rendering rendering, Element renderingNode) {
        Comment comment = doc.createComment("UID of the startRendering");
        Element uidNode = doc.createElement("uid");
        uidNode.appendChild(doc.createTextNode(rendering.getStartRenderUID()));

        renderingNode.appendChild(comment);
        renderingNode.appendChild(uidNode);
    }

    private void createStartTimestampNode(Document doc, Rendering rendering, Element renderingNode) {
        Comment comment = doc.createComment("One or more timestamps of the startRendering");

        renderingNode.appendChild(comment);

        if (!rendering.getStartRenders().isEmpty()) {
            for (String timestamp : rendering.getStartRenders()) {
                Element startNode = doc.createElement("start");
                startNode.appendChild(doc.createTextNode(timestamp));
                renderingNode.appendChild(startNode);
            }
        }
    }

    private void createGetTimestampNode(Document doc, Rendering rendering, Element renderingNode) {
        Comment comment = doc.createComment("One or more timestamps of getRendering");

        renderingNode.appendChild(comment);

        if (!rendering.getGetRenders().isEmpty()) {
            for (String timestamp : rendering.getGetRenders()) {
                Element getNode = doc.createElement("get");
                getNode.appendChild(doc.createTextNode(timestamp));
                renderingNode.appendChild(getNode);
            }
        }
    }

    private void createSummaryNode(Document doc, Element reportNode) {
        Comment comment = doc.createComment("Summary");
        reportNode.appendChild(comment);

        Element summaryNode = doc.createElement("summary");

        createCountNode(doc, summaryNode);
        createDuplicatesNode(doc, summaryNode);
        createUnnecessaryNode(doc, summaryNode);

        reportNode.appendChild(summaryNode);
    }

    private void createCountNode(Document doc, Element summaryNode) {
        Comment comment = doc.createComment("Total number of renderings");
        Element countNode = doc.createElement("count");
        countNode.appendChild(doc.createTextNode(Integer.toString(this.renderings.size())));

        summaryNode.appendChild(comment);
        summaryNode.appendChild(countNode);
    }

    private void createDuplicatesNode(Document doc, Element summaryNode) {
        Comment comment = doc.createComment("Number of double renderings (multiple starts with same UID)");
        Element duplicatesNode = doc.createElement("duplicates");

        int duplicates = 0;
        for (Rendering rendering : this.renderings) {
            if (rendering.getStartRenders().size() > 1) {
                duplicates += rendering.getStartRenders().size() - 1;
            }
        }

        duplicatesNode.appendChild(doc.createTextNode(Integer.toString(duplicates)));

        summaryNode.appendChild(comment);
        summaryNode.appendChild(duplicatesNode);
    }

    private void createUnnecessaryNode(Document doc, Element summaryNode) {
        Comment comment = doc.createComment("Number of startRenderings without get");
        Element unnecessaryNode = doc.createElement("unnecessary");

        int unnecessary = 0;
        for (Rendering rendering : this.renderings) {
            if (rendering.getGetRenders().isEmpty()) {
                unnecessary += rendering.getStartRenders().size();
            }
        }

        unnecessaryNode.appendChild(doc.createTextNode(Integer.toString(unnecessary)));

        summaryNode.appendChild(comment);
        summaryNode.appendChild(unnecessaryNode);
    }

    private double updateProgress(long amountDone, long amountToDo) {
        return Math.ceil(((double) amountDone / amountToDo) * 100);
    }
}
