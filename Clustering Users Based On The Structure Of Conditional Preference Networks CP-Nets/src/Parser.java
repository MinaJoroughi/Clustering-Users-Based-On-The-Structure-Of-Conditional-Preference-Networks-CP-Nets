import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Parser {

	private String fileName;
	//private ArrayList<String> attributesName = new ArrayList<>();
	//private ArrayList<Attribute> attributes = new ArrayList<>();
	private Document doc;
	private ArrayList<Integer> atts;
	final static Random random = new Random();



	public Parser(String fileName) throws ParserConfigurationException, SAXException, IOException {
		this.fileName = fileName;
		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
	}


	public Parser(String fileName, ArrayList<Integer> atts) throws ParserConfigurationException, SAXException, IOException {
		this.fileName = fileName;
		this.atts = atts;
		File inputFile = new File(fileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
	}


	/*public ArrayList<Attribute> getAttributes() {
		return attributes;
	}*/


	/*	public void findAttributes(){
		NodeList nList = doc.getElementsByTagName("PREFERENCE-VARIABLE");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);         
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				if(eElement.getElementsByTagName("VARIABLE-NAME").getLength() != 0){
					String name = eElement
							.getElementsByTagName("VARIABLE-NAME")
							.item(0)
							.getTextContent();
					attributesName.add(name);
				}
			}
		}        
	}*/

	public void findAttributeAndParent(){
		NodeList prefrenceStatement = doc.getElementsByTagName("PREFERENCE-STATEMENT");

		for (int temp = 0; temp < prefrenceStatement.getLength(); temp++) {
			Node nNode = prefrenceStatement.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				for(int i=1; i<=Attribute.getNumAtts();i++){
					if(eElement.getElementsByTagName("STATEMENT-ID").item(0).getTextContent().equals("p"+i+"_1")){

						String child = eElement.getElementsByTagName("PREFERENCE-VARIABLE").item(0).getTextContent();
						int index = Integer.valueOf(child.substring(1, child.length()));
						//System.out.println(child+ " ---- "+ index );
						if(Attribute.getAtts()[index] == null){
							Attribute.getAtts()[index] = new Attribute(child);
						}

						NodeList condition = eElement.getElementsByTagName("CONDITION");
						for(int j =0; j<condition.getLength(); j++){
							Node n = condition.item(j);
							String attributeName = n.getTextContent().substring(0, 2);
							//System.out.print(n.getTextContent() +", ");
							int parentIndex = Integer.valueOf(attributeName.substring(1, 2));

							String[] ss = n.getTextContent().split("=");
							//System.out.print(ss[0] +", ");
							int parentIndex2 = Integer.valueOf(ss[0].substring(1, ss[0].length()));

							if(Attribute.getAtts()[parentIndex2] == null){
								Attribute.getAtts()[parentIndex2] = new Attribute(ss[0]);
							}
							Attribute.getAtts()[parentIndex2].getChild().add(index);
						}
						//System.out.println();
					}
				}
			}
		}
	}



	public ArrayList<int[]> findUserPref(){
		
		ArrayList<int[]> userPref = new ArrayList<int[]>();
		NodeList prefrenceStatement = doc.getElementsByTagName("PREFERENCE-STATEMENT");
		for (int temp = 0; temp < prefrenceStatement.getLength(); temp++) {
			Node nNode = prefrenceStatement.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String child = eElement.getElementsByTagName("PREFERENCE-VARIABLE").item(0).getTextContent();
				int childIndex = Integer.valueOf(child.substring(1, child.length()));
				if(atts.contains(childIndex)){
					int[] attPrefrence = new int[PreProcess.NUM_OF_ATTRIBUTES];
					for(int i=0; i<attPrefrence.length; i++){
						attPrefrence[i] = -1;
					}
					attPrefrence[0] = childIndex;
					NodeList condition = eElement.getElementsByTagName("CONDITION");
					for(int j =0; j<condition.getLength(); j++){
						Node n = condition.item(j);
						String[] ss = n.getTextContent().split("=");
						int parentIndex = Integer.valueOf(ss[0].substring(1, ss[0].length()));
						if(atts.contains(parentIndex)){
							attPrefrence[parentIndex] =  Integer.valueOf(ss[1]);	
						}
					}
					NodeList prefrence = eElement.getElementsByTagName("PREFERENCE");
					Node n = prefrence.item(0);
					String[] ss = n.getTextContent().split(":");
					attPrefrence[childIndex] = Integer.valueOf(ss[0]);
					userPref.add(attPrefrence);
					for(int q=0; q<attPrefrence.length;q++){
						System.out.print(attPrefrence[q] +", ");
					}
					System.out.println("----------------------");
				}
			}
		}
		/*for(int i=0; i<userPref.size(); i++){
			for(int j=0; j<userPref.get(i).length; j++){
				System.out.print(userPref.get(i)[j]+ " ,");
			}
			System.out.println();
		}*/
		//userPref = summerizeUserPref(userPref);
		userPref = summerizeUserPref2(userPref);
		/*for(int i=0; i<userPref.size(); i++){
			for(int j=0; j<userPref.get(i).length; j++){
				System.out.print(userPref.get(i)[j]+",,");
				
			}
			System.out.println();
		}*/

		return userPref;
	}
	
	public ArrayList<int[]> summerizeUserPref(ArrayList<int[]> userPref){
		int num1 = 0;
		int num2 = 0;
		ArrayList<int[]> li = new ArrayList<int[]>();
		for(int i=0; i<userPref.size(); i++){
			boolean flag = false;
			int indexOfAtt = userPref.get(i)[0];
			int valueOfAtt = userPref.get(i)[indexOfAtt]; 
			if(valueOfAtt == 1)
				num1++;
			else if(valueOfAtt == 2)
				num2++;
			if(li.size() == 0){
				li.add(userPref.get(i));
				continue;
				}
			outerloop:
			for(int j=0; j<li.size(); j++){
				for(int z=0; z<userPref.get(i).length; z++){
					if(z == indexOfAtt)
						continue;
					if(z == userPref.get(i).length-1 && userPref.get(i)[z] == li.get(j)[z]){
						flag = true;
						break outerloop;
						}
					if(userPref.get(i)[z] != li.get(j)[z]){
						flag = false;
						break;
						
					}
				}
			}
			if(!flag){
				li.add(userPref.get(i));
				int preIndex = li.get(li.size()-2)[0];
				if(num1 > num2)
					li.get(li.size()-2)[preIndex] = 1;
	
				else if(num2 > num1)
					li.get(li.size()-2)[preIndex] = 2;
				else {
					li.get(li.size()-2)[preIndex] = (random.nextInt() % 2 == 0) ? 1 : 2;
				}
					
				num1 = 0; 
				num2 = 0;
				
					}
		}
		int preIndex = li.get(li.size()-1)[0];
		if(num1 > num2)
			li.get(li.size()-1)[preIndex] = 1;

		else if(num2 > num1)
			li.get(li.size()-1)[preIndex] = 2;
		else {
			li.get(li.size()-1)[preIndex] = (random.nextInt() % 2 == 0) ? 1 : 2;
		}
		
		for(int x = 0; x<li.size(); x++){
			for(int y=0; y<li.get(x).length; y++){
				System.out.print(li.get(x)[y]+ ", ");
			}
			System.out.println();
		}
		return li;
	}
	
	public ArrayList<int[]> summerizeUserPref2(ArrayList<int[]> userPref){
		ArrayList<int[]> li = new ArrayList<int[]>();
		Map<String, int[]> outComeAndAtt = new HashMap<String, int[]>();
		for(int i=0; i<userPref.size(); i++){
			String input = "";
			for(int j=0; j<userPref.get(i).length; j++){
				if(j == userPref.get(i)[0] && j<userPref.get(i).length-1)
					continue;
				if(userPref.get(i)[j] != -1 && j != userPref.get(i)[0])
					input = input + ""+j+","+userPref.get(i)[j]+",";
				if(j== userPref.get(i).length-1){
					int valueOfAtt = userPref.get(i)[userPref.get(i)[0]];
					if(outComeAndAtt.containsKey(input)){
						int[] array = outComeAndAtt.get(input);
						if(valueOfAtt == 2)
							array[1]++;
						else if(valueOfAtt == 1)
							array[0]++;
						outComeAndAtt.put(input, array);
					}
					else{
						int[] array = new int[2];
						if(valueOfAtt == 2)
							array[1]++;
						else if(valueOfAtt == 1)
							array[0]++;
						outComeAndAtt.put(input, array);
					}
				}
					
			}
		}
		ArrayList<String> stringUserPref = new ArrayList<String>(outComeAndAtt.keySet());
		for(int i=0; i<stringUserPref.size(); i++){
			int[] outPut = new int[PreProcess.NUM_OF_ATTRIBUTES];
			for(int j=0; j<outPut.length; j++){
				outPut[j] = -1;
			}
			int[] pref = outComeAndAtt.get(stringUserPref.get(i));
			int max = Math.max(pref[0], pref[1]);
			String[] arrayPref = stringUserPref.get(i).split(",");
			for(int z=0; z<arrayPref.length; z+=2){
				outPut[Integer.valueOf(arrayPref[z])] = Integer.valueOf(arrayPref[z+1]);
			}
			if(max == pref[0] && pref[0] != pref[1])
				outPut[Integer.valueOf(arrayPref[1])] = 1;
			else if (max == pref[1] && pref[0] != pref[1])
				outPut[Integer.valueOf(arrayPref[1])] = 2;
			else if(pref[0] == pref[1])
				outPut[Integer.valueOf(arrayPref[1])] = (random.nextInt() % 2 == 0) ? 1 : 2;
			li.add(outPut);
			for(int q=0; q<outPut.length; q++){
				System.out.print(outPut[q]+", ");
			}
			System.out.println("@@@@@@@@@@");
		}
		return li;
	}
}

