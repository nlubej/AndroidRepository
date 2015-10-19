package nlubej.gains.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import nlubej.gains.R;


public class MySpinnerAdapter extends ArrayAdapter<String> {

	private Context c;
	private String[] exerciseName;
	private String[] routineName;
	
	public MySpinnerAdapter(Context ctx, int txtViewResourceId, String[] exerciseName, String[] routineName) { 
		super(ctx, txtViewResourceId, exerciseName); 
		this.exerciseName = exerciseName;
		this.routineName = routineName;
		this.c = ctx;
	}
	
	
	@Override 
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) { 
		return getCustomView(position, cnvtView, prnt); 
	}
	
	@Override 
	public View getView(int pos, View cnvtView, ViewGroup prnt) { 
		return getCustomView(pos, cnvtView, prnt);
	}

	
	public View getCustomView(int position, View convertView, ViewGroup parent) { 
		LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		
		View mySpinner = inflater.inflate(R.layout.custom_spinner, parent, false);
		TextView main_text = (TextView) mySpinner .findViewById(R.id.spinner_exercise_name); 
		main_text.setText(exerciseName[position]); 
		TextView subSpinner = (TextView) mySpinner .findViewById(R.id.spinner_routine_name); 
		subSpinner.setText(routineName[position]); 
		
		return mySpinner; 
	}
}
