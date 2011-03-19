package Core;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.util.index.IndexService;
import org.neo4j.util.index.LuceneIndexService;

public class Proglish{

	private static String NEO_DB_PATH;
    private static final String NAME = "name";
    private static final String VAL = "value";
    private static final String UNIT = "unit";
    private static final String WORD = "word";
    private static final String TYPE = "type";
    private static final String THING = "thing";
    private static final String PROP = "property";
    
    private static NeoService neo;
    private static IndexService nameIndex;
    private static IndexService propertyIndex;
    private static IndexService valueIndex;
    private static IndexService wordIndex;

    /*public static Pattern integerPattern;
    public static Pattern doublePattern;
    public static Pattern rationalPattern;
    public static Pattern mixedFractionPattern;
    public static Pattern scientificNotationPattern;
    public static Pattern mathPattern;
    
    public static HashMap<Double, LinkedList<String>> numberStringMap; */
    
    private static enum Relays implements RelationshipType
    {
		HAS,
		IS,
		WAS,
		WILLBE,
		KNOWNAS,
		ARITHMETIC
    }
    
    private enum Tenses{
    	PAST(Relays.WAS),
    	PRESENT(Relays.IS),
    	FUTURE(Relays.WILLBE);
    	
    	Relays relay;
    	Tenses(Relays r){
    		relay = r;
    	}
    	public Relays toRelay(){
    		return relay;
    	}
    	
    }

	public Proglish(String path){
		
		 NEO_DB_PATH = path;
		
		 neo = new EmbeddedNeo( NEO_DB_PATH );
	     nameIndex = new LuceneIndexService( neo );
	     propertyIndex = new LuceneIndexService( neo );
	     valueIndex = new LuceneIndexService( neo );
	     wordIndex = new LuceneIndexService( neo );
	     registerShutdownHookForNeoAndIndexService();
	     
	     //numberStringMap = new HashMap<Double, LinkedList<String>>();
	     
	     show("Loading numeric words...");
	     
	     //TODO Load numeric words 
	     
	     show("Done loading numeric words...");
	     
	     /*integerPattern = Pattern.compile("[0-9]+");
	     doublePattern = Pattern.compile("([0-9]+\\.[0-9]*)|([0-9]*\\.[0-9]+)");
	     rationalPattern = Pattern.compile("[0-9]+\\/[0-9]+");
	     mixedFractionPattern = Pattern.compile("[0-9]+ +[0-9]+\\/[0-9]+");
	     scientificNotationPattern = Pattern.compile("([0-9]+\\.[0-9]*)|([0-9]*\\.[0-9]+) *\\*? *(10\\^[0-9]+)|(E[0-9]+)");
	     mathPattern = Pattern.compile("([\\(\\)\\/\\+\\-\\*x\\^\\. ]*[0-9]+[\\(\\)\\/\\+\\-\\*x\\^\\. ]*)+");*/
	}
	
	public void clear(){
		Transaction tx = neo.beginTx();
		try{
			tx.success();
		}
		finally{
			tx.finish();
		}
	}
	
	// NEW THING
	
	public Node newThing(String name){
		Transaction tx = neo.beginTx();

		try{
	        Node aka = newWord(name);
			
	        Node node = nameIndex.getSingleNode(NAME, name);
	        if(node!=null){
	        	int i = 1;
	        	while(nameIndex.getSingleNode(NAME, name+i)!=null){
	        		i++;
	        	}
	        	name = name+i;
	        }
	        node = neo.createNode();
	        node.setProperty( NAME, name );
	        node.setProperty(TYPE, THING);
	        nameIndex.index( node, NAME, name );
	       
	        aka.createRelationshipTo( node, Relays.KNOWNAS);
	        
	        tx.success();
			return node;
	    }
		finally{
			tx.finish();
		}
	}
	
	//NEW PROPERTY
	
