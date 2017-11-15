
public class Similarity {

	public Similarity() {
		
	}
	
	
	public double findCosinSim(int[] firstArry, int[] secondArray){
		double count = 0;
		double firstArrayLenght = 0;
		double SecondArrayLenght = 0;
		for(int j=0; j<Attribute.getNumAtts()-1; j++){
			count += firstArry[j]*secondArray[j];
			firstArrayLenght  += Math.pow(firstArry[j], 2);
			SecondArrayLenght +=  Math.pow(secondArray[j], 2);
		}
		double similarity = count / (Math.sqrt(firstArrayLenght) * Math.sqrt(SecondArrayLenght));
		double roundOff = (double) Math.round((similarity + 1) * 1000) / 1000;
		return roundOff;//because in finding the best outcome it gives negative values that we don't want

	}
	
	public double findCosinSimDouble(double[] firstArry, double[] secondArray){
		double count = 0;
		double firstArrayLenght = 0;
		double SecondArrayLenght = 0;
		for(int j=0; j<Attribute.getNumAtts(); j++){
			count += firstArry[j]*secondArray[j];
			firstArrayLenght  += Math.pow(firstArry[j], 2);
			SecondArrayLenght +=  Math.pow(secondArray[j], 2);
		}
		double similarity = count / (Math.sqrt(firstArrayLenght) * Math.sqrt(SecondArrayLenght));
		double roundOff = (double) Math.round((similarity + 1) * 1000) / 1000;
		return roundOff;//because in finding the best outcome it gives negative values that we don't want

	}
	
	
}