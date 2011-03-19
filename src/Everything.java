import java.util.LinkedList;

public abstract class Everything {
	
	private LinkedList<Thing> things = new LinkedList<Thing>();
	private LinkedList<Relay> relays = new LinkedList<Relay>();
	
	public String toString(){
		String world = "";
		for(int i=0;i<things.size();i++){
			world = world+things.get(i).toString();
		}
		for(int i=0;i<relays.size();i++){
			world = world+relays.get(i).toString();
		}
		return world;
	}
	
	public void fromString(String world){
		//TODO
	}
	
	public Thing getThing(String name){
		int index = 0;
		
		while(index<things.size()){
			if(things.get(index).getName().equals(name)){
				return things.get(index);
			}
			else{index++;}
		}
		
		return null;
	}
	
	public void addThing(Thing newThing){
		things.add(newThing);
	}
	
	
}
