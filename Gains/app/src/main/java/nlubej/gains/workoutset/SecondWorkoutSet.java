package nlubej.gains.workoutset;

public class SecondWorkoutSet extends WorkoutSet{

	private String hours;
	private String minute;
	private String second;
	private String distance;
	
	
	
	public SecondWorkoutSet(String hours, String minute, String second, String distance) {
		super();
		this.hours = hours;
		this.minute = minute;
		this.second = second;
		this.distance = distance;
	}
	
	public String getHours() {
		return hours;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getMinute() {
		return minute;
	}
	public void setMinute(String minute) {
		this.minute = minute;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String note) {
		this.distance = note;
	}
	
	
	
}
