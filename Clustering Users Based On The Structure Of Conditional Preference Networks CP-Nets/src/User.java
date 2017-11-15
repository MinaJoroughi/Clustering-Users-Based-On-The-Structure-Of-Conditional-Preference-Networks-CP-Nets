import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class User {
	
	private String userName;
	private Attribute[] atts;
	private List<int[]> userPrefTable;
	private static int maxWeight;
	
	private static Map<String, int[]> usersAndWeight = new HashMap<String, int[]>();
	private static Map<String, int[]> usersAndNormalizedWeight = new HashMap<String, int[]>();
	private static Map<String, int[]> usersAndBestOutcome = new HashMap<String, int[]>();
	private static Map<String, ArrayList<int[]>> usersAndPrefrences = new HashMap<String, ArrayList<int[]>>();
	private static Map<String, ArrayList<int[]>> usersAndPrefrencesForAllImpAtt = new HashMap<String, ArrayList<int[]>>();
	private static Map<String, int[]> usersAndWeightforFinalOutcome = new HashMap<String, int[]>();
	private static Map<String, ArrayList<Integer>> usersAndCommonAttforwithCommonCluster = new HashMap<String, ArrayList<Integer>>(); 
	private static Map<String, Integer[]> usersAndSortedWeightIndex = new HashMap<String, Integer[]>();
	private static Map<String, Integer[]> usersAndSortedNonNormalizedWeightIndex = new HashMap<String, Integer[]>();
	private static Map<String, ArrayList<Integer>> usersAndAtts = new HashMap<String, ArrayList<Integer>>();
	private static Map<String, int [][]> usersAndOutcomes = new HashMap<String, int[][]>();
	private static Map<String, double []> usersAndAttributeAverageWeightBasedOnOutcomeWeights = new HashMap<String, double[]>();
	



	public User(String userName, Attribute[] atts) {
		this.userName = userName;
		this.atts = atts;
		setUsersAndWeight(userName, atts);
		
	}
	
	public static void putuUersAndCommonAttforwithCommonCluster(String userName, ArrayList<Integer> commonAtt){
		usersAndCommonAttforwithCommonCluster.put(userName, commonAtt);
	}
	
	public static Map<String, ArrayList<Integer>> getUersAndCommonAttforwithCommonCluster(){
		return usersAndCommonAttforwithCommonCluster;
	}
	
	
	public static void putusersAndWeightforFinalOutcome(String userName, int[]weight){
		usersAndWeightforFinalOutcome.put(userName, weight);
	}
	
	public static Map<String, int[]> getusersAndWeightforFinalOutcome(){
		return usersAndWeightforFinalOutcome;
	}
	
	public static void putUsersAndPrefrences(String userName, ArrayList<int[]>userPref){
		usersAndPrefrences.put(userName, userPref);
	}
	
	public static ArrayList<int[]> getUsersAndPrefrences(String userName){
		return usersAndPrefrences.get(userName);
	}
	
	public static ArrayList<int[]> getUsersAndPrefrencesForAllImpAtt(String userName){
		return usersAndPrefrencesForAllImpAtt.get(userName);
	}
	
	
	public static Map<String, int[]> getUsersAndWeight() {
		return usersAndWeight;

	}
	
	public static Map<String, int[]> getUsersAndNormalizedWeight() {
		return usersAndNormalizedWeight;
	}
	
	
	public static Map<String, double[]> getUsersAndAttributeAverageWeightBasedOnOutcomeWeights() {
		return usersAndAttributeAverageWeightBasedOnOutcomeWeights;
	}
	
	public static Map<String, int [][]> getusersAndOutcomes() {
		return usersAndOutcomes;
	}



	public void setUsersAndWeight(String userName, Attribute[] atts){
		
		int [] data = new int[atts.length];
		int maxForUser = 0; 
		for(int i=1; i<Attribute.getNumAtts(); i++){
			maxWeight = Math.max(maxWeight, atts[i].getWeight());
			//System.out.println("maxweight is"+ maxWeight);
			maxForUser = Math.max(maxForUser, atts[i].getWeight());
			data[i-1] = atts[i].getWeight();
		}
			data[data.length-1] = maxForUser;
			//System.out.println("maxnumber for group is "+maxForUser);
			usersAndWeight.put(userName, data);

	}
	
	public static void findUsersAndAtts(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int[] userAttWeight = usersAndWeight.get(userName.get(i));
			ArrayList<Integer> attsName = new ArrayList<>();
			for(int j=0; j<userAttWeight.length-1; j++){
				if(userAttWeight[j] != 0){
					attsName.add(j+1);
				}
			}
			usersAndAtts.put(userName.get(i), attsName);
		}
	}
	
	public static void defineOutcomeBasedOnImpAtt(){
		
		findUsersAndAtts();
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			ArrayList<Integer> atts = usersAndAtts.get(userName.get(i));
			int[][] OutcomeCombination = Cluster.clusterOutcomes(atts);
			usersAndOutcomes.put(userName.get(i), OutcomeCombination);
		}	
	}
	
	
	public static void normalizeWeights(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int weight[] = usersAndWeight.get(userName.get(i));
			//System.out.println("name"+userName.get(i));
			int normalizedWeight[] = new int [weight.length-1];
			int maxForUser = weight[weight.length-1];
			for(int j=0; j<normalizedWeight.length; j++){
				normalizedWeight[j] = (weight[j]*maxWeight)/maxForUser;
				//System.out.println(normalizedWeight[j]);
			}
			/*System.out.println(userName.get(i));
			for(int z = 0; z<normalizedWeight.length; z++){
				System.out.println(normalizedWeight[z]);
			}*/
			usersAndNormalizedWeight.put(userName.get(i), normalizedWeight);
		
		}
		
	}
	
	public static void findWeigthforUserOutcomes(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int[] weight = usersAndWeight.get(userName.get(i));
			ArrayList<int[]> userPref = usersAndPrefrencesForAllImpAtt.get(userName.get(i));
			int[][] userOutcomes = usersAndOutcomes.get(userName.get(i));
			
			for(int j=0; j<userPref.size(); j++){
				Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
				for(int z=1; z<userPref.get(j).length; z++){
					if(userPref.get(j)[z] == -1)continue;
					indexPref.put(z, userPref.get(j)[z]);
				}
				ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
				for(int w=0; w<userOutcomes.length; w++){
					for(int q=0; q<prefrence.size(); q++){
						if(userOutcomes[w][prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
						if(q == prefrence.size()-1){
							int index = userPref.get(j)[0];
							int attWeight = weight[index-1];
							userOutcomes[w][0] += attWeight;
						}
					}

				}	

			}
			usersAndOutcomes.put(userName.get(i), userOutcomes);
			
		}
		
	}
	
	public static void NormalizeOutcomeWeights() throws IOException{
		FileWriter writer = new FileWriter("UserPref.txt", true);
		 
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			writer.write("\r\n");
			 writer.write(userName.get(i));
			double maxOutcomeWeight = 0; 
			for(int j=0 ; j<usersAndOutcomes.get(userName.get(i)).length; j++){
				if(usersAndOutcomes.get(userName.get(i))[j][0] > maxOutcomeWeight)
					maxOutcomeWeight = usersAndOutcomes.get(userName.get(i))[j][0];
			}
			for(int j=0 ; j<usersAndOutcomes.get(userName.get(i)).length; j++){
					double weight =  usersAndOutcomes.get(userName.get(i))[j][0]/maxOutcomeWeight;
			            writer.write("\r\n");   // write new line
			            for(int q = 0; q< usersAndOutcomes.get(userName.get(i))[j].length; q++)
			            	writer.write(usersAndOutcomes.get(userName.get(i))[j][q]+""+"\t");
			            writer.write((Math.round(weight * 100.0) / 100.0)+"");
			            	
			}
			
		}
		 writer.close();
	}
	
	
	public static void findNormalizedWeightforUserOutcomes(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int[] weight = usersAndNormalizedWeight.get(userName.get(i));
			ArrayList<int[]> userPref = usersAndPrefrencesForAllImpAtt.get(userName.get(i));
			int[][] userOutcomes = usersAndOutcomes.get(userName.get(i));
			for(int j=0; j<userOutcomes.length; j++)
				userOutcomes[j][0] = 0;
			
			for(int j=0; j<userPref.size(); j++){
				Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
				for(int z=1; z<userPref.get(j).length; z++){
					if(userPref.get(j)[z] == -1)continue;
					indexPref.put(z, userPref.get(j)[z]);
				}
				ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
				for(int w=0; w<userOutcomes.length; w++){
					for(int q=0; q<prefrence.size(); q++){
						if(userOutcomes[w][prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
						if(q == prefrence.size()-1){
							int index = userPref.get(j)[0];
							int attWeight = weight[index-1];
							userOutcomes[w][0] += attWeight;
						}
					}

				}	

			}
			usersAndOutcomes.put(userName.get(i), userOutcomes);
			
		}
	}
	
	public static void findAttributeAverageWeightBasedOnOutcomeWeight(){
		ArrayList<String> userName = new ArrayList<String>(usersAndAtts.keySet());
		
		for(int i=0; i<userName.size(); i++){
			int[][] outComes = usersAndOutcomes.get(userName.get(i));
			int maxWeightForOutCome = 0;
			for(int j=0; j<outComes.length; j++){
				if(maxWeightForOutCome < outComes[j][0])
					maxWeightForOutCome = outComes[j][0];
			}
			
			int[] weight1 = new int[PreProcess.NUM_OF_ATTRIBUTES]; 
			int[] weight2 = new int[PreProcess.NUM_OF_ATTRIBUTES]; 
			for(int j=0; j<outComes.length; j++){
				for(int z=1; z<outComes[j].length; z++){
					if(outComes[j][z] == 0)
						continue; 
					else if(outComes[j][z] == 1)
						weight1[z] += maxWeightForOutCome - outComes[j][0];
					else if(outComes[j][z] == 2)
						weight2[z] += maxWeightForOutCome - outComes[j][0];
				}
			}
			
			int ave = 0; 
			int min = 0;
			boolean flag = true;
			int count = 0; 
			for(int j=0; j<weight1.length; j++){
				weight1[j] = weight1[j]/(outComes.length/2);
				weight2[j] = weight2[j]/(outComes.length/2);
				ave += weight1[j]+weight2[j];
				if(weight1[j] != 0 && flag){
					min = weight1[j];
					flag = false;
				}
				if(weight1[j]!= 0){
					min = Math.min(min, Math.min(weight1[j], weight2[j]));
					count++;
				}
			
			}
			ave = ave/(count * 2); // Because we have two different values for each attribute
			double [] weight = new double[PreProcess.NUM_OF_ATTRIBUTES];
			for(int j=0; j<weight1.length; j++){
				if(weight1[j] == 0 && weight2[j] == 0)
					continue;
				int minWeight = Math.min(weight1[j], weight2[j]);
				if(minWeight == weight1[j]){
					double temp = Math.abs((double)(minWeight - ave) / (ave - min));
					weight[j] = (double)Math.round(temp * 1000) / 1000;
					
				}
				
				else if(minWeight == weight2[j]){
					double temp = Math.abs((double)(minWeight - ave) / (ave - min));
					weight[j] =  -1 * (double)Math.round(temp * 1000) / 1000;
						
				}	
				
			}
			usersAndAttributeAverageWeightBasedOnOutcomeWeights.put(userName.get(i), weight);
			
		}
		
	}
	
	public static void sorUsersAndNormilizedWeight(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndNormalizedWeight.keySet());

		for(int i=0; i<userName.size(); i++){
			int maxLenght = 0;
			for(int j=0; j<usersAndNormalizedWeight.get(userName.get(i)).length; j++){ 
				String weight = ""+usersAndNormalizedWeight.get(userName.get(i))[j];
				if(weight.length() > maxLenght){
					maxLenght = weight.length();
				}
			}
			String[] normalizedWeight = new String[usersAndNormalizedWeight.get(userName.get(i)).length];
			for(int j=0; j<normalizedWeight.length; j++){
				String weight = ""+usersAndNormalizedWeight.get(userName.get(i))[j];
				if(weight.length() < maxLenght){
					int difference = maxLenght - weight.length();
					for(int z=0; z<difference; z++){
						weight = "0"+weight;
					}
				}
				normalizedWeight[j] = weight;
			}
			MyTest comparator = new MyTest(normalizedWeight);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			usersAndSortedWeightIndex.put(userName.get(i), indexes);
		}
	}
	