	public Node newProp(String name){
		Transaction tx = neo.beginTx();

		try{
	        Node aka = newWord(name);
			
	        Node node = nameIndex.getSingleNode(NAME, name);
	        if(node!=null){
	        	int i = 1;
	        	while(nameIndex.getSingleNode(NAME, name+i)!=null){
	        		i++;
	        	}
	        	name = name+i;
	        }
	        node = neo.createNode();
	        node.setProperty( NAME, name );
	        node.setProperty(TYPE, PROP);
	        nameIndex.index( node, NAME, name );
	       
	        aka.createRelationshipTo( node, Relays.KNOWNAS);
	        
	        tx.success();
			return node;
	    }
		finally{
			tx.finish();
		}
	}
	
	//NEW NUMERIC value

	public Node newThing(double val){
		Transaction tx = neo.beginTx();
		
		try{
			/*System.out.println("making new Thing");*/
	        Node node = neo.createNode();
	        node.setProperty( TYPE, THING );
	        node.setProperty( VAL, val );
	        node.setProperty(UNIT, null);
	        valueIndex.index( node, VAL, val );
	        tx.success();
			return node;
	    }
		finally{
			tx.finish();
		}
	}
	
	public Node newThing(double val, String unit){
		Transaction tx = neo.beginTx();
		
		try{
			/*System.out.println("making newVal");*/
	        Node node = neo.createNode();
	        node.setProperty( TYPE, THING );
	        node.setProperty( VAL, val );
	        node.setProperty(UNIT,unit);
	        /*valueIndex.index( node, VAL, val );*/
	        tx.success();
			return node;
	    }
		finally{
			tx.finish();
		}
	}
	
	//New WORD
	
	public Node newWord(String word){
		Transaction tx = neo.beginTx();
		
		try{
			Node node = wordIndex.getSingleNode(WORD, word);
			if(node!=null){
			}
			else{
	        node = neo.createNode();
	        node.setProperty( TYPE, WORD );
	        node.setProperty( WORD , word );
//	        valueIndex.index( node, VAL, val );
			}
			tx.success();
			return node;
	    }
		finally{
			tx.finish();
		}
	}

	// THE THING
	
