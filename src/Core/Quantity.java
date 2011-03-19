package Core;

public class Quantity{
	
	double value;
	String unit;

	public Quantity(double val, String u){
		value = val;
		unit = u;
	}

	public Quantity(int val, String u){
		value = (double)val;
		unit = u;
	}
	
	/*TODO: practically everything
	 * in particular:
	 * 	- verify units
	 *  - implement conversions
	*/
	
}
