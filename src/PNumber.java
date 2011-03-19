import java.util.Scanner;
import java.util.StringTokenizer;
import java.lang.Math;

public class PNumber implements Comparable<PNumber>{
	
	public boolean subjective = false;
	private double value = 0;
	private double positiveError = 0;
	private double negativeError = 0;
	private String unit = "unity";

	public PNumber(){
	}
	public PNumber(double val){
		value = val;
	}
	public PNumber(int val){
		new PNumber(new Integer(val).doubleValue());
	}
	public PNumber(double val, String u){
		value = val;
		setUnit(u);
	}
	public PNumber(int val, String u){
		new PNumber(new Integer(val).doubleValue(),u);
	}
	public PNumber(double val, double err){
		value = val;
		positiveError = err;
		negativeError = err;
	}
	public PNumber(int val, double err){
		new PNumber(new Integer(val).doubleValue(),err);
	}
	public PNumber(int val, int err){
		new PNumber(new Integer(val).doubleValue(),new Integer(err).doubleValue());
	}
	public PNumber(double val, int err){
		new PNumber(val,new Integer(err).doubleValue());
	}
	public PNumber(double val, double err, String uni){
		value = val;
		setUnit(uni);
		positiveError = err;
		negativeError = err;
	}
	public PNumber(int val, double err,String u){
		new PNumber(new Integer(val).doubleValue(),err,u);
	}
	public PNumber(int val, int err,String u){
		new PNumber(new Integer(val).doubleValue(),new Integer(err).doubleValue(),u);
	}
	public PNumber(double val, int err,String u){
		new PNumber(val,new Integer(err).doubleValue(),u);
	}
	public PNumber(double val, double neg, double pos){
		value = val;
		positiveError = pos;
		negativeError = neg;
	}
	public PNumber(double val, double pos, double neg, String uni){
		value = val;
		setUnit(uni);
		positiveError = pos;
		negativeError = neg;
	}
	public PNumber(String number) throws Exception{
		Scanner scan = new Scanner(number);
		
		if(scan.hasNextDouble()){
			value = scan.nextDouble();
		}
		else if(scan.hasNextInt()){
			value = (double)scan.nextInt();
		}
		else{
			throw new Exception("invalid number form");
		}
		
		if(scan.hasNextDouble()){
			positiveError = scan.nextDouble();
		}
		if(scan.hasNextDouble()){
			negativeError = Math.abs(scan.nextDouble());
		}
		else{negativeError=positiveError;}
		
		if(!scan.hasNextDouble()&&!scan.hasNextInt()&&scan.hasNext()){
			unit = scan.next();
		}
		
	}
	public PNumber(Scanner scan) throws Exception{
		
		if(scan.hasNextDouble()){
			value = scan.nextDouble();
		}
		else if(scan.hasNextInt()){
			value = (double)scan.nextInt();
		}
		else{
			throw new Exception("invalid number form");
		}
		
		if(scan.hasNextDouble()){
			positiveError = scan.nextDouble();
		}
		if(scan.hasNextDouble()){
			negativeError = Math.abs(scan.nextDouble());
		}
		else{negativeError=positiveError;}
		
		if(!scan.hasNextDouble()&&!scan.hasNextInt()&&scan.hasNext()){
			unit = scan.next();
		}
		
	}
	/*These getters and setters should never have to be used externally*/
	public void setValue(double value){
		this.value=value;
	}
	public double getValue(){
		return value;
	}
	
	public PNumber withoutUnit(){
		PNumber temp = new PNumber(value, negativeError,positiveError);
		return temp;
	}
	/**/
	public double getLowerBound(){
		return value-negativeError;
	}
	
	public double getUpperBound(){
		return value+positiveError;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}
	
	public PNumber power(PNumber exp){
		
		double t = Math.pow(value, exp.getValue());
		
		PNumber temp = new PNumber(
				t,
				Math.pow(value+positiveError, exp.getUpperBound())-t,
				t-Math.pow(value-negativeError, exp.getLowerBound()),
				this.unit
		);
		return temp;
	}
	
	public PNumber plus(PNumber sum){
		
		PNumber temp = sum.convert(this.unit);
		
		double t = value+temp.getValue();
		
		temp = new PNumber(
				t,
				((value+positiveError)+sum.getUpperBound())-t,
				t-((value-negativeError)+sum.getLowerBound()),
				this.unit
		);
		return temp;
	}
	
