package Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Stack;

public class ProglishParser {
	
	Proglish prog;

//***************SYNTAX PATTERNS********************
	// Full sentence
	/*Pattern addField = Pattern.compile("([ a-z0-9,']+? has [ a-z0-9,']+?\\.)");
	Pattern addFieldVal = Pattern.compile("([ a-z0-9,']+? has [ a-z0-9,']+? = [ a-z0-9,']+?\\.)|([ a-z0-9,']+? has [ a-z0-9,']+? equal to [ a-z0-9,']+?\\.)");
	Pattern queryFieldBoolean = Pattern.compile("([ a-z0-9,']+? has ((a )|(an ))?[ a-z0-9,']+?\\?)");
	Pattern queryField = Pattern.compile("((what = )?([ a-z0-9,']+?\'s [ a-z0-9,']+?)\\??)|([ a-z0-9,']+?\'s [ a-z0-9,']+? = what\\?)");
	*/
//***************TAGGABLE PATTERNS************
	TaggablePattern ThingHasProp;
	TaggablePattern ThingHasPropEquals;
	TaggablePattern ThingHadProp;
	TaggablePattern ThingHadPropEquals;
	TaggablePattern ThingHadPropTime;
	TaggablePattern ThingHadPropEqualsTime;
	TaggablePattern ThingWillHaveProp;
	TaggablePattern ThingWillHavePropEquals;
	TaggablePattern ThingWillHavePropTime;
	TaggablePattern ThingWillHavePropEqualsTime;
	
	LinkedList<TaggablePattern> atomics;
	
//***************PatternRule Array************
	
	LinkedList<PatternRule> rules;
	
//***************CONSTRUCTOR******************
	
	public ProglishParser (String repository, String pathToPatterns) throws FileNotFoundException{
		
		prog = new Proglish(repository);
		
		//****Define Basic Taggable Patterns
		
			//person has height
		ThingHasProp = new TaggablePattern();
		
		ThingHasProp.add(new Blank(prog,"thing"));
		ThingHasProp.add("has");
		ThingHasProp.add(new Blank(prog,"prop"));
		
			//person has height = 5 ft 6 inches
		ThingHasPropEquals = new TaggablePattern();
		
		ThingHasPropEquals.add(new Blank(prog,"thing"));
		ThingHasPropEquals.add("has");
		ThingHasPropEquals.add(new Blank(prog,"prop"));
		ThingHasPropEquals.add("equal to");
		ThingHasPropEquals.add(new Blank(prog,"thing"));
	
		//TODO: Load more taggable pattern mappings from the XML file
		
		rules = new LinkedList<PatternRule>();
		
		atomics.add(ThingHasProp);
		atomics.add(ThingHasPropEquals);
		
		try {
			Scanner ruleScanner = new Scanner(new File(pathToPatterns));
			Pattern pr = Pattern.compile("<PatternRule>.+?</PatternRule>");
			while(ruleScanner.hasNext(pr)){
				rules.add(new PatternRule( ruleScanner.next(pr), prog ) );
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
//***************PARSER METHODS***************
	
	public String clean(String sentence){
		
		sentence = sentence.toLowerCase();
		
		//Replacement patterns
		/*sentence = sentence.replaceAll(" (is a)(( type of)|( member of set))? ", " isA ");
		sentence = sentence.replaceAll("((,)?(( which is)|( is))?( greater than ))",">");
		sentence = sentence.replaceAll("((,)?(( which is)|( is))?( less than ))", "<");
		sentence = sentence.replaceAll("(===)|(==)|( equals )|((, )?((which is )|(is ))?(equal to))|((, )?(which )?(is))", "=");
		sentence = sentence.replaceAll(" (plus)|(add) ","+");
		sentence = sentence.replaceAll(" (minus)|(subtract) ","-");
		sentence = sentence.replaceAll(" (mult)|(multiply)|(times) ","*");
			//Units
		sentence = sentence.replaceAll(" (feet)|(ft.)|(ft) "," foot ");
		sentence = sentence.replaceAll(" (weeks)|(wks.)|(wks)|(wk.)|(wk) "," week ");
		sentence = sentence.replaceAll(" days "," day ");
		sentence = sentence.replaceAll(" (hours)|(hrs.)|(hrs)|(hr.)|(hr) "," hour ");
		sentence = sentence.replaceAll(" (minutes) "," minute ");*/
		
		return sentence;
	
	}
	
	private TaggablePattern replace(String input){
		TaggablePattern tp = null;
		
		Iterator<TaggablePattern> atoms = atomics.iterator();
											//Go through all the built-in commands
		while(atoms.hasNext()){
			TaggablePattern at = atoms.next();
			if(at.matches(input)){
				return at;
			}
		}
		
		Iterator<PatternRule> r = rules.iterator();
											//Go through all the rules
		while(r.hasNext()){
			TaggablePattern rul = r.next().from;
			if(rul.matches(input)){
				return rul;
			}
		}
		return tp;
	}
	
	private LinkedList<TaggablePattern> sentenceSplit(String input){
		LinkedList<TaggablePattern> sentences = new LinkedList<TaggablePattern>();
		int inputLength = input.length();
		int inputStart = 0;
		int inputEnd = inputLength;
		TaggablePattern exp = replace(input);
		
		if(exp.equals(null)){
			
			LinkedList<TaggablePattern> rest = new LinkedList<TaggablePattern>();
			
			while(inputEnd>0&&exp.equals(null)&&rest.isEmpty()){
				inputEnd--;
				exp = replace(input.substring(inputStart, inputEnd));
				rest = sentenceSplit(input.substring(inputEnd,inputLength));
			}
			if(!exp.equals(null)&&!rest.isEmpty()){
				sentences.add(exp);
				sentences.addAll(rest);
			}
		}
		else{
			sentences.add(exp);
		}
		
		return sentences;
	}
	
	public LinkedList<TaggablePattern> replaceAll(String input){
		
		LinkedList<TaggablePattern> statements = sentenceSplit(input);
		
		if(statements.isEmpty()){
			return statements;
		}
		else{
			
		}
	}
	
	public void aver(String input) throws Exception{
		 
		String[] sentences = input.split("[.?]");
		LinkedList<TaggablePattern> full = new LinkedList<TaggablePattern>();
		
		for(int i = 0; i<sentences.length; i++){
			
			full.addAll(replaceAll(clean(sentences[i])));
		}
		
		
	}

}
