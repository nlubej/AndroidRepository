package nlubej.gains;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import nlubej.gains.Adapters.ProgramAdapter;
import nlubej.gains.Views.Program;
import nlubej.gains.Adapters.CardioLogAdapter;
import nlubej.gains.Adapters.StrengthLogAdapter;
import nlubej.gains.Adapters.WeightLogAdapter;
import nlubej.gains.interfaces.onActionSubmit;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */



public class Logg extends Fragment implements OnItemSelectedListener, onActionSubmit
{

	int id;
	int prevId;
	int num = 1;
	int selectedExercise = 1;

	String unitSystem;
	String[] exerciseName;
	String[] routineName;
	long[] exerciseId;
	long [] routineId;
	String[] duration;
	double[] distance;

	int[] workoutNums;
	int[] sets;
	double[] weights;
	int[] reps;
	long programId = 0;
	String[] dates;

	ArrayList<RelativeLayout> layouts;
	SharedPreferences prefs;
	ImageView expand;
	Spinner spinner;
	boolean showGraph = false;
	View fragment;
	RelativeLayout rlayout;
	RelativeLayout expandLayout;
	DBAdapter dbHelper;
	RelativeLayout log_layout;
	RelativeLayout log_seperator;
	RelativeLayout expandView;
	ArrayList<RelativeLayout> rlayouts;
	TextView warning;
	ListView logList;
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment = inflater.inflate(R.layout.log, container, false);
		prefs = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
		spinner = (Spinner) fragment.findViewById(R.id.spinner);
		rlayout = (RelativeLayout)fragment.findViewById(R.id.rlayout);
		rlayouts = new ArrayList<RelativeLayout>();
		logList = (ListView) fragment.findViewById(R.id.logList);
		layouts = new ArrayList<RelativeLayout>();
		warning = (TextView)fragment.findViewById(R.id.warninLog);
		unitSystem = "";
		
		

		if(prefs.getString("unitSystem", "").compareTo("metric")==0)
			unitSystem = "metric";
		else
			unitSystem = "imperial";

		Log.d("nlubej", "unit system : " + unitSystem);

		Log.d("nlubej", "unit system  " + prefs.getString("unitSystem", ""));

		this.setHasOptionsMenu(true);
		init();

