package Core;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PatternRule {
	
	public TaggablePattern from;
	public LinkedList<TaggablePattern> to;
	LinkedList<Integer> blankMap;

	public PatternRule(TaggablePattern f, LinkedList<TaggablePattern> t){
		from = f;
		to = t;
		Blank[] fBlanks = f.getBlanks();
		
		for(int k = 0; k<t.size(); k++){

		Blank[] tBlanks = t.get(k).getBlanks();
		
		for(int i = 0; i<fBlanks.length; i++){
			for(int j = 0; j<fBlanks.length; j++){
				if(tBlanks[i].getType().equals(fBlanks[j].getType())&&
					tBlanks[i].getLiteral().equals(fBlanks[j].getLiteral())){
						blankMap.add(j);
				}
			}
		}
		}
	}
	public PatternRule(LinkedList<Integer> m,TaggablePattern f, LinkedList<TaggablePattern> t){
		from = f;
		to = t;
		blankMap = m;
	}
	
	public PatternRule(String line, Proglish prog){		//From XML
		LinkedList<Integer> m = new LinkedList<Integer>();
		
		if(line.matches("<PatternRule>.+<PatternRule>")){
			
			Scanner prScan = new Scanner(line);
			Pattern middle = Pattern.compile("[^<]+");
			prScan.next("<PatternRule>");
			
			prScan.next("<blankMap>");
			while(prScan.hasNextInt()){
				m.add(prScan.nextInt());
				prScan.next(",");
			}
			prScan.next("</blankMap>");
			
			prScan.next("<from>");
			from = new TaggablePattern(prScan.next(middle),prog);
			prScan.next("</from>");
			
			prScan.next("<to>");
			to = new LinkedList<TaggablePattern>();
			Pattern tp = Pattern.compile("<TaggablePattern>.+?</TaggablePattern>");
			while(prScan.hasNext(tp)){
				to.add(new TaggablePattern(prScan.next(tp),prog));
			}
		}
	}
	
	public boolean matches(TaggablePattern f){
		return from.matches(f);
	}
	
	public boolean matches(String sent){
		return from.matches(sent);
	}
	
	public LinkedList<TaggablePattern> PatternReplace(LinkedList<TaggablePattern> frm){
		TaggablePattern f = frm.get(0);
		return PatternReplace(f);
	}
	
	public LinkedList<TaggablePattern> PatternReplace(TaggablePattern f){
		
		
		LinkedList<TaggablePattern> newPatt = null;
		
		if(matches(f)){
			TaggablePattern nextPatt = new TaggablePattern();
			newPatt = new LinkedList<TaggablePattern>();
			ListIterator<Integer> blanks = blankMap.listIterator();
				
			Blank[] newBlanks = f.getBlanks();
			
			ListIterator<TaggablePattern> li = to.listIterator();
			
			while(li.hasNext()){
				nextPatt = li.next();
				
				for(int i = 0; i<nextPatt.length(); i++){
					Object tempObj = nextPatt.patt.get(i);
					if(tempObj instanceof Blank){
						nextPatt.patt.set(i, newBlanks[blanks.next()]);
					}
				}
				
				newPatt.add(nextPatt);
			}
			
		}
		
		return newPatt;
		
	}
	
	public String toString(){
		String pr = "<PatternRule><blankMap>";
		ListIterator<Integer> bm = blankMap.listIterator();
		while(bm.hasNext()){
			pr = pr+bm.next()+",";
		}
		pr = pr+"</blankMap>"+from.toString();
		pr = pr+"<from>"+from.toString()+"</from>";
		
		ListIterator<TaggablePattern> li = to.listIterator();
			pr = pr+"<to>";
		while(li.hasNext()){
			pr=pr+li.next().toString();
		}
			pr = pr+"</to>";
		
		pr = pr+"</PatternRule>";
		return pr;
	}
}