	public Node Thing(String name){
		Node thing;
		
		Transaction tx = neo.beginTx();
		
		try{
			thing = nameIndex.getSingleNode( NAME, name );
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		return thing;
	}
	
	//Find all things known as "suchandsuch" 
	
	public LinkedList<Node> thingsCalled(String word){
		Node wordNode;
		LinkedList<Node> aka = new LinkedList<Node>();
		
		Transaction tx = neo.beginTx();
		
		try{
			//find the word (if any)
			wordNode = wordIndex.getSingleNode( WORD, word );
			
			if(wordNode!=null){ //if it does exist...
				
				Traverser akaTraverser = wordNode.traverse(
						Traverser.Order.BREADTH_FIRST, 
						StopEvaluator.DEPTH_ONE, 
						new ReturnableEvaluator(){
							public boolean isReturnableNode(TraversalPosition pos){
								return pos.currentNode().getProperty(TYPE).equals(THING);
							}
						}, 
						Relays.KNOWNAS, 
						Direction.OUTGOING );
				// get all the things it refers to
				Iterator<Node> thingIterator = akaTraverser.iterator();
				//and put them in a linked list
				while(thingIterator.hasNext()){
					aka.add(thingIterator.next());
				}
			}
			
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		return aka;
	}
	
	//Find all properties called "suchandsuch"
	
	public LinkedList<Node> PropertiesCalled(String word){
		Node wordNode;
		LinkedList<Node> aka = new LinkedList<Node>();
		
		Transaction tx = neo.beginTx();
		
		try{
			//find the word (if any)
			wordNode = wordIndex.getSingleNode( WORD, word );
			
			if(wordNode!=null){ //if it does exist...
				
				Traverser akaTraverser = wordNode.traverse(
						Traverser.Order.BREADTH_FIRST, 
						StopEvaluator.DEPTH_ONE, 
						new ReturnableEvaluator(){
							public boolean isReturnableNode(TraversalPosition pos){
								return pos.currentNode().getProperty(TYPE).equals(PROP);
							}
						}, 
						Relays.KNOWNAS, 
						Direction.OUTGOING );
				// get all the properties it refers to
				Iterator<Node> thingIterator = akaTraverser.iterator();
				//and put them in a linked list
				while(thingIterator.hasNext()){
					aka.add(thingIterator.next());
				}
			}
			
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		return aka;
	}
	
	//Is this node called "suchandsuch"?
	
	public boolean isCalledQ(Node node, String word){
		Transaction tx = neo.beginTx();
		boolean iscalled = false;
		
		try{
			Traverser akaTraverser = node.traverse(
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(WORD);
						}
					}, 
					Relays.KNOWNAS, 
					Direction.INCOMING );
			// get all the things it's called
			Iterator<Node> thingIterator = akaTraverser.iterator();
			//and put them in a linked list
			while(thingIterator.hasNext()&&!iscalled){
				if(thingIterator.next().getProperty(WORD).equals(word)){
					iscalled = true;
				}
			}
			
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		return iscalled;
	}
	
	//THE NUMERIC THING
	
	public Node Thing(double value){
		Node val;
		
		Transaction tx = neo.beginTx();
		
		try{
			val = valueIndex.getSingleNode( VAL, value );
			tx.success();
		}
		finally{
			tx.finish();
		}
		
		return val;
	}

	//ALSO KNOWN AS
	
	public void isCalled(Node node, String word){
		Transaction tx = neo.beginTx();
		
		try{
			Node wordnode = wordIndex.getSingleNode(WORD, word);
			if(node==null){
	        wordnode = neo.createNode();
	        wordnode.setProperty( TYPE, WORD );
	        wordnode.setProperty( WORD , word );
//	        valueIndex.index( node, VAL, val );
			}
			
			wordnode.createRelationshipTo(node, Relays.KNOWNAS);
			tx.success();
	    }
		finally{
			tx.finish();
		}
	}
	// MAKE A THING FROM A QUANTITY
	public Node newThing(Quantity quant){
		return newThing(quant.value, quant.unit);
	}
	
	private boolean containsElement(Iterator<String> it, String obj){
		while(it.hasNext()){
			if(it.next().equals(obj)){
				return true;
			}
		}
		return false;
	}
	
	public Quantity newQuantity(Node node){
		Transaction tx = neo.beginTx();
		Quantity quant;
		
		try{
			if(node.getProperty(TYPE).equals(THING)&&containsElement(node.getPropertyKeys().iterator(),VAL)){
				double val = (Double)node.getProperty(VAL);
				String unit = (String) node.getProperty(UNIT);
				quant = new Quantity(val,unit);
			}
			else{
				quant = null;
			}
			tx.success();
		}
		finally{
			tx.finish();
		}
		return quant;
		
	}
	// TODO TAG A RELATIONSHIP WITH A DATE
	
	
	//DOES IT HAVE THIS PROPERTY?
	
	public boolean hasQ(Node thing, final String propertyname){
		Transaction tx = neo.beginTx();
		
		try{
		
			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
									isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING ); 
			
			tx.success();
			
			return !hasTraverser.getAllNodes().isEmpty();
		
		}
		finally{
			tx.finish();
		}
	}
	
	public boolean hasQ(String thingname, final String propertyname){
		//This is not very rigorous, because it's really just saying
		//at least one thing called thingname has at least one property called propertyname
		Iterator<Node> it = thingsCalled(thingname).iterator();
		boolean hasq = false;
		
		while(it.hasNext()&&!hasq){
			if(hasQ(it.next(),propertyname)){
				hasq = true;
			}
		}
		return hasq;
	}
    
	// IT HAS THIS PROPERTY
	
    public void has(Node thing, final String propertyname){
	    Transaction tx = neo.beginTx();
		
		try{

			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop;
			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			if(allProps.isEmpty()){
				prop = newProp(propertyname);
				thing.createRelationshipTo(prop, Relays.HAS);
			}
			tx.success();
		}
		finally{
			tx.finish();
		}
	}
    
    public void has(String thingname, String propertyname){
    	//This is not very rigorous, because it's really just saying
		//at least one thing called thingname has at least one property called propertyname
    	LinkedList<Node> tc = thingsCalled(thingname);
    	Iterator<Node> it = tc.iterator();
    	while(it.hasNext()){
    		if(hasQ(it.next(),propertyname)){
    			return;
    		}
    	}
    	Node thing1 = tc.element();   //it stupidly chooses the first thing
    	Node prop = newProp(propertyname);
    	
    	thing1.createRelationshipTo(prop, Relays.HAS);
    	
    }
	    
    //IT HAS THIS PROPERTY-VALUE PAIR (THING)
    
    //QUANTITIES FIRST
    public void has(Node thing, String propertyname, Quantity value, Tenses tense){
    	has(thing, propertyname, newThing(value), tense);
    }
    public void has(Node thing, String propertyname, Quantity value){
    	has(thing, propertyname, value, Tenses.PRESENT);
    }
    public void had(Node thing, String propertyname, Quantity value){
    	has(thing, propertyname, value, Tenses.PAST);
    }
    public void willHave(Node thing, String propertyname, Quantity value){
    	has(thing, propertyname, value, Tenses.FUTURE);
    }
    //NOW NUMBERS
    public void has(Node thing, String propertyname, double value, Tenses tense){
    	has(thing, propertyname, newThing(value), tense);
    }
    public void has(Node thing, String propertyname, double value){
    	has(thing, propertyname, newThing(value), Tenses.PRESENT);
    }
    public void had(Node thing, String propertyname, double value){
    	has(thing, propertyname, newThing(value), Tenses.PAST);
    }
    public void willHave(Node thing, String propertyname, double value){
    	has(thing, propertyname, newThing(value), Tenses.FUTURE);
    }
    
    //NOW REGULAR THINGS
    public void has(Node thing, String propertyname, Node value){
    	has(thing,propertyname,value,Tenses.PRESENT);
    }
    
    public void had(Node thing, String propertyname, Node value){
    	has(thing,propertyname,value,Tenses.PAST);
    }
    
    public void willHave(Node thing, String propertyname, Node value){
    	has(thing, propertyname, value, Tenses.FUTURE);
    }
    
    public void has(Node thing, final String propertyname, final Node value, Tenses tense){
	    Transaction tx = neo.beginTx();
		
		try{
			
			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop;
			boolean newprop = false;
			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			if(allProps.size()<1){
				prop = newProp(propertyname);
				thing.createRelationshipTo(prop, Relays.HAS);
				newprop = true;
			}
			else{
				prop = allProps.iterator().next();//This is kind of dumb
					//it just uses the first one, but it's hard to do better
					//without more information
			}
			
			if(newprop){
				prop.createRelationshipTo(value, tense.toRelay());
			}
			else{
				Traverser isTraverser = prop.traverse( 
						Traverser.Order.BREADTH_FIRST, 
						StopEvaluator.DEPTH_ONE, 
						new ReturnableEvaluator(){
							public boolean isReturnableNode(TraversalPosition pos){
								return pos.currentNode().equals(value);
							}
						}, 
						tense.toRelay(), 
						Direction.OUTGOING );
				
				Iterator<Node> allVals = isTraverser.iterator();
				
				if(!allVals.hasNext()){
					prop.createRelationshipTo(value, tense.toRelay());
				}
			}
			
			tx.success();
		}
		finally{
			tx.finish();
		}
	}
    public void has(Node thing, String propertyname, String valuename){
    	has(thing,propertyname,valuename,Tenses.PRESENT);
    }
    
    public void had(Node thing, String propertyname, String valuename){
    	has(thing,propertyname,valuename,Tenses.PAST);
    }
    
    public void willHave(Node thing, String propertyname, String valuename){
    	has(thing, propertyname, valuename, Tenses.FUTURE);
    }
    
    public void has(Node thing, final String propertyname, final String valuename, Tenses tense){
    	Transaction tx = neo.beginTx();
		
		try{
			
			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop;
			boolean newprop = false;
			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			if(allProps.size()<1){
				prop = newProp(propertyname);
				thing.createRelationshipTo(prop, Relays.HAS);
				newprop = true;
			}
			else{
				prop = allProps.iterator().next();//This is kind of dumb
					//it just uses the first one, but it's hard to do better
					//without more information anyway
			}
			
			Node value;
			
			if(newprop){
				LinkedList<Node> vals = thingsCalled(valuename);
				if(vals.isEmpty()){
					value = newThing(valuename);
				}
				else{
					value = vals.element();
				}
			}
			else{
				Traverser isTraverser = prop.traverse( 
						Traverser.Order.BREADTH_FIRST, 
						StopEvaluator.DEPTH_ONE, 
						new ReturnableEvaluator(){
							public boolean isReturnableNode(TraversalPosition pos){
								return pos.currentNode().getProperty(TYPE).equals(THING)&&
									isCalledQ(pos.currentNode(),valuename);
							}
						}, 
						tense.toRelay(), 
						Direction.OUTGOING );
				
				Iterator<Node> allVals = isTraverser.iterator();
				
				if(!allVals.hasNext()){
					value = newThing(valuename);
				}
				else{
					value = allVals.next();
				}
				
			}
			prop.createRelationshipTo(value, tense.toRelay());
			
			tx.success();
		}
		finally{
			tx.finish();
		}
    }
    
   
    public void has(String thingname, String propertyname, Node value){
    	has(thingname, propertyname, value,Tenses.PRESENT);
    }
    public void had(String thingname, String propertyname, Node value){
    	has(thingname, propertyname, value,Tenses.PAST);
    }
    public void willHave(String thingname, String propertyname, Node value){
    	has(thingname, propertyname, value,Tenses.FUTURE);
    }
    
    public void has(String thingname, final String propertyname, final Node value,Tenses tense){
	    Transaction tx = neo.beginTx();
		
		try{
			Node thing = Thing(thingname);

			if(Thing(thingname)==null){
				thing = newThing(thingname);
			}
			
			has(thing, propertyname, value, tense);
			
			tx.success();
		}
		finally{
			tx.finish();
		}
	}
    
    public void has(String thingname, String propertyname, String value){
    	has(thingname, propertyname, value,Tenses.PRESENT);
    }
    public void had(String thingname, String propertyname, String value){
    	has(thingname, propertyname, value,Tenses.PAST);
    }
    public void willHave(String thingname, String propertyname, String value){
    	has(thingname, propertyname, value,Tenses.FUTURE);
    }
    
    public void has(String thingname, final String propertyname, final String value, Tenses tense){
    	Transaction tx = neo.beginTx();
		
		try{
			Node thing = Thing(thingname);

			if(Thing(thingname)==null){
				thing = newThing(thingname);
			}
			
			has(thing, propertyname, value, tense);
			
			tx.success();
		}
		finally{
			tx.finish();
		}
    }
    
    // THIS IS WHERE I LEFT OFF
    
    //IT DOES NOT HAVE THIS PROPERTY
    
    public void hasNot(Node thing, final String propertyname){
	    Transaction tx = neo.beginTx();
		
		try{

			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );

			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			//delete links to properties it does not have
			
			if(!allProps.isEmpty()){
				for(Node i:allProps){
					i.getSingleRelationship(Relays.HAS, Direction.INCOMING).delete();
				}
			}
			tx.success();
		}
		finally{
			tx.finish();
		}
	}
    
