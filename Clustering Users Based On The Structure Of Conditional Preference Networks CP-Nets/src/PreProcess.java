import java.io.IOException;

import javax.sql.rowset.spi.SyncFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class PreProcess {
	
	public final static int NUM_OF_ATTRIBUTES = 7;
	//private WriteExcel wrExcel ;
	
	public PreProcess() throws IOException {
		//this.wrExcel = new WriteExcel("Weight.xlsx","Distance.xlsx");
	}
	
	
	/*public WriteExcel getWrExcel() {
		return wrExcel;
	}*/


	public void process(String fileName) throws ParserConfigurationException, SAXException, IOException {
		
		Attribute.clearAll();
		Parser parser = new Parser(fileName);
		parser.findAttributeAndParent();		
		Attribute.findChildList();
		
		for(int i=1; i < NUM_OF_ATTRIBUTES; i++){
			//System.out.println(Attribute.getAtts()[i].getName());
			if(!Attribute.getChildList().contains(Attribute.getAtts()[i].getIndex()))
					Attribute.getAtts()[i].calcWieght();
		}
		
			//Attribute.setImportance(); //should be uncommented in regular evaluation
		/*	
		
		for(int j=1 ;j < Test.K;j++){
			System.out.println("Attributename " +Attribute.getAtts()[j].getName()+ " "+ Attribute.getAtts()[j].getWeight() );
			for(int i=0; i<Attribute.getAtts()[j].getChild().size(); i++){
				//System.out.println(x);
				System.out.println("child name"+Attribute.getAtts()[j].getChild().get(i));
			}
		}*/
		String m =fileName.substring(23, 27); 
		User user = new User(fileName.substring(23, 27), Attribute.getAtts());
		//wrExcel.writeWeightsInExcelNew();
		//wrExcel.writeWeightsInExcel(fileName.substring(23, 27), Attribute.getAtts());
	}
}