		return fragment;
	}


	public void init() 
	{
		dbHelper = new DBAdapter(fragment.getContext());
		exerciseName = null;
		exerciseId = null;
		routineId = null;
		routineName = null;
		dbHelper.open(); 
		programId = Long.parseLong(prefs.getString("defaultProgram", "0"));
		Cursor c = dbHelper.getRoutineIds(programId);
		int numC = c.getCount();

		try {
			if(c.getCount() != 0) {
				routineId = new long[c.getCount()];
				int i=0;
				while(c.moveToNext()){
					routineId[i] = c.getLong(0);
					Log.d("nlubej", routineId[0] + "");
					i++;
				}
			}
		}finally {
			c.close();
			dbHelper.close();
		}

		if(numC != 0) {
			dbHelper.open();
			Cursor d = dbHelper.getExerciseFromIds(routineId);

			try {
				if(d.getCount() != 0) {
					Log.d("nlubej", d.getCount() + " count");
					int i=2;
					exerciseName = new String[d.getCount()+2];
					routineName = new String[d.getCount()+2];
					exerciseId = new long[d.getCount()+2];
					routineId = new long[d.getCount()+2];
					while(d.moveToNext()){
						exerciseId[i] = d.getLong(0);
						exerciseName[i] = d.getString(1);
						routineName[i] = d.getString(2);
						routineId[i] = d.getLong(3);
						i++;

						Log.d("nlubej", d.getLong(0) + "         " + d.getString(1) + "       "  + d.getString(2));

					}

					exerciseName[0] = "";
					routineName[0] = "Select an exercise from default program";

					exerciseName[1] = "Weight";
					routineName[1] = "User stats";
				}
				else 
				{
					exerciseName = new String[2];
					routineName = new String[2];

					exerciseName[0] = "";
					routineName[0] = "No exercises added";
					
					exerciseName[1] = "Weight";
					routineName[1] = "User stats";
				}
			}finally {
				d.close();
				dbHelper.close();

			}
		}
		else
		{
			exerciseName = new String[2];
			routineName = new String[2];

			exerciseName[0] = "";
			routineName[0] = "No exercises added";
			
			exerciseName[1] = "Weight";
			routineName[1] = "User stats";

		}




		prevId = R.id.spinner;
		id = 1;

		//init spinner




	}


	private void initExercise() {
		getActivity().supportInvalidateOptionsMenu();

		sets = null;
		weights = null;
		reps = null;
		workoutNums = null;
		duration = null;
		distance = null;
		int mode = 0;

		if(selectedExercise == 1) //dobit ho�emo log za weight ne pa za strength in cardio
		{
			dbHelper.open();
			Cursor c = dbHelper.getWeightLog();
			Log.i("nlubej","kok jih je? " + c.getCount());
			int i=0;
			
			if(c.getCount() != 0) {
				
				int num = dbHelper.getLastWorkoutNum(0);
				Log.i("nlubej","last workout num " + num);
				
				dates = new String[c.getCount()+num];
				weights = new double[c.getCount()+num];
				
				while(c.moveToNext())
				{
					dates[i] = c.getString(1);
					weights[i] = -1;
					i++;
					weights[i] = c.getDouble(0);
					dates[i] = c.getString(1);
					i++;

				}
				logList.setAdapter(new WeightLogAdapter(fragment.getContext(), weights, dates ,unitSystem));
				
			}
			c.close();
			dbHelper.close();
		}
		else if(selectedExercise == 0) 
		{
			logList.setAdapter(null);
		}
		else 
		{
			try {
				dbHelper.open(); 
				int num = dbHelper.getLastWorkoutNum(exerciseId[selectedExercise]);

				String type=  "";
				type = dbHelper.getType(exerciseId[selectedExercise]);

				Cursor c = dbHelper.getLog(exerciseId[selectedExercise]);
				if(c.getCount() != 0) {
					if(type.compareTo("Strength") == 0)
					{
						c.moveToFirst();
						mode = 0;
						sets = new int[c.getCount()+num];
						weights = new double[c.getCount()+num];
						reps = new int[c.getCount()+num];
						workoutNums = new int[c.getCount()+num];
						dates = new String[c.getCount()+num];
						Log.i("nlubej", "count " + c.getCount()+" + " + num);
						int i=0;
						int workoutNum = 1;

						sets[i] = 0;
						weights[i] = 0;
						reps[i] = 0;
						workoutNums[i] = workoutNum;
						dates[i] = c.getString(7);
						i++;


						do{
							if(c.getInt(6) != workoutNum){
								Log.d("nlubej", "workoutNum : " + c.getInt(6));
								workoutNum = c.getInt(6);
								sets[i] = 0;
								weights[i] = 0;
								reps[i] = 0;
								workoutNums[i] = workoutNum;
								dates[i] = c.getString(7);
								i++;

							}

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
						c.moveToFirst();
						
						sets = new int[c.getCount()+num];
						duration = new String[c.getCount()+num];
						distance = new double[c.getCount()+num];
						workoutNums = new int[c.getCount()+num];
						dates = new String[c.getCount()+num];
						mode = 1;

						int i=0;
						int workoutNum = 1;
						
						sets[i] = 0;
						duration[i] = "";
						distance[i] = 0.0;
						workoutNums[i] = workoutNum;
						dates[i] = c.getString(7);
						i++;

						
						do{
						Log.i("nlubej", c.getInt(1) + "    duration: " + c.getString(4) + " workoutNum: " + c.getInt(6));
							if(c.getInt(6) != workoutNum){
								Log.d("nlubej", "workoutNum : " + c.getInt(6));
								workoutNum = c.getInt(6);
								sets[i] = 0;
								duration[i] = "";
								distance[i] = 0.0;
								workoutNums[i] = workoutNum;
								dates[i] = c.getString(7);
								i++;

							}
							sets[i] = c.getInt(1);
							duration[i] = c.getString(4);
							distance[i] = c.getDouble(5);
							workoutNums[i] = 0;
							dates[i] = "";

							i++;
							Log.d("nlubej", c.getInt(1) + "    " + c.getString(4));
						}while(c.moveToNext());
					}
				}
				c.close();
			}
			catch(NullPointerException e)
			{

			}
			finally {
				
				dbHelper.close();
				if(mode == 0){
					logList.setAdapter(new StrengthLogAdapter(getActivity().getFragmentManager(), Logg.this,fragment.getContext(), exerciseId[selectedExercise], sets, weights, reps, workoutNums, dates, unitSystem));
				}
				else
				{
					logList.setAdapter(new CardioLogAdapter(getActivity().getFragmentManager(), Logg.this, fragment.getContext(), exerciseId[selectedExercise], sets, duration, distance, workoutNums,dates, unitSystem));
				}
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		selectedExercise = position;
		Log.i("nlubej",position + "");
		if(exerciseId != null) {
			if(position == 1)
			{
				showGraph = false;
				Log.i("nlubej","clicked weight");
			}
			else {
				dbHelper.open();
				String type = dbHelper.getType(exerciseId[selectedExercise]);
				dbHelper.close();

				if(selectedExercise != 0 && type.compareTo("Strength") == 0) {
					showGraph = true;
				}
				else if(type.compareTo("Strength") != 0)
					showGraph = false;
			}
		}
		Log.e("nlubej", "default program: " + Integer.parseInt(prefs.getString("DEFAULT_PROGRAM", "-99")));
		initExercise();
	}

	@Override 
	public void onResume(){
		super.onResume();
		programId = Long.parseLong(prefs.getString("DefaultProgram", "6"));
		Log.i("nlubej", "New value of default program: " + programId);
		init();
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}


	//MENU

	@Override 
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.log_menu, menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.graph) {
			if(showGraph)
			{
			/*	Intent i = new Intent(fragment.getContext(), nlubej.gains.LogGraph.class);
				i.putExtra("exerciseID",exerciseId[selectedExercise]);
				i.putExtra("routineID", routineId[selectedExercise]); 
				i.putExtra("exerciseName", exerciseName[selectedExercise]); 
				startActivity(i);*/
			}
		}
		return true;
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if(showGraph)
			menu.getItem(0).setVisible(true);
		else
			menu.getItem(0).setVisible(false);	
	}


	@Override
	public void OnSubmit (String friendEmail) {
		initExercise();
		
	}











}