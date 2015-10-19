package nlubej.gains.dialogs;

import nlubej.gains.DBAdapter;
import nlubej.gains.R;
import nlubej.gains.adapters.CardioLogAdapter;
import nlubej.gains.adapters.StrengthLogAdapter;
import nlubej.gains.dialogs.AddRoutineDialog.NoticeDialogListener;
import nlubej.gains.listeners.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ShowPrevWorkoutDialog extends DialogFragment {
	
	View view;
	DBAdapter dbHelper;
	long exerciseId;
	String type =  "";
	int lastWorkoutNum,mode;
	int[] workoutNums;
	int[] sets;
	double[] weights;
	int[] reps;
	String[] duration;
	String[] dates;
	double[] distance;
	String unitSystem;
	ListView logList;
	SharedPreferences prefs;
	TextView text;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		

		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_show_prev_workout, null);	
		logList = (ListView) view.findViewById(R.id.lastLogList);
		text = (TextView)view.findViewById(R.id.prevWorkoutText);
		text.setText("Previous Workout");
		exerciseId = getArguments().getLong("exerciseId");
		lastWorkoutNum = getArguments().getInt("lastWorkoutNum");
		
		if(prefs.getString("unitSystem", "").compareTo("metric")==0)
			unitSystem = "metric";
		else
			unitSystem = "imperial";
		
		if(lastWorkoutNum == -1) 
		{
			dbHelper.open();
			lastWorkoutNum = dbHelper.getLastWorkoutNum(exerciseId);
			Log.d("nlubej", "last exercise num = " + lastWorkoutNum );
			dbHelper.close();
			
		}

		
		
		getLastWorkoutData();
		builder.setView(view);
		
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
		alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
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
			c = dbHelper.getPrevLog(exerciseId,lastWorkoutNum);
			type = dbHelper.getType(exerciseId);
		
			sets = null;
			weights = null;
			reps = null;
			duration = null;
			distance = null;
			mode = 0;
			dates = null;
			

			if(c.getCount() != 0) {
				c.moveToFirst();
				if(type.compareTo("Strength") == 0)
				{
					mode = 0;
					sets = new int[c.getCount()+1];
					weights = new double[c.getCount()+1];
					reps = new int[c.getCount()+1];
					workoutNums = new int[c.getCount()+1];
					dates = new String[c.getCount()+1];
					
					int i=0;
					int workoutNum = 0;
					
					sets[i] = 0;
					weights[i] = 0;
					reps[i] = 0;
					workoutNums[i] = lastWorkoutNum;
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
				else 
				{
					sets = new int[c.getCount()+ 1];
					duration = new String[c.getCount()+ 1];
					distance = new double[c.getCount()+ 1];
					workoutNums = new int[c.getCount()+ 1];
					dates = new String[c.getCount()+1];
					
					mode = 1;
					
					int i=0;
					int workoutNum = 1;
					
					duration[i] = "";
					distance[i] = 0.0;
					workoutNums[i] = lastWorkoutNum;
					dates[i] = c.getString(7);
					i++;
	
					
					do{
						sets[i] = c.getInt(1);
						duration[i] = c.getString(4);
						distance[i] = c.getDouble(5);
						workoutNums[i] = 0;
						dates[i] = "";
							
						i++;
					}while(c.moveToNext());
				}
			}
			else {
				text.setText("No Previous Workout");
			}
		}
		catch(NullPointerException e)
		{
			
		}
		finally {
			c.close();
			dbHelper.close();
			if(mode == 0){
				logList.setAdapter(new StrengthLogAdapter(getFragmentManager(), getActivity().getApplicationContext(),exerciseId, sets, weights, reps, workoutNums, dates, unitSystem));
			}
			else
			{
				logList.setAdapter(new CardioLogAdapter(getFragmentManager(), getActivity().getApplicationContext(), exerciseId, sets, duration, distance, workoutNums, dates, unitSystem));
			}
		}
		
	}
}