    public void hasNot(String thingname, String propertyname){
    	//TODO unsure what this should mean: NONE, or AT LEAST ONE?
    }
    //IT DOES NOT HAVE THIS PROPERTY-VALUE PAIR
    
    //HELPER FUNCTION
    
    /*public void hasNot(String thingname, final String propertyname, final String value){
	    Transaction tx = neo.beginTx();
		
		try{
			
			Node thing = Thing(thingname);
			
			
			if(Thing(thingname)==null){
				thing = newThing(thingname);
			}
			else{
				thing = Thing(thingname);
			}
			
			//thing is the "thing"
			
			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(NAME).equals(propertyname)&&
							pos.currentNode().getProperty(TYPE).equals(PROP);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop;
			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			if(allProps.size()<1){
				prop = newProp(propertyname);
				thing.createRelationshipTo(prop, Relays.HAS);
			}
			else{
				prop = (Node)(allProps.toArray()[0]);
			}
			
			//prop is the property node
			
			Traverser isTraverser = prop.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals("thing");
						}
					}, 
					Relays.IS, 
					Direction.OUTGOING );
			

			Object[] allVals = isTraverser.getAllNodes().toArray();
			boolean hasVal = false;
			
			int i = 0;
			
			while( i<allVals.length&&hasVal==false){
				//If any nodes' names = 'value', sever the Relationship.
				if(((Node)allVals[i]).getProperty(NAME).equals(value)){
					((Node)allVals[i]).getSingleRelationship(Relays.IS,Direction.INCOMING).delete();
					hasVal = true;
					prop.createRelationshipTo(((Node)allVals[i]), Relays.WAS);
				}
				i++;
			}

			tx.success();
		}
		finally{
			tx.finish();
		}
	}*/
    
