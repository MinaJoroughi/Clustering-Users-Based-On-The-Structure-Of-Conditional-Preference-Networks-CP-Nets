import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ReadFolder {
	
	private String folderName;
	private WriteExcel wrExcel ;
	
	
	
	public ReadFolder(String folderName) throws IOException, ParserConfigurationException, SAXException {
		this.folderName = folderName;
		this.wrExcel = new WriteExcel("Weight.xlsx","Distance.xlsx");
		readFileFromFolder();
	}
	
	public void readFileFromFolder() throws IOException, ParserConfigurationException, SAXException{
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		PreProcess process = new PreProcess();
		for (File file : listOfFiles) {
		    if (file.isFile() && file.getName().contains(".xml")) {
		        try {
		        	System.out.println("Processing"+file.getPath());
		        	process.process(file.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}
		User.defineOutcomeBasedOnImpAtt();//Sensitivity Method //CPT //Brute force
		//User.sortUsersAndWeight();//BestOutcome Method
		User.findUsersPrefOverAllImpAtt();//BestOutcome Method //Sensitivity Method //CPT //brute force
		User.findWeigthforUserOutcomes();//Sensitivity Method //CPT //brute force
		User.NormalizeOutcomeWeights();// brute force 
		//User.findAttributeAverageWeightBasedOnOutcomeWeight();//Sensitivity Method
		//User.findUsersBestOutcome();//BestOutcome Method
		//User.modifyWeightsBasedonUserBestOutcome();//BestOutcome Method
		//wrExcel.writeWeightsInExcelNew(); //BestOutcome Method // plain Method
		//wrExcel.writeDoubleWeightsInExcelNew(); // Sensitivity Method
		//wrExcel.calcSaveDistanceInExcel();//BestOutcome Method //Plain Method
		//wrExcel.calcSaveDistanceInExcelForDouble(); //Sensitivity Method
		//User.normalizeWeights(); //should be uncommented in regular evaluation 
	}
}
