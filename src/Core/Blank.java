package Core;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Blank {

	private String parent = "everything";
	public String regex = "\\b.+\\b";
	private Proglish prog;
	private String type = "prop";
	public String currentLiteral = "";
	public int score = 0;
	
	public Blank(Proglish proglish, String type){
		this.type = type;
		prog = proglish;
	}
	
	public Blank(Proglish proglish, String type, String parent){
		prog = proglish;
		this.type = type;
		this.parent=parent;
	}
	
	public Blank(Proglish proglish, String type, String parent, String regex){
		prog = proglish;
		this.type = type;
		this.parent=parent;
		this.regex=regex;
	}
	
	public Blank(String xml, Proglish proglish){
		prog = proglish;
		
		if(xml.matches("<Blank>.+</Blank>")){
			Scanner ns = new Scanner(xml);
			Pattern rest = Pattern.compile("[^<]+");
			ns.next("<Blank>");
			//type parent literal regex score
			ns.next("<type>");
			type = ns.next(rest);
			ns.next("</type>");
			ns.next("<parent>");
			parent = ns.next(rest);
			ns.next("</parent>");
			ns.next("<literal>");
			currentLiteral = ns.next(rest);
			ns.next("</literal>");
			ns.next("<regex>");
			regex = ns.next(rest);
			ns.next("</regex>");
			ns.next("<score>");
			score = ns.nextInt();
		}
	}
	
	public String getParent(){
		return parent;
	}
	
	public void setParent(String parent){
		this.parent = parent;
	}
	
	public String getRegex(){
		return regex;
	}
	
	public String getType(){
		return type;
	}
	
	public String getLiteral(){
		return currentLiteral;
	}
	
	public void setLiteral(String lit){
		currentLiteral = lit;
	}
	
	public boolean matchesOut(String literal){
		boolean match = true;
		
		if(type=="thing"&&!parent.equals("everything")){
			match = prog.inheritsFromQ(literal.trim(), "member", parent);
		}
		
		if(!regex.equals("")){
			match = match&&literal.matches(regex);
		}
		
		if(match){
			currentLiteral = literal;
		}
		
		return match;
		
	}
	public boolean matches(String literal){
		boolean match = true;
		
		if(type.equals("thing")&&!parent.equals("everything")){
			
			Boolean parentPresent = false;
			String[] literalParents = prog.inheritsFrom(literal,"memberOfSet");
			for(int i = 0; i < literalParents.length; i++){
				if(literalParents[i].equals(parent)){
					parentPresent = true;
				}
			}
			if(parentPresent == false){
				
				String[] parentsParents = prog.inheritsFrom(parent, "memberOfSet");
				
				for(int i = 0; i < literalParents.length; i++){
					for(int j = 1; j < parentsParents.length; j++){
						if(literalParents[i].equals(parentsParents[j])){
							parent = parentsParents[i];
							parentPresent = true;
						}
					}
				}
			}
			if(parentPresent = false){
				parent = "everything";
			}
			
		}
		
		if(!regex.equals("")){
			match = match&&literal.matches(regex);
		}
		
		if(match){
			currentLiteral = literal;
		}
		
		return match;
		
	}
	public boolean matches(Blank other){
		return other.getType().equals(type);
	}
	
	//TODO: Merging Two Blanks
	/*public void merge(Blank other){
		if(type.equals("thing")&&other.getType().equals("thing")){
			if(other.getParent().equals(parent)){} //DO NOTHING
			else if(other.getParent()){
				
			}
		}
	}*/
	
	public String toString(){
		return "<Blank><type>"+type+"</type><parent>"+parent+"</parent><regex>"+
			regex+"</regex><literal>"+currentLiteral+"</literal><score>"+score+
			"</score></Blank>";
		
	}
	
	//Haven't been able to think of a situation in which this would be useful
	/*public Blank unify(Blank other){
		
		
		
		return other;
	}*/
}
