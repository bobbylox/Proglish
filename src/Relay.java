import java.util.Stack;

public /*abstract*/ class Relay extends Everything{
	
	public String name;
	public PNumber probability = new PNumber(1.0);
	
	private Stack<String> evaluationStack = new Stack<String>();
	
	public Relay(String name, Stack<String> eval){
		this.name = name;
		evaluationStack = eval;
	}
	
	public Relay(String name, String eval){
		//TODO parse strings to facts
	}
	
	public Relay(String name, Fact fact1){
		
		Relay(name,)
	}
	
	public String getName(){
		return name;
	}
	
	public Relay simplify(){
		//TODO
		Relay temp = new Relay(name,new Stack<String>());
		
		return temp;
	}

}
