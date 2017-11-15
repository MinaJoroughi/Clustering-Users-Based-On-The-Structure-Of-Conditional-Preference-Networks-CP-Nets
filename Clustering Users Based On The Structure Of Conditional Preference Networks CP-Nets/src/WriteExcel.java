import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class WriteExcel {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private int rowCount;
	private String weights,distance;

	private Map<String, int[]> usersAndWeight;
	
	

	public WriteExcel(String weights, String distance) throws IOException {	
		this.weights = weights;
		this.distance = distance;
		this.usersAndWeight = User.getUsersAndWeight();
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet(weights);
		rowCount = 1;
		createColumnForWeihgt();
	}



	public void createColumnForWeihgt() throws IOException{ 
		Row row = sheet.createRow(0);
		for(int i=0; i<Attribute.getNumAtts(); i++){
			Cell cell = row.createCell(i);
			cell.setCellValue("x"+i);
		}
		try (FileOutputStream outputStream = new FileOutputStream(this.weights)) {
			workbook.write(outputStream);
		}
	}
	
	

	/*public void writeWeightsInExcel(String userName, Attribute atts[])throws IOException{
		Row row = sheet.createRow(rowCount++);
		row.createCell(0).setCellValue(userName);
		int [] data = new int[atts.length];
		for(int i=1; i<Attribute.getNumAtts(); i++){
			int cellNum = atts[i].getIndex();
			Cell cell = row.createCell(cellNum);	
			cell.setCellValue(atts[i].getWeight());
			data[i] = atts[i].getWeight();
		}
		this.usersAndWeight.put(userName, data);

		try (FileOutputStream outputStream = new FileOutputStream(this.weights)) {
			workbook.write(outputStream);
		}
	}*/
	
	public void writeWeightsInExcelNew()throws IOException{
		this.rowCount = 1;
		ArrayList<String> userName = new ArrayList<String>(this.usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			Row row = sheet.createRow(rowCount++);
			row.createCell(0).setCellValue(userName.get(i));
			for(int j=1; j<Attribute.getNumAtts(); j++){
				Cell cell = row.createCell(j);
				cell.setCellValue(usersAndWeight.get(userName.get(i))[j-1]);
			}	
		}
		try (FileOutputStream outputStream = new FileOutputStream(this.weights)) {
			workbook.write(outputStream);
		} 
	}
	
	public void writeDoubleWeightsInExcelNew()throws IOException{
		this.rowCount = 1;
		ArrayList<String> userName = new ArrayList<String>(this.usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			Row row = sheet.createRow(rowCount++);
			row.createCell(0).setCellValue(userName.get(i));
			for(int j=1; j<Attribute.getNumAtts(); j++){
				Cell cell = row.createCell(j);
				cell.setCellValue(User.getUsersAndAttributeAverageWeightBasedOnOutcomeWeights().get(userName.get(i))[j]);
			}	
		}
		try (FileOutputStream outputStream = new FileOutputStream(this.weights)) {
			workbook.write(outputStream);
		} 
	}
	
	
	public void calcSaveDistanceInExcel(){
		
		try{
			Similarity simCalc = new Similarity();
			double [][] similarityMatrix = new double[this.usersAndWeight.keySet().size()][this.usersAndWeight.keySet().size()]; 
			ArrayList<String> userName = new ArrayList<String>(this.usersAndWeight.keySet());
			
			for(int i=0; i < userName.size();i++){
				for(int j=0; j < userName.size();j++){
					similarityMatrix[i][j] = simCalc.findCosinSim(this.usersAndWeight.get(userName.get(i)),this.usersAndWeight.get(userName.get(j)));
				}
			}

			// Clear rowCount
			this.rowCount=1;
			// saving to file
			// create headers
			createColumnForSimilarity(userName);
			//create rows

			for(int i=1; i<=userName.size(); i++){
				Row row = sheet.createRow(rowCount++);
				row.createCell(0).setCellValue(userName.get(i-1));
				for(int j=1; j<=userName.size(); j++){
					Cell cell = row.createCell(j);
					cell.setCellValue(similarityMatrix[i-1][j-1]);
				}
			}
			try (FileOutputStream outputStream = new FileOutputStream(this.distance)) {
				workbook.write(outputStream);
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	
	public void calcSaveDistanceInExcelForDouble(){
		
		try{
			Similarity simCalc = new Similarity();
			double [][] similarityMatrix = new double[this.usersAndWeight.keySet().size()][this.usersAndWeight.keySet().size()]; 
			ArrayList<String> userName = new ArrayList<String>(this.usersAndWeight.keySet());
			
			for(int i=0; i < userName.size();i++){
				for(int j=0; j < userName.size();j++){
					similarityMatrix[i][j] = simCalc.findCosinSimDouble(User.getUsersAndAttributeAverageWeightBasedOnOutcomeWeights().get(userName.get(i)),
							User.getUsersAndAttributeAverageWeightBasedOnOutcomeWeights().get(userName.get(j)));
				}
			}

			// Clear rowCount
			this.rowCount=1;
			// saving to file
			// create headers
			createColumnForSimilarity(userName);
			//create rows

			for(int i=1; i<=userName.size(); i++){
				Row row = sheet.createRow(rowCount++);
				row.createCell(0).setCellValue(userName.get(i-1));
				for(int j=1; j<=userName.size(); j++){
					Cell cell = row.createCell(j);
					cell.setCellValue(similarityMatrix[i-1][j-1]);
				}
			}
			try (FileOutputStream outputStream = new FileOutputStream(this.distance)) {
				workbook.write(outputStream);
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public void createColumnForSimilarity(ArrayList<String > userName) throws IOException{ 
		Row row = sheet.createRow(0);

		for(int i=0; i<=userName.size(); i++){
			Cell cell = row.createCell(i);
			if(i==0){
				cell.setCellValue("Names");
			}else{
				cell.setCellValue(userName.get(i-1));
			}
		}
		try (FileOutputStream outputStream = new FileOutputStream(this.distance)) {
			workbook.write(outputStream);
		}
	}
	
	public void calcSaveDistanceInText(){
		
		try{
			Similarity simCalc = new Similarity();
			double [][] similarityMatrix = new double[this.usersAndWeight.keySet().size()][this.usersAndWeight.keySet().size()]; 
			ArrayList<String> userName = new ArrayList<String>(this.usersAndWeight.keySet());
			
			try {
				File file = new File("UsersName.csv");
				if ( ! file.exists() ) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String user: userName){
					bw.write(user+"'"+"\n");
				}
				bw.close();
				System.out.println("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for(int i=0; i < userName.size();i++){
				for(int j=0; j < userName.size();j++){
					similarityMatrix[i][j] = simCalc.findCosinSim(this.usersAndWeight.get(userName.get(i)),this.usersAndWeight.get(userName.get(j)));
				}
			}
			try {
				File file = new File("SourceTargetWeight.csv");
				if ( ! file.exists() ) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.close();
				System.out.println("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}


			for(int i=1; i<=userName.size(); i++){
				Row row = sheet.createRow(rowCount++);
				row.createCell(0).setCellValue(userName.get(i-1));
				for(int j=1; j<=userName.size(); j++){
					Cell cell = row.createCell(j);
					cell.setCellValue(similarityMatrix[i-1][j-1]);
				}
			}
			try (FileOutputStream outputStream = new FileOutputStream(this.distance)) {
				workbook.write(outputStream);
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

}
