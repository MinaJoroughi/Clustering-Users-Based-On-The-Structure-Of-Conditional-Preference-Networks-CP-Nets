import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Attribute {

	private static final int NUM_ATTS = PreProcess.NUM_OF_ATTRIBUTES;
	private static Attribute atts[] = new Attribute[NUM_ATTS];
	private static Set<Integer> childList = new HashSet<Integer>();
	private String name;
	private int weight;
	private ArrayList<Integer> child;
	private boolean visited;
	private String tag;
	
	
	public static void clearAll(){
		atts = new Attribute[NUM_ATTS];
		childList = new HashSet<Integer>();
	}
	
	public Attribute(String name) {
		this.name = name;
		child = new ArrayList<Integer>();
		visited = false;
		weight = 0;
		tag = "Unimportant";
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public static Attribute[] getAtts() {
		return atts;
	}



	public static void setAtts(Attribute[] atts) {
		Attribute.atts = atts;
	}


	public String getName() {
		return name;
	}
	
	
	public int getIndex() {
		return Integer.valueOf(name.substring(1,name.length()));
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getWeight() {
		return weight;
	}



	public void setWeight(int weight) {
		this.weight = weight;
	}



	public ArrayList<Integer> getChild() {
		return child;
	}



	public void setChild(ArrayList<Integer> child) {
		this.child = child;
	}



	public boolean isVisited() {
		return visited;
	}



	public void setVisited(boolean visited) {
		this.visited = visited;
	}



	public static int getNumAtts() {
		return NUM_ATTS;
	}



	public static Set<Integer> getChildList() {
		return childList;
	}


	public static void setChildList(Set<Integer> childList) {
		Attribute.childList = childList;
	}


	public static void findChildList(){
		for(Attribute att : atts){
			if(att!= null){
				for(int i=0; i<att.child.size(); i++){
					int childNum = att.child.get(i);
					childList.add(childNum);
				}
			}
		}
		//System.out.println(childList.toString());
	}



	public static void setImportance(){
		int max = atts[1].getWeight();
		int min = max;
		for(Attribute attribute : atts){
			if(attribute != null){
				min = Math.min(min, attribute.getWeight());
				max = Math.max(max, attribute.getWeight());
			}
		}
		int mid = max/3;//this value is branching factor
		for(Attribute attribute : atts){
			if(attribute != null){
				String attNum = attribute.name.substring(1);
				if(!childList.contains(Integer.valueOf(attNum)) && attribute.getWeight() == 1){
					attribute.setTag("Important");
					attribute.setWeight(max);
				}
				else if(attribute.getWeight() > mid)
					attribute.setTag("Important");
				else{
					attribute.setWeight(0);
				}
			}
		}
		/*for(Attribute att : atts){
			if(att != null && att.getTag().equals("Important")){
				System.out.println(att.getName()  + "mina");
			}
		}*/
	}

	public int calcWieght(){
		//System.out.println("Setting value to: " +name);
		if(visited){
			//System.out.println("this attribute is already visited" + weight);
			return weight;
		}
		else if(child.size() == 0){
			weight = 1;
			visited = true;
			//System.out.println("this attribute hasn't any parent" + weight);
			return weight;
		}
		else{
			for(int i=0; i<child.size(); i++){
				//System.out.println("this attribut's " +i +" parent is " + Attribute.atts[parent.get(i)].name);
				//System.out.println("Defining the weight for " + Attribute.atts[child.get(i)].name);
				weight += atts[child.get(i)].calcWieght();
			}
			weight += 1;
			visited = true;
			//System.out.println("getting outs"+name);
			return weight;
		}
	}
}
