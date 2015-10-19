package nlubej.gains.xmlbackup;

public class XmlLog{
	private String set;
	private String weight;
	private String rep;
	private String duration;
	private String distance;
	private String date;
	private String workoutNum;
	private String type;
	

	public XmlLog(String string, String string2, String string3, String duration, String string4, String date, String string5, String type) {
		this.set = string;
		this.weight = string2;
		this.rep = string3;
		this.duration = duration;
		this.distance = string4;
		this.date = date;
		this.workoutNum = string5;
		this.type = type;	
	}
	
	
	public String getSet() {
		return set;
	}


	public void setSet(String set) {
		this.set = set;
	}


	public String getWeight() {
		return weight;
	}


	public void setWeight(String weight) {
		this.weight = weight;
	}


	public String getRep() {
		return rep;
	}


	public void setRep(String rep) {
		this.rep = rep;
	}


	public String getDuration() {
		return duration;
	}


	public void setDuration(String duration) {
		this.duration = duration;
	}


	public String getDistance() {
		return distance;
	}


	public void setDistance(String distance) {
		this.distance = distance;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public String getWorkoutNum() {
		return workoutNum;
	}


	public void setWorkoutNum(String workoutNum) {
		this.workoutNum = workoutNum;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}