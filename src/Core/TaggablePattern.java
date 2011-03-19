package Core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TaggablePattern {
	
	LinkedList<Object> patt = new LinkedList<Object>();
	public int score = 0;
	private int minLength = 0;
	private String regex = "";
	private int changesSinceRegexUpdate = 1;
	public LinkedList<String> literals;

	public TaggablePattern(LinkedList<Object> pattern){
		
		//test that this is a pattern with no disallowed objects
		
		boolean patternQ = true;
		
		for(int i = 0;i<pattern.size();i++){
			Class<? extends Object> elementclass = pattern.get(i).getClass();
			if(!(elementclass.equals(String.class)||
					elementclass.equals(Blank.class)||
					elementclass.equals(Pattern.class)||
					elementclass.equals(TaggablePattern.class))){
				patternQ = false;
			}
		}
		
		if(patternQ){
			patt = pattern;
		}
	}
	
	public TaggablePattern(){}
	
	public TaggablePattern(String xml,Proglish prog){
		if(xml.matches("<TaggablePattern>.+</TaggablePattern>")){
			Scanner ns = new Scanner(xml);
			Pattern rest = Pattern.compile("[^<]+");
				ns.next("<TaggablePattern>");
					ns.next("<score>");
						score = ns.nextInt();
					ns.next("</score>");
				while(ns.hasNext()){
					if(ns.hasNext("<Literal>")){
						ns.next("<Literal>");
						add(ns.next(rest));
						ns.next("</Literal>");
					}
					else if(ns.hasNext("<Regex>")){
						ns.next("<Regex>");
						add(Pattern.compile(ns.next(rest)));
						ns.next("</Regex>");		
					}
					else if(ns.hasNext("<Blank>")){
						add(new Blank(ns.next(Pattern.compile("<Blank>.+?</Blank>")),prog));
					}
					else if(ns.hasNext("<TaggablePattern>")){
						add(new TaggablePattern(
							ns.next(
								Pattern.compile("<TaggablePattern>.+?</TaggablePattern>")
									),prog));
					}
					
				}
		}
	}
	
	private boolean elementMatches(Pattern element, String literal){
		return element.matcher(literal).matches();
	}
	
	private boolean elementMatches(String element, String literal){
		return element.equalsIgnoreCase(literal.trim());
	}
	
	private boolean elementMatches(Blank element, String literal){
		return element.matches(literal);
	}
	
	private boolean elementMatches(TaggablePattern element, String literal){
		return element.matches(literal);
	}
	
	private boolean elementMatches(Object element, String literal){
		Class<? extends Object> elementClass = element.getClass();
		
		if(elementClass.equals(String.class)){
			return elementMatches((String)element,literal);
		}
		else if(elementClass.equals(Pattern.class)){
			return elementMatches((Pattern)element,literal);
		}	
		else if(elementClass.equals(Blank.class)){
			return elementMatches((Blank)element,literal);
		}	
		else if(elementClass.equals(TaggablePattern.class)){
			return elementMatches((TaggablePattern)element,literal);
		}	
		else{
			return false;
		}
		
	}
	
	
	private boolean taggableMatches(Object thisElement, Object otherElement){
		Class<? extends Object> elementClass1 = thisElement.getClass();
		Class<? extends Object> elementClass2 = otherElement.getClass();
		
		if(elementClass1.equals(String.class)){
			if(elementClass2.equals(String.class)){
				return ((String)thisElement).equals((String)otherElement);
			}
			else{
				return false;
			}
		}
		else if(elementClass1.equals(Pattern.class)){
			if(elementClass2.equals(String.class)){
				return ((Pattern)thisElement).matcher((String)otherElement).matches();
			}
			else if(elementClass2.equals(Pattern.class)){
				return ((Pattern)thisElement).pattern().equals(((Pattern)otherElement).pattern());
			}
			else{return false;}
		}
		else if(elementClass1.equals(Blank.class)){
			if(elementClass2.equals(Blank.class)){
				return ((Blank)thisElement).matches((Blank)otherElement);
			}
			else if(elementClass2.equals(TaggablePattern.class)){
				TaggablePattern shemp = ((TaggablePattern)otherElement).copy();
				return shemp.matches(((Blank)thisElement).currentLiteral);
			}
			else{
				return false;
			}
		}
		else if(elementClass1.equals(TaggablePattern.class)){
			if(elementClass2.equals(TaggablePattern.class)){
				return ((TaggablePattern)thisElement).matches((TaggablePattern)otherElement);
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	public boolean matches(TaggablePattern other){
		
		if(length()!=other.length()){
			return false;
		}
		else{
			boolean match = true;
			Iterator<Object> thisIt = patt.iterator();
			Iterator<Object> otherIt = other.patt.iterator();
			
			while(thisIt.hasNext()&&match){
				 if(!taggableMatches(thisIt.next(),otherIt.next())){
					 match = false;
				 }
			}	
			return match;
		}
	}
	
	
	public String toRegex() throws Exception{
		String matchable = "";
		Iterator<Object> it = patt.iterator();
		while(it.hasNext()){
			Object thing = it.next();
			Class<? extends Object> pattClass = thing.getClass();
			
			if(pattClass.equals(String.class)){
				matchable += (String)thing;
			}
			else if(pattClass.equals(Pattern.class)){
				matchable += ((Pattern)thing).pattern();
			}
			else if(pattClass.equals(Blank.class)){
				matchable += ((Blank)thing).getRegex();
			}
			else if(pattClass.equals(TaggablePattern.class)){
				matchable += ((TaggablePattern)thing).toRegex();
			}
			else{throw new Exception("invalid class in TaggablePattern");}
		}
		return matchable;
	}
	
	public boolean quickMatch(String literal){
		String reg = "";
		if(changesSinceRegexUpdate>0){
			try {
				reg = toRegex();
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
		else{
			reg = regex;
		}
		if(literal.matches(reg)){
			literals.add(literal);
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean matches(String literal){

		if(findmatch(patt,literal,0)){
			literals.add(literal);
			return true;
		}
		else{
			return false;
		}
		
	}
	
	private boolean findmatch(LinkedList<Object> pat, String lit, int pos1){

		int pos2 = pos1;
		
		if(pat.isEmpty()&&pos1>=lit.length()){
			return true;
		}
		else if(pat.isEmpty()||pos1>=lit.length()){
			return false;
		}
		else{
			while(pos2<=lit.length()){
				if(elementMatches(pat.element(),lit.substring(pos1,pos2).trim())){
					
					LinkedList<Object> copy = new LinkedList<Object>(pat);
					copy.removeFirst();
					
					if(findmatch(copy,lit,pos2)){
						return true;
					}
					else{
						pos2++;
					}
				}
				else{
					pos2++;
				}
			}
			return false;
		}
	}
	
	public String[] getBlankStrings(){
		LinkedList<String> blankList= new LinkedList<String>();
		
		for(int i = 0;i<patt.size();i++){
			Class<? extends Object> elementclass = patt.get(i).getClass();
			if(elementclass.equals(Blank.class)){
				blankList.add(((Blank)patt.get(i)).getLiteral());
			}
			else if(elementclass.equals(TaggablePattern.class)){
				String[] subBlanks = ((TaggablePattern)patt.get(i)).getBlankStrings();
				blankList.add("(");
				for(int j = 0; j<subBlanks.length;j++){
					blankList.add(subBlanks[j]);
				}
				blankList.add(")"); //This is to denote that this was all in one position
			}
		}
		
		String[] allBlanks = (String[])blankList.toArray();
		
		/*String[] blanknames = new String[allBlanks.length];
		
		for(int i = 0; i<allBlanks.length; i++){
			blanknames[i] = (String)allBlanks[i];
		}*/
		
		return allBlanks;
		
	}
	
	public Blank[] getBlanks(){
		LinkedList<Blank> blankList= new LinkedList<Blank>();
		
		for(int i = 0;i<patt.size();i++){
			Class<? extends Object> elementclass = patt.get(i).getClass();
			if(elementclass.equals(Blank.class)){
				blankList.add((Blank)patt.get(i));
			}
			else if(elementclass.equals(TaggablePattern.class)){
				Blank[] subBlanks = ((TaggablePattern)patt.get(i)).getBlanks();
				for(int j = 0; j<subBlanks.length;j++){
					blankList.add(subBlanks[j]);
				}
				
			}
			
		}
		
		Blank[] blanks = new Blank[blankList.size()];
		
		for(int i = 0; i<blankList.size(); i++){
			blanks[i] = blankList.get(i);
		}
		
		return blanks;
		
	}
	
	
	public int length(){
		return patt.size();
	}
	
	public String toString(){
		String str = "<TaggablePattern><score>"+score+"</score>";
		Object obj;
		ListIterator<Object> li = patt.listIterator(0);
		
		while(li.hasNext()){
			obj = li.next();
			if( obj instanceof String){
				str = str+"<Literal>"+obj+"</Literal>";
			}
			else if(obj instanceof Pattern){
				str = str+"<Regex>"+obj+"</Regex>";
			}
			else if(obj instanceof Blank||obj instanceof TaggablePattern){
				str = str+obj.toString();
			}
		}
		
		str = str+"</TaggablePattern>";
		return str;
	}
	
	public void add(Object obj){
		if(obj instanceof String||obj instanceof Pattern||obj instanceof Blank||obj instanceof TaggablePattern){
			patt.add(obj);
		}
		changesSinceRegexUpdate++;
	}
	public void add(int pos, Object obj){
		add(obj,pos);
	}
	public void add(Object obj, int pos){
		if(obj instanceof String||obj instanceof Pattern||obj instanceof Blank||obj instanceof TaggablePattern){
			patt.add(pos,obj);
		}
		changesSinceRegexUpdate++;
	}
	public boolean isEmpty(){
		return patt.isEmpty();
	}
	public TaggablePattern copy(){
		TaggablePattern copy = new TaggablePattern(patt);
		copy.score = this.score;
		copy.regex = this.regex;
		return copy;
	}

}