public static void sortUsersAndWeight(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());

		for(int i=0; i<userName.size(); i++){
			int maxLenght = 0;
			for(int j=0; j<usersAndWeight.get(userName.get(i)).length; j++){ 
				String weight = ""+usersAndWeight.get(userName.get(i))[j];
				if(weight.length() > maxLenght){
					maxLenght = weight.length();
				}
			}
			String[] normalizedWeight = new String[usersAndWeight.get(userName.get(i)).length - 1];
			for(int j=0; j<normalizedWeight.length; j++){
				String weight = ""+usersAndWeight.get(userName.get(i))[j];
				if(weight.length() < maxLenght){
					int difference = maxLenght - weight.length();
					for(int z=0; z<difference; z++){
						weight = "0"+weight;
					}
				}
				normalizedWeight[j] = weight;
			}
			MyTest comparator = new MyTest(normalizedWeight);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			usersAndSortedNonNormalizedWeightIndex.put(userName.get(i), indexes);
		}
	}
	
	public static void findUsersPrefOverAllImpAtt() throws ParserConfigurationException, SAXException, IOException{
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int[] weights = usersAndWeight.get(userName.get(i));
			ArrayList<Integer> impAtts = new ArrayList<>();
			for(int j=0; j<weights.length - 1; j++){
				if(weights[j] != 0)
					impAtts.add(j+1);	
			}
			Parser parse = new Parser("examples/cpnet_n06c3d2_"+userName.get(i)+".xml",impAtts);
			System.out.println("reading file" + "cpnet_n06c3d2_"+userName.get(i)+".xml for finding best outcome");
			ArrayList<int[]> userPref = parse.findUserPref();
			usersAndPrefrencesForAllImpAtt.put(userName.get(i), userPref);
				
		}		
	}
	
	public static void findUsersBestOutcome(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		
		for(int i=0; i<userName.size(); i++){ 
			Map<String, Integer> attsAndValues = new HashMap<String, Integer>();
			
			for(int j=1; j<Attribute.getNumAtts(); j++){
				attsAndValues.put("x"+j, -1);
			}
			
			ArrayList<int[]> userPref = usersAndPrefrencesForAllImpAtt.get(userName.get(i));
			Integer[] sortedWeightIndex = usersAndSortedNonNormalizedWeightIndex.get(userName.get(i));
			for(int j = sortedWeightIndex.length-1; j >= 0; j--){
				
				int attName = sortedWeightIndex[j];
				if(usersAndWeight.get(userName.get(i))[attName] == 0)
					break;
				if(userPref.size() == 0)
					break; 
				for(int z=0; z<userPref.size(); z++){
					
					if(userPref.get(z)[0] == attName + 1){
						Map<String, Integer> attsAndTempValues = new HashMap<String, Integer>();
						for(int q=1; q<userPref.get(z).length; q++){
							
							attsAndTempValues.put("x"+q, userPref.get(z)[q]);
						}
						
						Map<String, Integer> changedAttsAndValues = new HashMap<String, Integer>();

						for(int q=1; q<=attsAndTempValues.size(); q++){
							
							ArrayList<String> changedAttName = new ArrayList<String>(changedAttsAndValues.keySet()); 
							if(q == attsAndTempValues.size()){
								
								if(attsAndValues.get("x"+q) == attsAndTempValues.get("x"+q) ||
										attsAndValues.get("x"+q) != -1 && attsAndTempValues.get("x"+q) == -1){
									
									attsAndValues.put(changedAttName.get(0),attsAndTempValues.get(changedAttName.get(0)));
									userPref.remove(z);
									z--;
									break;
								}
								
								else if(attsAndValues.get("x"+q) != -1 && attsAndTempValues.get("x"+q) != -1){
									
									userPref.remove(z);
									z--;
									break;
								}
								
								else if(attsAndValues.get("x"+q) == -1 && attsAndTempValues.get("x"+q) != -1){
									//changedAttsAndValues.put("x"+q,attsAndTempValues.get("x"+q));
									attsAndValues.put("x"+q,attsAndTempValues.get("x"+q));
									userPref.remove(z);
									z--;
									break;
								}
							}
							
							if( attsAndValues.get("x"+q) == attsAndTempValues.get("x"+q))
								continue; 
							else if(attsAndValues.get("x"+q) != -1 && attsAndTempValues.get("x"+q) == -1)
								continue;
							else if(attsAndValues.get("x"+q) != -1 && attsAndTempValues.get("x"+q) != -1){
								userPref.remove(z);
								z--;
								break;
							}
							else if(attsAndValues.get("x"+q) == -1 && attsAndTempValues.get("x"+q) != -1){
								changedAttsAndValues.put("x"+q,attsAndTempValues.get("x"+q));
								continue; 
							}		
						}
					}
				}	
			}
			int[] bestOutcome = new int[attsAndValues.size() + 1];
			for(int g=1; g<=attsAndValues.size(); g++){
				bestOutcome[g] = attsAndValues.get("x"+g);
			}
			usersAndBestOutcome.put(userName.get(i), bestOutcome);
		}
	}
	
	public static void modifyWeightsBasedonUserBestOutcome(){
		
		ArrayList<String> userName = new ArrayList<String>(usersAndWeight.keySet());
		for(int i=0; i<userName.size(); i++){
			int [] bestOutcome = usersAndBestOutcome.get(userName.get(i));
			int [] weight = usersAndWeight.get(userName.get(i));
			for(int j=1; j<bestOutcome.length; j++){
				if(bestOutcome[j] == 1){
					int weightAtt = weight[j - 1];
					weight[j - 1] = -weightAtt;
				}
			}
			usersAndWeight.put(userName.get(i), weight);
		}
	}
	
	public static void findBaselineOutcome() throws ParserConfigurationException, SAXException, IOException{

		Map<String, int[]> attsAndValues = new HashMap<String, int[]>();
		for(int i=1; i<Attribute.getNumAtts(); i++){
			attsAndValues.put("x"+i, new int[3]);
		}

		boolean flag = true;
		int num = 0;
		ArrayList<String> userName = new ArrayList<String>(usersAndNormalizedWeight.keySet());
		ArrayList<String> attNames = new ArrayList<String>(attsAndValues.keySet());
		while(flag){
			for(int i=0; i<userName.size(); i++){
				int index = usersAndSortedWeightIndex.get(userName.get(i)).length - 1 - num;
				ArrayList<Integer> commonAtts = new ArrayList<>();
				ArrayList<int[]> userPref = new ArrayList<>();
				int att = usersAndSortedWeightIndex.get(userName.get(i))[index];
				int attName = att+1;
				if(attsAndValues.get("x"+attName)[2] == 0){
					commonAtts.add(attName) ;
					Parser parse = new Parser("examples/cpnet_n06c4d2_"+userName.get(i)+".xml",commonAtts);
					System.out.println("reading file" + "cpnet_n06c4d2_"+userName.get(i)+".xml");		
					userPref = parse.findUserPref();
				}
				if(userPref.size() == 1){
					int value = userPref.get(0)[attName];
					if(value == 1){
						int weight = usersAndNormalizedWeight.get(userName.get(i))[att]
								+ attsAndValues.get("x"+attName)[0];
						int call = attsAndValues.get("x"+attName)[1] +1;
						int[] temp = {weight,call, 0}; 
						attsAndValues.put("x"+attName, temp);
					}
					else if(value == 2){
						int weight = -1 * usersAndNormalizedWeight.get(userName.get(i))[att]
								+ attsAndValues.get("x"+attName)[0];
						int call = attsAndValues.get("x"+attName)[1] +1;
						int[] temp = {weight,call, 0}; 
						attsAndValues.put("x"+attName, temp);
					}	
				}	
				else{
					for(int z=0; z<userPref.size(); z++){
						Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
						for(int j=1; j<userPref.get(z).length; j++){
							if(userPref.get(z)[j] == -1)continue;
							indexPref.put(j, userPref.get(z)[j]);
						}
						ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
						for(int q=0; q<prefrence.size(); q++){
							if(attsAndValues.get("x"+prefrence.get(q))[2] == 1 
									&& attsAndValues.get("x"+prefrence.get(q))[0] > 0 
									&& indexPref.get(prefrence.get(q)) != 1) break;
							if(attsAndValues.get("x"+prefrence.get(q))[2] == 1 && 
									attsAndValues.get("x"+prefrence.get(q))[0] < 0 
									&& indexPref.get(prefrence.get(q)) != 2) break;
							if(q == prefrence.size()-1){
								int indexAtt = userPref.get(z)[0];
								int value = indexPref.get(indexAtt);
								if(value == 1){
									int weight = usersAndNormalizedWeight.get(userName.get(i))[att]
											+ attsAndValues.get("x"+attName)[0];
									int call = attsAndValues.get("x"+attName)[1] +1;
									int[] temp = {weight,call, 0}; 
									attsAndValues.put("x"+attName, temp);
								}
								else if(value == 2){
									int weight = -1 * usersAndNormalizedWeight.get(userName.get(i))[att]
											+ attsAndValues.get("x"+attName)[0];
									int call = attsAndValues.get("x"+attName)[1] +1;
									int[] temp = {weight,call, 0}; 
									attsAndValues.put("x"+attName, temp);
								}	
							}
						}
					}
				}
			}
			for(String Name : attNames){
				if(attsAndValues.get(Name)[1] != 0){
					int weight = attsAndValues.get(Name)[0];
					int call = attsAndValues.get(Name)[1];
					int[] temp = {weight,call, 1};
					attsAndValues.put(Name, temp);
				}		
			}
			for(String Name : attNames){
				if(attsAndValues.get(Name)[2] != 0)
					flag = false;
				else{
					flag = true; 
					break;
				}
			}
			num++;
		}
		for(int i=0; i<attNames.size(); i++){
			if(attsAndValues.get(attNames.get(i))[1] > 0){
				if(attsAndValues.get(attNames.get(i))[0] > 0){
					System.out.println("the baseline value for arr "+ attNames.get(i) +
							" is " + 1);
				}
				else if(attsAndValues.get(attNames.get(i))[0] < 0){
					System.out.println("the baseline value for arr "+ attNames.get(i) +
							" is " + 2);
				}
				else{
					System.out.println("the baseline value for arr "+ attNames.get(i) +
							" is undefined");
				}
				
			}
		}
	}
	
	public static void findAccuracyForFinalOutcome(){
		ArrayList<String> userName = new ArrayList<String>(usersAndWeightforFinalOutcome.keySet());
		int no = 0;
		int yes = 0;
		for(int i=0; i<userName.size(); i++){
			int[] ss = usersAndWeightforFinalOutcome.get(userName.get(i));
			if(ss[0] > (ss[1]/2))
				yes++;
			else
				no++;
		}
		System.out.println("yes  ="+yes);
		System.out.println("no  ="+no);
	}
	

	
	
	/*public static void findUserWeightPrefoverOutcome(String clusterNum, String userName, ArrayList<Integer>commonAtts){
		int[] weight = usersAndNormalizedWeight.get(userName);
		String[] weightStr = new String[weight.length];
		for(int i=0; i<weight.length; i++)
			weightStr[i] = ""+weight[i];
		MyTest comparator = new MyTest(weightStr);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		//for(int i=0; i<indexes.length; i++){
			//System.out.println(indexes[i] + "   $$" + weight[indexes[i]]);
		for(int i=indexes.length-1; i>-1; i--){
			if(commonAtts.contains(indexes[i])){
				int attWeight = weight[indexes[i]];
				ArrayList<int[]> userPref = usersAndPrefrences.get(userName);
				for(int j=0; j<userPref.size(); j++){
					Map<Integer, Integer> prefrence = new HashMap<Integer, Integer>();
					if(userPref.get(j)[0] == indexes[i]){
						for(int z=1; z<userPref.get(j).length; z++){
							if(userPref.get(j)[z] != -1){
								prefrence.put(z, userPref.get(j)[z]);
								Cluster.giveWeightforOutcome(clusterNum, indexes[i], attWeight, prefrence);
							}
						}
					}
				}	
			}
		}
	}*/
}
