package Core;

public class Date {
	
	public int year;
	public int month;
	public int day;
	public int hour;
	public int minute;
	public double second;
	
	
	
	public Date(int yr){
		year = yr;
	}
	public Date(int yr, int mon){
		year = yr;
		month = mon;
	}
	public Date(int yr, int mon, int d){
		year = yr;
		month = mon;
		day = d;
	}
	public Date(int yr, int mon, int d, int hr, int min){
		year = yr;
		month = mon;
		day = d;
		hour = hr;
		minute = min;
	}
	public Date(int yr, int mon, int d, int hr, int min, double sec){
		year = yr;
		month = mon;
		day = d;
		hour = hr;
		minute = min;
		second = sec;
	}
	public Date(int hr, int min, double sec){
		hour = hr;
		minute = min;
		second = sec;
	}
	public String format(){
		return hour+":"+minute+":"+second+" "+month+"/"+day+"/"+year;
	}

}
