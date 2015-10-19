package nlubej.gains.dialogs;

import nlubej.gains.DBAdapter;
import nlubej.gains.R;
import nlubej.gains.adapters.StrengthLogAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class GraphPointDialog extends DialogFragment {

	View view;
	DBAdapter dbHelper;
	long exerciseId;
	int workoutNum;
	double weight;
	String unitSystem;
	ListView graphPointList;
	SharedPreferences prefs;
	int[] workoutNums;
	int[] sets;
	double[] weights;
	int[] reps;
	String[] dates;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {



		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_graph_point, null);	
		graphPointList = (ListView) view.findViewById(R.id.graphPointList);

		exerciseId = getArguments().getLong("exerciseId");
		weight = getArguments().getDouble("weight");
		workoutNum = getArguments().getInt("workoutNum");

		if(prefs.getString("unitSystem", "").compareTo("metric")==0)
			unitSystem = "metric";
		else
			unitSystem = "imperial";
		
		
		getLastWorkoutData();
		builder.setView(view);
		
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		 final AlertDialog alertDialog = builder.create();
		 alertDialog.getWindow().requestFeature(STYLE_NO_TITLE);
		alertDialog.show();
	
		alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
		{            
			@Override
			public void onClick(View v)
			{
				alertDialog.dismiss();
			}
		});

		return alertDialog;
	}

	private void getLastWorkoutData() {
		Cursor c = null;
		try {
			dbHelper.open();
			
			c = dbHelper.getPrevLog(exerciseId,workoutNum);
			sets = null;
			weights = null;
			reps = null;
			dates = null;

			if(c.getCount() != 0) {
				c.moveToFirst();
				sets = new int[c.getCount()+1];
				weights = new double[c.getCount()+1];
				reps = new int[c.getCount()+1];
				workoutNums = new int[c.getCount()+1];
				dates = new String[c.getCount()+1];
				
				int i=0;

				sets[i] = 0;
				weights[i] = 0;
				reps[i] = 0;
				workoutNums[i] = workoutNum;
				dates[i] = c.getString(7);
				i++;
				
				do{
					sets[i] = c.getInt(1);
					weights[i] = c.getDouble(2);
					reps[i] = c.getInt(3);
					workoutNums[i] = 0;	
					dates[i] = "";
					i++;
				}while(c.moveToNext());
			}
		}
		catch(NullPointerException e)
		{

		}
		finally {
			c.close();
			dbHelper.close();
			graphPointList.setAdapter(new StrengthLogAdapter(getFragmentManager(),getActivity().getApplicationContext(),exerciseId, sets, weights, reps, workoutNums, dates, unitSystem));

		}

	}
}