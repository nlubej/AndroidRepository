package nlubej.gains.workoutset;

public class FirstWorkoutSet extends WorkoutSet {

	private String set;
	private String kg;
	private String rep;
	
	public FirstWorkoutSet(String set, String kg, String rep) {
		this.set = set;
		this.kg = kg;
		this.rep = rep;
	}
	
	public FirstWorkoutSet() {
	}
	
	public String getSet() {
		return set;
	}
	
	public void setSet(String set) {
		this.set = set;
	}
	
	public String getKg() {
		return kg;
	}
	
	public void setKg(String kg) {
		this.kg = kg;
	}
	
	public String getRep() {
		return rep;
	}
	
	public void setRep(String rep) {
		this.rep = rep;
	}
	
	
}
