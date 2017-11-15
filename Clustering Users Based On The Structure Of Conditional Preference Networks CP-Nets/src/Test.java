import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Test {
	
	WriteExcel wr ;
	public final static int NUMBEROFUSER = 15;
	
	
	
	public Test(){
		try {
			wr = new WriteExcel("Weight.xlsx","Distance.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String [] args){
		File folder = new File("examples");
		File[] listOfFiles = folder.listFiles();
		Test mina  = new Test();
		for (File file : listOfFiles) {
		    if (file.isFile() && file.getName().contains(".xml")) {
		        try {
		        	System.out.println("Processing "+file.getPath());
		        	mina.process(file.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}
		mina.calcSaveDistance();
	}
	public void calcSaveDistance(){
		//wr.calcSaveDistance();
	}
	public void process(String fileName) throws ParserConfigurationException, SAXException, IOException {
		//Hashmap[String name, Attribute atts[]]
		Attribute.clearAll();
		Parser parser = new Parser(fileName);
		//parser.findAttributes();
		parser.findAttributeAndParent();
		//ArrayList<Attribute> attributes = parser.getAttributes();
		
		Attribute.findChildList();
		
		for(int i=1; i < NUMBEROFUSER; i++){
			//System.out.println(Attribute.getAtts()[i].getName());
			if(!Attribute.getChildList().contains(Attribute.getAtts()[i].getIndex()))
					Attribute.getAtts()[i].calcWieght();
		}
		
			Attribute.setImportance();
			//Map<String, int[]> userNameAndImportantAttributes = new HashMap<String, int[]>();
			//userNameAndImportantAttributes.put("Mina", importantAttributesWeight);
		/*	
		
		for(int j=1 ;j < Test.K;j++){
			System.out.println("Attributename " +Attribute.getAtts()[j].getName()+ " "+ Attribute.getAtts()[j].getWeight() );
			for(int i=0; i<Attribute.getAtts()[j].getChild().size(); i++){
				//System.out.println(x);
				System.out.println("child name"+Attribute.getAtts()[j].getChild().get(i));
			}
		}*/
		
		
		
		//wr.writeWeightsInExcel(fileName.substring(23, 27), Attribute.getAtts());
		

		//InsertImportantAtt.writeAttributeName();
		//InsertImportantAtt.writeAttributeWeight("\nmina\t");

	}

}