    //IT HAS THIS PROPERTY-VALUE PAIR (NUMBER)   
    
    //IT DOES NOT HAVE THIS PROPERTY-VALUE PAIR (NUMBER)
    
    /*public void hasNot(String thingname, final String propertyname, final double value){
	    Transaction tx = neo.beginTx();
		
		try{
			
			Node thing = Thing(thingname);
			
			
			if(Thing(thingname)==null){
				thing = newThing(thingname);
			}
			else{
				thing = Thing(thingname);
			}
			
			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(NAME).equals(propertyname)&&
							pos.currentNode().getProperty(TYPE).equals("property");
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop;
			Collection<Node> allProps = hasTraverser.getAllNodes();
			
			if(allProps.size()<1){
				prop = newProp(propertyname);
				thing.createRelationshipTo(prop, Relays.HAS);
			}
			else{
				prop = (Node)(allProps.toArray()[0]);
			}
			
			Traverser isTraverser = prop.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals("value");
						}
					}, 
					Relays.IS, 
					Direction.OUTGOING );
			
			Object[] allVals = isTraverser.getAllNodes().toArray();
			boolean hasVal= false;
			
			int i = 0;
			while( i<allVals.length&&hasVal==false){
				//If any nodes' values = 'value', the relationship is severed.
				if(((Node)allVals[i]).getProperty(VAL).equals(value)){  //TODO deal with units
					((Node)allVals[i]).getSingleRelationship(Relays.IS, Direction.INCOMING).delete();
				}
				i++;
			}

			tx.success();
		}
		finally{
			tx.finish();
		}
	}*/
    
    
    public LinkedList<Node> s(String thingname, String propertyname){//This gives them ALL
    	Iterator<Node> tc = thingsCalled(thingname).iterator();
    	LinkedList<Node> s = new LinkedList<Node>();
    	
    	while(tc.hasNext()){
    		s.addAll(s(tc.next(),propertyname));
    	}
    	
    	return s;
    	
    }
    
