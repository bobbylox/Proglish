import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;

import Core.Proglish;

public class inheritanceStopEvaluator implements StopEvaluator {
	
	String propertyname;
	String thingname;
	
	

	public inheritanceStopEvaluator(NeoService neo, String thingname, String property){
		propertyname = property;
	}
			
	public boolean isStopNode(TraversalPosition position){Transaction tx = neo.beginTx();
	
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
				ProglishRelays.HAS, 
				Direction.OUTGOING ); 
		
		tx.success();
		
		return !hasTraverser.getAllNodes().isEmpty();
		
	
		Node node = position.previousNode();
		return Proglish.hasQ((String)(node.getProperty("name")), propertyname);
		
	}
	finally{
		tx.finish();
	}
	}


}
