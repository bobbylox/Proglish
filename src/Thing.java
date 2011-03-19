import java.util.ArrayList;

public class Thing extends Everything{
	
	private ArrayList<String> fieldNames = new ArrayList<String>(5);
	
	private ArrayList<Object> fieldValues = new ArrayList<Object>(5);
	
	private ArrayList<Fact> facts = new ArrayList<Fact>(5);
	
	public Thing(String name){
		fieldNames.add("name");
		fieldValues.add(name);
	}
	public Thing(String name, PNumber value){
		fieldNames.add("name");
		fieldValues.add(name);
		fieldNames.add("value");
		fieldValues.add(value);
	}
	public String getName(){
		return getValue("name").toString();
	}
	public boolean hasA(String fieldName){
		return fieldNames.contains(fieldName);
	}
	public Object getValue(String fieldName){
		int index = fieldNames.indexOf(fieldName);
		if(index==-1){
			fieldNames.add(fieldName);
			fieldValues.add(null);
			return null;
		}
		else{
			return fieldValues.get(index);
		}
		
	}
	public void add(String fieldName, Object fieldValue){
		fieldNames.add(fieldName);
		fieldValues.add(fieldValue);
	}
	public void add(String fieldName){
		this.add(fieldName, null);
	}
	public void setValue(String fieldName, Object fieldValue){
		int index = fieldNames.indexOf(fieldName);
		if(index==-1){
			this.add(fieldName, fieldValue);
		}
		else{
			fieldValues.set(index,fieldValue);
		}
	}
	public String toString(){
		String finalString = "<"+this.getName()+"><fields>";
		String tempString = "";
		
		for(int i=0 ; i<fieldNames.size();i++){
			tempString = fieldNames.get(i);
			finalString = finalString+"<"+tempString+">"+fieldValues.get(i).toString()+"</"+tempString+">";
		}
		
		finalString = finalString+"</fields><relays>";
		
		// Deal with relays
		for(int i=0 ; i<facts.size();i++){
			finalString = finalString+facts.get(i).toString();
		}
		
		finalString=finalString+"</relays></"+this.getName()+">";
		
		return finalString;
	}
}
