package Core;

//import java.util.regex.Pattern;


public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ProglishParser p = new ProglishParser("neo-store");
		
		System.out.println(p.ThingHasProp.matches("rob has Hand Quantity equal to 2"));
		System.out.print(p.ThingHasProp.getBlanks());
	
		//********Testing Proglish Core Functions
		/*Proglish prog = new Proglish("newlocation");

		prog.has("Rob","part","head");
		
		prog.has("Rob","part","torso");
		
		prog.has("head","part","nose");
		
		System.out.println(prog.inheritsFromQ("nose", "part", "Rob"));*/
		
		//********Testing Proglish String Functions
		
		/*Proglish prog = new Proglish("neo-store");
		
		TaggablePattern firstPatt = new TaggablePattern("TP");
		firstPatt.add(new Blank(prog,"thing"));
		firstPatt.add("'s");
		firstPatt.add(new Blank(prog,"prop"));
		
		System.out.println(firstPatt.toString());
		
		System.out.println(firstPatt.matches("stacy's mom"));
		String[] blanks = firstPatt.getBlankStrings();
		
		for(int i = 0; i<blanks.length; i++){
			System.out.println(blanks[i]);
		}
		
		Proglish.end();*/
		
		/*PatternRule muffin = new PatternRule(new TaggablePattern(),new TaggablePattern());
		LinkedList<Integer> one = new LinkedList<Integer>();
		one.add(1);
		LinkedList<Integer> two = new LinkedList<Integer>();
		two.add(2);
		two.add(3);
		LinkedList<Integer> three = new LinkedList<Integer>();
		three.add(4);
		three.add(5);
		three.add(6);
		LinkedList<Integer>[] scone = new LinkedList[3];
		scone[0] = one;
		scone[1] = two;
		scone[2] = three;
		
		LinkedList<Integer>[] bberry = muffin.reverseMap(scone);
		
		for(int i = 0; i<6; i++){
			ListIterator it = bberry[i].listIterator();
			while(it.hasNext()){
				System.out.print(it.next());
			}
		}*/
		
	}

}
