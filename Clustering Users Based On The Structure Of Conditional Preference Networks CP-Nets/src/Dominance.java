import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Dominance {
	private String folderName;



	public Dominance (String folderName) throws IOException {

		this.folderName = folderName;
		readFileFromFolder();
	}

	public void readFileFromFolder() throws IOException{

		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile() && file.getName().contains(".xml")) {
				try {
					System.out.println("Modifying"+file.getPath());
					//modifyBetterOutcome(file.getPath());
					//modifyWorseOutcome(file.getPath());
					reDominance(file.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void modifyBetterOutcome(String fileName) throws ParserConfigurationException, SAXException, IOException, TransformerException{

		int[] finalOutcome = {2, 1, 1, 1, 1, 2, 2, 2};
		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList twoOutcomeValues =  doc.getElementsByTagName("OUTCOME");

		for (int temp = 0; temp < twoOutcomeValues.getLength(); temp++) {

			Node nNode = twoOutcomeValues.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				if(eElement.getElementsByTagName("LABEL").item(0).getTextContent().equals("BETTER")){
					NodeList betterOutcomeValues = eElement.getElementsByTagName("ASSIGNMENT");

					for(int i=0; i<betterOutcomeValues.getLength(); i++){
						int attIndex = i+1;

						Node mNode = betterOutcomeValues.item(i);

						if(mNode.getNodeType() == Node.ELEMENT_NODE) {
							Element mElement = (Element) mNode;

							if(mElement.getElementsByTagName("PREFERENCE-VARIABLE").item(0).getTextContent().equals("x"+attIndex)){
								String value = mElement.getElementsByTagName("VALUATION").item(0).getTextContent();

								if(Integer.valueOf(value) != finalOutcome[i]){
									mElement.getElementsByTagName("VALUATION").item(0).setTextContent(""+finalOutcome[i]);
									//eElement.setTextContent(""+finalOutcome[i]);
								}
							}
						}
					}
				}
			}						
		}
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(fileName));
				transformer.transform(source, result);

				System.out.println("Done");
	}
	
	public void modifyWorseOutcome(String fileName) throws ParserConfigurationException, SAXException, IOException, TransformerException{

		int[] finalOutcome = {3, 3, 3, 3, 3, 3, 3, 3};
		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList twoOutcomeValues =  doc.getElementsByTagName("OUTCOME");

		for (int temp = 0; temp < twoOutcomeValues.getLength(); temp++) {

			Node nNode = twoOutcomeValues.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				if(eElement.getElementsByTagName("LABEL").item(0).getTextContent().equals("WORSE")){
					NodeList betterOutcomeValues = eElement.getElementsByTagName("ASSIGNMENT");

					for(int i=0; i<betterOutcomeValues.getLength(); i++){
						int attIndex = i+1;

						Node mNode = betterOutcomeValues.item(i);

						if(mNode.getNodeType() == Node.ELEMENT_NODE) {
							Element mElement = (Element) mNode;

							if(mElement.getElementsByTagName("PREFERENCE-VARIABLE").item(0).getTextContent().equals("x"+attIndex)){
								String value = mElement.getElementsByTagName("VALUATION").item(0).getTextContent();

								if(Integer.valueOf(value) != finalOutcome[i]){
									mElement.getElementsByTagName("VALUATION").item(0).setTextContent(""+finalOutcome[i]);
									//eElement.setTextContent(""+finalOutcome[i]);
								}
							}
						}
					}
				}
			}						
		}
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		transformer.transform(source, result);

		System.out.println("Done");
	}
	
	public void reDominance(String fileName) throws ParserConfigurationException, SAXException, IOException, TransformerException{
		
		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList twoOutcomeValues =  doc.getElementsByTagName("OUTCOME");

		for (int temp = 0; temp < twoOutcomeValues.getLength(); temp++) {

			Node nNode = twoOutcomeValues.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				if(eElement.getElementsByTagName("LABEL").item(0).getTextContent().equals("WORSE")){
					
					eElement.getElementsByTagName("LABEL").item(0).setTextContent("BETTER");
				}
				
				else if (eElement.getElementsByTagName("LABEL").item(0).getTextContent().equals("BETTER")){
					
					eElement.getElementsByTagName("LABEL").item(0).setTextContent("WORSE");
				}
			}						
		}
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(fileName));
		transformer.transform(source, result);

		System.out.println("Done");
	}
}
