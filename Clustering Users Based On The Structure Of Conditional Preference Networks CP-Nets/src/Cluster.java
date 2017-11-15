
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class Cluster {

	private String fileName; 
	private static Map<String, ArrayList<String>> clustersAndUsers = new HashMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<Integer>> clustersAndAtt = new HashMap<String, ArrayList<Integer>>();
	private static Map<String, int [][]> clustersAndOutcomes = new HashMap<String, int[][]>();
	private static Map<String, Integer []> clustersAndSortedIndex = new HashMap<String, Integer[]>();
	private static Map<Integer, ArrayList<String>> attAndCluster = new HashMap<Integer, ArrayList<String>>();
	private static int[] finalOutCome = new int[PreProcess.NUM_OF_ATTRIBUTES];
	//private static Map<String, int[]> usersAndNormalizedWeight = new HashMap<String, int[]>();



	public Cluster(String fileName) {
		this.fileName = fileName;
		ReadFile();
	}



	public static Map<String, ArrayList<String>> getClustersAndUsers() {
		return clustersAndUsers;
	}



	public void ReadFile(){
		try (BufferedReader br = new BufferedReader(new FileReader(this.fileName)))
		{

			br.readLine();
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				String[] line = currentLine.split(",");
				String user = line[0];
				String cluster = line[3];
				//System.out.println("cluster "+cluuster + " user "+ user);
				if(clustersAndUsers.containsKey(cluster))
					clustersAndUsers.get(cluster).add(user);

				else{
					ArrayList<String> userName = new ArrayList<>();
					userName.add(user);
					clustersAndUsers.put(cluster, userName);
				}


			}
			findClusterAndAtts();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}



	public int[][] clacClusterWeight(){

		Map<String, int[]> usersAndNormalizedWeight = User.getUsersAndNormalizedWeight();
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		int[][] clusterAttWeight = new int[clusterNum.size()][PreProcess.NUM_OF_ATTRIBUTES];
		for(int i=0; i<clusterNum.size(); i++){
			ArrayList<String> usersInCluster = new ArrayList<String>();
			usersInCluster = clustersAndUsers.get(clusterNum.get(i));
			int[] clusterWeight = new int[PreProcess.NUM_OF_ATTRIBUTES];
			clusterWeight[0] = Integer.valueOf(clusterNum.get(i));
			for(int j=1; j<=usersInCluster.size(); j++){
				int[] userWeight = usersAndNormalizedWeight.get(usersInCluster.get(j-1));
				for(int z=1; z<clusterWeight.length; z++){
					clusterWeight[z] += userWeight[z-1];
				}
			}
			for(int q=1 ; q<clusterWeight.length; q++)
				clusterWeight[q] = clusterWeight[q]/usersInCluster.size();
				
			clusterAttWeight[i] = clusterWeight;	
		}
		return clusterAttWeight;
	}



	public int[][] calcImportantAttInCluster(){
		int[][] clusterAttWeight = clacClusterWeight();
		int[] maxWeight = new int[clusterAttWeight[0].length-1];
		int[] minWeight = new int[clusterAttWeight[0].length-1];
		for(int j=1; j<clusterAttWeight[0].length; j++){
			int max = clusterAttWeight[0][j];
			int min = clusterAttWeight[0][j];
			for(int i=0; i<clusterAttWeight.length; i++){
				max = Math.max(max, clusterAttWeight[i][j]); 
				min = Math.min(min, clusterAttWeight[i][j]);
			}
			maxWeight[j-1] = max;
			minWeight[j-1] = min;
		}

		for(int j=1; j<clusterAttWeight[0].length; j++){
			double mid = (maxWeight[j-1] + minWeight[j-1])/2;
			for(int i=0; i<clusterAttWeight.length; i++){
				if(clusterAttWeight[i][j] < (mid)){
					clusterAttWeight[i][j] = 0; 
				}
			}
		}
		return clusterAttWeight;
	}


	public void findClusterAndAtts(){
		int[][] clusterAttWeight = calcImportantAttInCluster();
		for(int i=0; i<clusterAttWeight.length; i++){
			ArrayList<Integer> atts = new ArrayList<>();
			for(int j=1; j<clusterAttWeight[0].length; j++){
				if(clusterAttWeight[i][j] != 0){
					atts.add(j);
				}	
			}
			clustersAndAtt.put(""+i, atts);
		}
	}


	public void prepareForRead() throws ParserConfigurationException, SAXException, IOException{
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int i=0; i<clusterNum.size(); i++){
			findClusterOutcomes(clusterNum.get(i));
			ArrayList<String> usersInCluster = new ArrayList<String>();
			ArrayList<Integer> atts = new ArrayList<>();
			usersInCluster = clustersAndUsers.get(clusterNum.get(i));
			atts = clustersAndAtt.get(clusterNum.get(i));
			for(int j=0; j<usersInCluster.size(); j++){
				
				ArrayList<Integer> commonAtts = new ArrayList<>();
				for(int z=0; z<atts.size(); z++){
					int index = atts.get(z);
					if(User.getUsersAndWeight().get(usersInCluster.get(j))[index-1] != 0){
						commonAtts.add(index);
					}
				}
				
			//////First Method for Cleaning CPT////////
				/*if(commonAtts.size() != 0){
					User.putuUersAndCommonAttforwithCommonCluster(usersInCluster.get(j), commonAtts);
					Parser parse = new Parser("examples/cpnet_n12c5d2_"+usersInCluster.get(j)+".xml", commonAtts);
					System.out.println("reading file" + "cpnet_n12c5d2_"+usersInCluster.get(j)+".xml");
					User.putUsersAndPrefrences(usersInCluster.get(j), parse.findUserPref());
					findWeigthforClusterOutcomes(clusterNum.get(i), usersInCluster.get(j));
				}*/
				////End Of First Method//////
				//////Second Method For Cleaning CPT///////
				if(commonAtts.size() != 0){
					User.putuUersAndCommonAttforwithCommonCluster(usersInCluster.get(j), commonAtts);
					User.findNormalizedWeightforUserOutcomes();
					findWeightForClusterOutcomesSecondMethod(clusterNum.get(i), usersInCluster.get(j), commonAtts.size());
				}
				//////End Of Second Method//////////

			}
		}
	}
	
	
	public void findBestOutcomeforCluster(){
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int i=0; i<clusterNum.size(); i++){
			sortOutcomesforCluster(clusterNum.get(i));
		}
	}
	
	public void sortOutcomesforCluster(String clusterNum){
		int[][] clusterOutcomes = clustersAndOutcomes.get(clusterNum);
		String[] outcomeWeight = new String[clusterOutcomes.length];
		int maxLenght = 0;
		for(int i=0; i<clusterOutcomes.length; i++){
			String weight = ""+clusterOutcomes[i][0];
			if(weight.length() > maxLenght) {
				maxLenght = weight.length();
			}
		}
		for(int i=0; i<clusterOutcomes.length; i++){
			String weight = ""+clusterOutcomes[i][0];
			if(weight.length() < maxLenght){
				int difference = maxLenght - weight.length();
				for(int j=0; j<difference; j++){
					weight = "0"+weight;
				}
			}
			outcomeWeight[i] = weight;
		}
		MyTest comparator = new MyTest(outcomeWeight);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		clustersAndSortedIndex.put(clusterNum, indexes);
		/*System.out.println("Clustername" + clusterNum);
		for(int i=0; i<indexes.length; i++){
			System.out.println(indexes[i] + "   " + outcomeWeight[indexes[i]]);
		}*/
	}
	
	public void findCommonAttInClusters(){
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int i=0; i<clusterNum.size(); i++){
			int size = clustersAndSortedIndex.get(clusterNum.get(i)).length;
			int index = clustersAndSortedIndex.get(clusterNum.get(i))[size-1];
			int[] outcome = clustersAndOutcomes.get(clusterNum.get(i))[index];
			for(int j=1; j<outcome.length; j++){
				if(outcome[j] == 0)
					continue;
				if(attAndCluster.containsKey(j)){
					ArrayList<String> temprory = attAndCluster.get(j);
					temprory.add(clusterNum.get(i));
					attAndCluster.put(j, temprory);
				}
				else{
					ArrayList<String> temprory = new ArrayList<>();
					temprory.add(clusterNum.get(i));
					attAndCluster.put(j, temprory);
				}		
			}
		}
	}

	public void findClusterOutcomes(String clusterName){

		ArrayList<Integer> atts = clustersAndAtt.get(clusterName);
		int[][] OutcomeCombination = clusterOutcomes(atts);
		clustersAndOutcomes.put(clusterName, OutcomeCombination);			

	}

	public static int[][] clusterOutcomes(ArrayList<Integer> atts){
		int difCom = (int)Math.pow(2, atts.size());
		int[][] OutcomeCombination = new int[difCom][PreProcess.NUM_OF_ATTRIBUTES];
		for(int i=0; i<difCom; i++){
			String combination = Integer.toBinaryString(i);
			if(combination.length()<atts.size()){
				while(combination.length() < atts.size()){
					combination = "0"+combination;
				}
			}
			for(int j=0; j<combination.length(); j++){
				int val = Integer.valueOf(combination.substring(j, j+1));
				int index = atts.get(j);
				if(val == 0)
					OutcomeCombination[i][index] = 2;
				else
					OutcomeCombination[i][index] = val;
			}
		}
		return OutcomeCombination;

	}
	
	public void findWeightForClusterOutcomesSecondMethod(String clusterName, String userName, int CommSize){
		
		int[][] userOutcomes = User.getusersAndOutcomes().get(userName);
		
		int[][] clusterOutcomes = clustersAndOutcomes.get(clusterName);
		for(int i=0; i<userOutcomes.length; i++){
			Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
			for(int j=1; j<userOutcomes[i].length; j++){
				if(userOutcomes[i][j] == 0)continue;
				indexPref.put(j, userOutcomes[i][j]);
			}
		ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
		for(int z=0; z<clusterOutcomes.length; z++){
			for(int q=0; q<prefrence.size(); q++){

				if(clusterOutcomes[z][prefrence.get(q)] != 0 && 
						clusterOutcomes[z][prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
				if(q == prefrence.size()-1){
						clusterOutcomes[z][0] += userOutcomes[i][0]/(userOutcomes.length / Math.pow(CommSize, 2));
					}
				}
			}
		}
		clustersAndOutcomes.put(clusterName, clusterOutcomes);
	}


	public void findWeigthforClusterOutcomes(String clusterName, String userName){
		int[] weight = User.getUsersAndNormalizedWeight().get(userName);
		ArrayList<int[]> userPref = User.getUsersAndPrefrences(userName);
		int[][] clusterOutcomes = clustersAndOutcomes.get(clusterName);
		for(int i=0; i<userPref.size(); i++){
			Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
			for(int j=1; j<userPref.get(i).length; j++){
				if(userPref.get(i)[j] == -1)continue;
				indexPref.put(j, userPref.get(i)[j]);
			}
			ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
			for(int z=0; z<clusterOutcomes.length; z++){
				for(int q=0; q<prefrence.size(); q++){
					if(clusterOutcomes[z][prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
					if(q == prefrence.size()-1){
						int index = userPref.get(i)[0];
						int attWeight = weight[index-1];
						clusterOutcomes[z][0] += attWeight;
					}
				}

			}	

		}
		clustersAndOutcomes.put(clusterName, clusterOutcomes);
	}
	
	public static  void findWeightforFinalOutcome(String userName){
		
		int[] weight = User.getUsersAndNormalizedWeight().get(userName);
		ArrayList<Integer> commonAtt = User.getUersAndCommonAttforwithCommonCluster().get(userName);
		//ArrayList<int[]> userPref = User.getUsersAndPrefrences(userName);//nonCPT
		ArrayList<int[]> userPref = User.getUsersAndPrefrencesForAllImpAtt(userName); //CPT
		if(userPref != null){
		for(int i=0; i<userPref.size(); i++){
			Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
			for(int j=1; j<userPref.get(i).length; j++){
				if(userPref.get(i)[j] == -1)continue;
				indexPref.put(j, userPref.get(i)[j]);
			}
			ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
			for(int q=0; q<prefrence.size(); q++){
				if(finalOutCome[prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
				if(q == prefrence.size()-1){
					int index = userPref.get(i)[0];
					int attWeight = weight[index-1];
					if(User.getusersAndWeightforFinalOutcome().containsKey(userName)){
						User.getusersAndWeightforFinalOutcome().get(userName)[0] += attWeight;
						int[] ss = User.getusersAndWeightforFinalOutcome().get(userName);
 						
					}
					else{
						int[] weightForOutcome = new int[2];
						weightForOutcome[0] = attWeight;
						User.putusersAndWeightforFinalOutcome(userName, weightForOutcome);
						
					}
				}
				
			}

		}
		}

	}
	
	public static void findWeightForBestPref(String userName){
		ArrayList<Integer> commonAtt = User.getUersAndCommonAttforwithCommonCluster().get(userName);
		int weight = 0;
		if(commonAtt != null){
		for(int i=0; i<commonAtt.size(); i++){
			weight += User.getUsersAndNormalizedWeight().get(userName)[commonAtt.get(i)-1];
		}
		}
		if(User.getusersAndWeightforFinalOutcome().containsKey(userName)){
			User.getusersAndWeightforFinalOutcome().get(userName)[1] = weight;
		}
		else{
			int[] weightForOutcome = new int[2];
			weightForOutcome[1] = weight;
			User.putusersAndWeightforFinalOutcome(userName, weightForOutcome);
		}
	}
	
	
	public void findWeigthforClusterOutcomes2(){
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int m=0; m<clusterNum.size(); m++){
			ArrayList<String> usersInCluster = clustersAndUsers.get(m);
			for(int s=0; s<usersInCluster.size(); s++){
				int[] weight = User.getUsersAndNormalizedWeight().get(usersInCluster.get(s));
				ArrayList<int[]> userPref = User.getUsersAndPrefrences(usersInCluster.get(s));
				int[][] clusterOutcomes = clustersAndOutcomes.get(usersInCluster.get(s));
				for(int i=0; i<userPref.size(); i++){
					Map<Integer, Integer> indexPref = new HashMap<Integer, Integer>();
					for(int j=1; j<userPref.get(i).length; j++){
						if(userPref.get(i)[j] == -1)continue;
						indexPref.put(j, userPref.get(i)[j]);
					}
					ArrayList<Integer> prefrence = new ArrayList<Integer>(indexPref.keySet());
					for(int z=0; z<clusterOutcomes.length; z++){
						for(int q=0; q<prefrence.size(); q++){
							if(clusterOutcomes[z][prefrence.get(q)] != indexPref.get(prefrence.get(q))) break;
							if(q == prefrence.size()-1){
								int index = userPref.get(i)[0];
								int attWeight = weight[index-1];
								clusterOutcomes[z][0] += attWeight;
							}
						}

					}	

				}
				clustersAndOutcomes.put(clusterNum.get(m), clusterOutcomes);
			}
			
		}

	}

	public void findFinalOutcome(){
		ArrayList<Integer> attName = new ArrayList<Integer>(attAndCluster.keySet());
		for(int i=0; i<attName.size(); i++){
			int finalVal = 0;
			ArrayList<String> clusterNum = attAndCluster.get(attName.get(i));
			for(int j=0; j<clusterNum.size(); j++){
				Integer[] sortedIndex = clustersAndSortedIndex.get(clusterNum.get(j));
				int size = sortedIndex.length - 1;
				int index = sortedIndex[size];
				int[] clusterBestOutcomes = clustersAndOutcomes.get(clusterNum.get(j))[index]; 
				int value = clusterBestOutcomes[attName.get(i)];
				int numOfUserInCluster = clustersAndUsers.get(clusterNum.get(j)).size();
				if(value == 2){
					for(int z=0; z<numOfUserInCluster; z++){
						finalVal += 1;
					}
				}
				else if(value == 1){
					for(int z=0; z<numOfUserInCluster; z++){
						finalVal -= 1;
					}
				}
				
			}
			if(finalVal > 0)
				finalOutCome[attName.get(i)] = 2;
			else if(finalVal < 0)
				finalOutCome[attName.get(i)] = 1;
			else
				finalOutCome[attName.get(i)] = (Math.random() <= 0.5) ? 1 : 2;
						
		}
		for(int i=1; i<finalOutCome.length; i++){
			System.out.println("value for outcome x"+i+" is equal to " +finalOutCome[i]);
		}
	}
	
	public static void evaluateOutcome(){
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int i=0; i<clusterNum.size(); i++){
			ArrayList<String> usersInCluster = new ArrayList<String>();
			usersInCluster = clustersAndUsers.get(clusterNum.get(i));
			for(int j=0; j<usersInCluster.size(); j++){
				findWeightforFinalOutcome(usersInCluster.get(j));
				findWeightForBestPref(usersInCluster.get(j));
			}
		}
	}
	
	public static void evaluateClusterOutcome(){
		ArrayList<String> clusterNum = new ArrayList<String>(clustersAndUsers.keySet());
		for(int i=0; i<clusterNum.size(); i++){
			ArrayList<String> usersInCluster = new ArrayList<String>();
			usersInCluster = clustersAndUsers.get(clusterNum.get(i));
			int size = clustersAndSortedIndex.get(clusterNum.get(i)).length;
			int index = clustersAndSortedIndex.get(clusterNum.get(i))[size-1];
			int weight = clustersAndOutcomes.get(clusterNum.get(i))[index][0];
			int yes = 0;
			int no = 0;
			for(int j=0; j<usersInCluster.size(); j++){
				System.out.println("UserName "+ usersInCluster.get(j));
				int[] ss = User.getusersAndWeightforFinalOutcome().get(usersInCluster.get(j));
				if((weight/usersInCluster.size()) > (ss[1]/2))
					yes++;
				else
					no++;
			}
			System.out.println("values for cluster " + clusterNum.get(i) + " is:");
			for(int j=1; j<clustersAndOutcomes.get(clusterNum.get(i))[index].length; j++)
				System.out.println("the value for X"+j+" is equal to "+ clustersAndOutcomes.get(clusterNum.get(i))[index][j]);
			System.out.println("for cluster: " + clusterNum.get(i) + " the number of yes is:"+ yes+
					" and the number of no is : " +no);
			
		}
		
	}
}