    public LinkedList<Node> s(Node thing, final String propertyname){
    	LinkedList<Node> s = new LinkedList<Node>();
    	
    	Transaction tx = neo.beginTx();
    	
    	try{

			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Iterator<Node> allProps = hasTraverser.iterator();
			
			if(allProps.hasNext()){
				Node prop = allProps.next();
				
				Traverser isTraverser = prop.traverse(
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE,
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(THING);
						}
					}, 
					Relays.IS, 
					Direction.OUTGOING );
				
				Iterator<Node> ises = isTraverser.iterator();
				
				while(ises.hasNext()){
					s.add(ises.next());
				}
				
			}
			
			tx.success();
    	}
    	finally{
    		tx.finish();
    	}
    	return s;
    }
    
    public Node getPropertyNode(Node thing, final String propertyname){
    	Node prop;

    	Transaction tx = neo.beginTx();
    	
    	try{

			Traverser hasTraverser = thing.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(TYPE).equals(PROP)&&
								isCalledQ(pos.currentNode(),propertyname);
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Iterator<Node> p = hasTraverser.iterator();
			
			if(p.hasNext()){
				prop = p.next();
			}
			else{
				prop = null;
			}
			
			tx.success();
    	}
    	finally{
    		tx.finish();
    	}
    	
    	return prop;
    }
    
    public boolean inheritsFromQ(String instancename, final String propertyname, String setname){
    	String[] allParents = inheritsFrom(instancename, propertyname);
    	boolean memberQ = false;
    	for(int i = 0; i< allParents.length; i++){
    		if(allParents[i].equals(setname)){
    			memberQ = true;
    		}
    	}
    	return memberQ;
    }
    
   public String[] inheritsFrom(String instancename, final String propertyname){
	   LinkedList<Node> allNodes = inheritsFrom(Thing(instancename), propertyname);
	   int size = allNodes.size();
	   String[] thingNames = new String[size];

	   for(int i = 0; i<size; i++){
		   thingNames[i] = (String)(allNodes.get(i).getProperty(Proglish.NAME));
	   }
	   return thingNames;
   }

   private LinkedList<Node> inheritsFrom(Node instance, final String propertyname){
    	Transaction tx = neo.beginTx();
    
    	LinkedList<Node> allcategories = new LinkedList<Node>();
    	
    	try{
    		if(instance!=null){
    			
    			Traverser hasTraverser = instance.traverse( 
    					Traverser.Order.BREADTH_FIRST, 
    					StopEvaluator.DEPTH_ONE, 
    					new ReturnableEvaluator(){
    						public boolean isReturnableNode(TraversalPosition pos){
    							return pos.currentNode().getProperty(NAME).equals(propertyname)&&
    							pos.currentNode().getProperty(TYPE).equals("property");
    						}
    					}, 
    					Relays.IS, 
    					Direction.INCOMING );
    			
    			Object[] allProps = hasTraverser.getAllNodes().toArray();
    			
    			if(allProps.length!=0){
	    			
	    			for(int i = 0;i < allProps.length;i++){
	    				allcategories.add(thingThatHas((Node)allProps[i]));
	    			}
	    			
	    			for(int i = 0;i < allcategories.size();i++){
	    				allcategories.addAll(inheritsFrom(allcategories.get(i),propertyname));
	    			}

    			}
    			
    			return allcategories;
    		}
    	
    		tx.success();
    		return allcategories;
    	}
    	finally{
    		tx.finish();
    	}
    }
    
   //these two properties of things have a relationship given by some arithmetic, where prop1 is A and prop2 is B
   
   public void Relay(Node thing1, String propertyname1, Node thing2, String propertyname2, String arithmetic){
	   Node prop1 = getPropertyNode(thing1, propertyname1);
	   Node prop2 = getPropertyNode(thing2, propertyname2);
	   Relay(prop1, arithmetic, prop2);
   }
   
   public void Relay(Node prop1, final String arithmetic, Node prop2){
	   Transaction tx = neo.beginTx();
	   
	   try{
		   Relationship math = prop1.createRelationshipTo(prop2, Relays.ARITHMETIC);
		   math.setProperty("math", arithmetic);
		   tx.success();
	   }
	   finally{
		   tx.finish();
	   }
   }
   
    public void Relay(String thingname1, final String propertyname1, String thingname2, final String propertyname2, String arithmetic){
    	Transaction tx = neo.beginTx();
		
		try{
			
			//get thing1
			
			Node thing1 = Thing(thingname1);
			
			
			if(Thing(thingname1)==null){
				thing1 = newThing(thingname1);
			}
			else{
				thing1 = Thing(thingname1);
			}
			
			//get thing2
			
			Node thing2 = Thing(thingname2);
			
			
			if(Thing(thingname2)==null){
				thing2 = newThing(thingname2);
			}
			else{
				thing2 = Thing(thingname2);
			}
			
			//get property1
			
			Traverser hasTraverser = thing1.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(NAME).equals(propertyname1)&&
							pos.currentNode().getProperty(TYPE).equals("property");
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop1;
			Collection<Node> allProps1 = hasTraverser.getAllNodes();
			
			if(allProps1.size()<1){
				prop1 = newProp(propertyname1);
				thing1.createRelationshipTo(prop1, Relays.HAS);
			}
			else{
				prop1 = (Node)(allProps1.toArray()[0]);
			}
			
			//get property2
			
			hasTraverser = thing2.traverse( 
					Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					new ReturnableEvaluator(){
						public boolean isReturnableNode(TraversalPosition pos){
							return pos.currentNode().getProperty(NAME).equals(propertyname2)&&
							pos.currentNode().getProperty(TYPE).equals("property");
						}
					}, 
					Relays.HAS, 
					Direction.OUTGOING );
			
			Node prop2;
			Collection<Node> allProps2 = hasTraverser.getAllNodes();
			
			if(allProps2.size()<1){
				prop2 = newProp(propertyname1);
				thing2.createRelationshipTo(prop2, Relays.HAS);
			}
			else{
				prop2 = (Node)(allProps2.toArray()[0]);
			}
			
			//create relationship
			
			Relationship rel = prop1.createRelationshipTo(prop2, Relays.ARITHMETIC);
			rel.setProperty("math", arithmetic);
			
			tx.success();
		}
		finally{
			tx.finish();
		}
    }
    
    private Node thingThatHas(Node prop){
    	
    	Traverser itHasTraverser = prop.traverse(
    			Traverser.Order.BREADTH_FIRST, 
    			StopEvaluator.DEPTH_ONE, 
    		new ReturnableEvaluator(){
    			public boolean isReturnableNode(TraversalPosition pos){
					return pos.currentNode().getProperty(TYPE).equals("thing");
				}
    		}, 
    		Relays.HAS, 
			Direction.INCOMING);
    	
    	Object[] nodies = itHasTraverser.getAllNodes().toArray();
    	
    	if(nodies.length>=1){
    		return (Node)nodies[0];
    	}
    	else{
    		return null;
    	}
    	
    	
    }
    
    static void end()
    {
        nameIndex.shutdown();
        propertyIndex.shutdown();
        valueIndex.shutdown();
        neo.shutdown();
    }
    
    /* TODO!!
     * public LinkedList<String> stringsFromNumber(){
    	
    } */
    