	public PNumber minus(PNumber sub){
		
		PNumber temp = sub.convert(this.unit);
		
		double t = value-temp.getValue();
		
		temp = new PNumber(
				t,
				((value+positiveError)-sub.getUpperBound())-t,
				t-((value-negativeError)-sub.getLowerBound()),
				this.unit
		);
		return temp;
	}
	
	public PNumber times(PNumber sub){
			
			PNumber temp = sub.convert(this.unit);
			
			double t = value*temp.getValue();
			
			temp = new PNumber(
					t,
					((value+positiveError)*sub.getUpperBound())-t,
					t-((value-negativeError)*sub.getLowerBound()),
					this.unit
			);
			return temp;
		}
	
	public PNumber dividedBy(PNumber sub){
		
		PNumber temp = sub.convert(this.unit);
		
		double t = value/temp.getValue();
		
		temp = new PNumber(
				t,
				((value+positiveError)/sub.getUpperBound())-t,
				t-((value-negativeError)/sub.getLowerBound()),
				this.unit
		);
		return temp;
	}
	
	/*TODO Write a unit converter procedure for equation-based conversions i.e. temperature*/
	public PNumber convert(String unit){
		
		if(unit.equals("unity")){
			return new PNumber(value,positiveError,negativeError,"unity");
		}
		if(unit.equals(this.unit)){
			return new PNumber(value,positiveError,negativeError,unit);
		}
		
		String currentline="";
		double conv1 = 0.0;
		double conv2 = 0.0;
		FileStringReader reader = 
			new FileStringReader("/Users/robertlockhart/Documents/EclipseWorkspace/Proglish/unitConversions.txt");
		
		currentline = reader.readLine();
		StringTokenizer str = new StringTokenizer(currentline);
		
		Integer linecount = new Integer(str.nextToken());
		int lines = linecount.intValue();
		int linecounter = 0;
		
		while((!currentline.startsWith(this.unit))&&(!currentline.startsWith(unit))&&linecounter<=lines){
			currentline = reader.readLine();
			linecounter++;
			};
		
		StringTokenizer getconv1 = new StringTokenizer(currentline);
		String convert = getconv1.nextToken();
		convert = getconv1.nextToken();
		Double convgetter = new Double(convert);
		
		if(currentline.startsWith(this.unit)){
			conv1 = convgetter.doubleValue();
		}
		else { conv2 = convgetter.doubleValue();}
		
		while((!currentline.startsWith(this.unit))&&(!currentline.startsWith(unit))&&linecounter<=lines&&(!currentline.equals(""))){
			currentline = reader.readLine();
			linecounter++;
		}
		
		currentline = reader.readLine();
		
		if (currentline.startsWith(unit)){
			StringTokenizer getconv2 = new StringTokenizer(currentline);
			convert = getconv2.nextToken();
			convert = getconv2.nextToken();
			convgetter = new Double(convert);
			conv2 = convgetter.doubleValue();
			conv1 = conv1*(1/conv2);
			PNumber temp = new PNumber(value*conv1, positiveError*conv1, negativeError*conv1, unit);
			return temp;
		}
		else if (currentline.startsWith(this.unit)){
			StringTokenizer getconv2 = new StringTokenizer(currentline);
			convert = getconv2.nextToken();
			convert = getconv2.nextToken();
			convgetter = new Double(convert);
			conv1 = convgetter.doubleValue();
			conv1 = conv1*(1/conv2);
			PNumber temp = new PNumber(value*conv1, positiveError*conv1, negativeError*conv1, unit);
			return temp;
		}
		else {
			return null;
		}
		
		
	}
	
	public int compareTo(PNumber other){
		PNumber newOther = other.convert(this.unit);
		
		if(this.value==newOther.getValue()){
			return 0;
		}
		else if(this.value>newOther.getValue()){
			return 1;
		}
		else{
			return -1;
		}
	}
	
	public String toString(){
		
		String unitName;
		String pos;
		String neg;
		
			if(unit=="unity"){
				unitName = "";}
			else{ unitName = unit;}
			
			if(positiveError == 0){
				pos = "";
			}
			else {pos = " +"+positiveError;}
			
			if(negativeError == 0){
				neg = "";
			}
			else {neg = " or -"+negativeError;}
		
		return value+pos+neg+" "+unitName;
	}
}