/*public double calculate(String numerics){
    	
   	if(mathPattern.matcher(numerics).matches()){
    		
    	}
    	else if(unitsMathPattern.matcher()){
    		
    	}
    	else{
    		return 0;
    	}
    	/*if(integerPattern.matcher(numerics).matches()){
			Scanner numericScan = new Scanner(numerics);
			return (double)numericScan.nextInt();
		}
		else if(doublePattern.matcher(numerics).matches()){
			Scanner numericScan = new Scanner(numerics);
			return numericScan.nextDouble();
		}
		else if(rationalPattern.matcher(numerics).matches()){
			Scanner numericScan = new Scanner(numerics);
			int int1 = numericScan.nextInt();
			numericScan.next("/");
			int int2 = numericScan.nextInt();
			return int1/int2;
		}
		else if(mixedFractionPattern.matcher(numerics).matches()){
			Scanner numericScan = new Scanner(numerics);
			int int1 = 
		}
    	
    }*/
    
    private static void registerShutdownHookForNeoAndIndexService()
    {
        // Registers a shutdown hook for the Neo4j and index service instances
        // so that it shuts down nicely when the VM exits (even if you
        // "Ctrl-C" the running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                end();
            }
        } );
    }
    
    public void show(String message){
    	System.out.println(message);
    }
	
}