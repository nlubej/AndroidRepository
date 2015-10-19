package nlubej.gains;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import nlubej.gains.dialogs.AddExerciseCommentDialog;
import nlubej.gains.dialogs.AddExerciseDialog;
import nlubej.gains.dialogs.AddProgramDialog;
import nlubej.gains.dialogs.ShowPrevWorkoutDialog;
import nlubej.gains.listeners.RepListener;
import nlubej.gains.listeners.StartEditListener;
import nlubej.gains.listeners.WeightListener;
import nlubej.gains.workoutset.FirstWorkoutSet;
import nlubej.gains.workoutset.SecondWorkoutSet;
import nlubej.gains.workoutset.WorkoutSet;


public class Start extends Activity implements  OnClickListener, OnItemSelectedListener {

	SharedPreferences prefs;
	RelativeLayout rlayout;
	RelativeLayout rlayout2;
	ArrayList<RelativeLayout> rlayouts;
	WorkoutSet workoutSet;
	
    
	RelativeLayout mInputView;
	DBAdapter dbHelper;
	int reloadFrom = 0;
	boolean setsNotDeleted = true;
	boolean isClicked;
	int lastWorkoutNum = -1;

	int default_programId = -1;
	int reloadTo = 0;
	int id = 1;
	int startFromId = 1;
	int clickedPosition;
	int prevLayout2Id;   //id za relative layout2
	double progressValue = 0;
	int numSetsAdded = 1; //�tevilo sets added per exercise
	final int bars = 10;
	int screenType = -1;
	int prevscreenType = 0;
	int numExercises = 0;
	long routineID = 1;
	int exer_count = 0;
	boolean showWarning = false;
	//zacetek
	Spinner spinner;
	ImageView zacScreenNext;
	ImageView zacScreenPrev;
	ProgressBar zacProgressBar;
	TextView warning;
	
	//screen 1
	View space;
	TextView exerciseName;
	ProgressBar progressBar;
	ListView list;
	ImageView next;
	ImageView prev;
	EditText edit_reps;
	EditText edit_kg;
	ImageView Rplus;
	ImageView Rminus;
	ImageView Wplus;
	ImageView Wminus;
	
	//screen 2
	TextView exerciseName2;
	ProgressBar progressBar2;
	ListView list2;
	ImageView next2;
	ImageView prev2;
	Button save;
	Button save2;
	Button clear;
	Button clear2;
	EditText edit_h;
	EditText edit_m;
	EditText edit_s;
	EditText edit_distance;
	
	int width;
    int height;
    
	
	String[] exercises = null;
	long[] ids = null;
	String[] exerciseType = null;
	int[] prevId = null;
	int[] setNum = null;
	
	//for the spinner
	List<String> routines = null;
	List<Long> routineIds = null;
	
	
	
	
	ArrayList<ArrayList<WorkoutSet>> workoutSets;
	ArrayList<ArrayList<RelativeLayout>> layouts;
	
	private ViewFlipper switcher;
	
	int programID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
      
        //Initialize
        workoutSets = new ArrayList<ArrayList<WorkoutSet>>();
        layouts = new ArrayList<ArrayList<RelativeLayout>>();
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double increase;
        try {
        	increase = Double.parseDouble(prefs.getString("weightIncrement",""));
        }
        catch(NumberFormatException e)
        {
        	e.printStackTrace();
        	increase = 2.5;
        }
        
        
        
        //zacetek
        zacScreenNext = (ImageView)findViewById(R.id.zacScreenNext);
        zacScreenPrev = (ImageView)findViewById(R.id.zacScreenPrev);
        zacProgressBar = (ProgressBar)findViewById(R.id.zacProgressBar);
        spinner = (Spinner)findViewById(R.id.spinner);
        warning = (TextView)findViewById(R.id.warning);
        //screen 1
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        next = (ImageView)findViewById(R.id.screenNext);
        prev = (ImageView)findViewById(R.id.ScreenPrev);
        exerciseName = (TextView)findViewById(R.id.exerciseName);
        save = (Button) findViewById(R.id.save);
        clear = (Button) findViewById(R.id.clear);
        rlayout = (RelativeLayout)findViewById(R.id.rlayout);
        rlayout2 = (RelativeLayout)findViewById(R.id.rlayout2);
        edit_reps =(EditText)findViewById(R.id.editReps);
        edit_kg =(EditText)findViewById(R.id.editWeight);
        Rplus = (ImageView)findViewById(R.id.repsPlus);
        Rminus = (ImageView)findViewById(R.id.repsMinus);
        Wplus = (ImageView)findViewById(R.id.weightPlus);
        Wminus = (ImageView)findViewById(R.id.weightMinus);
        
        //screen 2
        progressBar2 = (ProgressBar)findViewById(R.id.ProgressBar2);
        next2 = (ImageView)findViewById(R.id.ScreenNext2);
        prev2 = (ImageView)findViewById(R.id.ScreenPrev2);
        exerciseName2 = (TextView)findViewById(R.id.exerciseName2);
        save2 = (Button) findViewById(R.id.save2);
        clear2 = (Button) findViewById(R.id.clear2);
        edit_h =(EditText)findViewById(R.id.h);
        edit_m =(EditText)findViewById(R.id.m);
        edit_s =(EditText)findViewById(R.id.s);
        edit_distance = (EditText)findViewById(R.id.editDistance);
        
       
        switcher = (ViewFlipper) findViewById(R.id.viewFlipper);
        
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        next2.setOnClickListener(this);
        prev2.setOnClickListener(this);
        save.setOnClickListener(this);
        save2.setOnClickListener(this);
        clear.setOnClickListener(this);
        clear2.setOnClickListener(this);
        zacScreenNext.setOnClickListener(this);
        zacScreenPrev.setOnClickListener(this);
        Wplus.setOnClickListener(new WeightListener(edit_kg,"+",increase));
        Wminus.setOnClickListener(new WeightListener(edit_kg,"-",increase));
        Rplus.setOnClickListener(new RepListener(edit_reps,"+"));
        Rminus.setOnClickListener(new RepListener(edit_reps,"-"));
        

        edit_distance.setOnFocusChangeListener(new StartEditListener(edit_distance,"0"));
        edit_h.setOnFocusChangeListener(new StartEditListener(edit_h, "00"));
        edit_m.setOnFocusChangeListener(new StartEditListener(edit_m, "00"));
        edit_s.setOnFocusChangeListener(new StartEditListener(edit_s, "00"));
        edit_kg.setOnFocusChangeListener(new StartEditListener(edit_kg, "0"));
        edit_reps.setOnFocusChangeListener(new StartEditListener(edit_reps, "0"));
        
        
        zacScreenPrev.setClickable(false);
        zacScreenPrev.setVisibility(View.INVISIBLE);
        
        ProgressBarDrawable progresDrawable = new ProgressBarDrawable(bars);
        ProgressBarDrawable progresDrawable2 = new ProgressBarDrawable(bars);
        ProgressBarDrawable progresDrawable3 = new ProgressBarDrawable(bars);
        
        progressBar.setProgressDrawable(progresDrawable);
        progressBar2.setProgressDrawable(progresDrawable2);
        zacProgressBar.setProgressDrawable(progresDrawable3);
        init();
      
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); 
        actionBar.setLogo(null);
        routines = new ArrayList<String>();
        routineIds = new ArrayList<Long>();
        dbHelper.open();

        default_programId = tryNumInt(prefs.getString("defaultProgram", ""));

        Log.i("Nlubej","default program " + default_programId);
        Cursor c = dbHelper.getRoutines(default_programId);
        Log.i("nlubej","stevilo routines za program : " + default_programId + "  "  + c.getCount());
        if(c.getCount() != 0) {
        	
			while(c.moveToNext()){
				 Log.i("nlubej","stevilo exercises : " + dbHelper.numExer(c.getLong(0)) + "");
				if(dbHelper.numExer(c.getLong(0)) > 0) {
					Log.d("nlubej", dbHelper.numExer(c.getLong(0))+"");
					routines.add(c.getString(1));
					routineIds.add(c.getLong(0));
				}
				else {
					warning.setText("* Routines without exercises are not shown");
				}
			}
        }
        else {
        	warning.setText("* No exercises found. Select a default program.");
			zacScreenNext.setClickable(false);
			zacScreenNext.setVisibility(View.VISIBLE);
        	
        }
        dbHelper.close();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(Start.this, android.R.layout.simple_spinner_dropdown_item, routines );
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        
        
        initStartValues();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK) {
        	 if(screenType != -2)
        	 {
        	 new AlertDialog.Builder(Start.this)
			    .setTitle("Finish Workout")
			    .setMessage("Are you sure you want to finish the workout?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	onBackPressed();
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        }
			     })
			     .show();
        	 }
        	 else
        		 onBackPressed();
         return true;
         }
         return super.onKeyDown(keyCode, event);    
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
        	if(screenType != -2) {
	        	new AlertDialog.Builder(Start.this)
			    .setTitle("Finish Workout")
			    .setMessage("Are you sure you want to finish the workout?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	onBackPressed();
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        }
			     })
			     .show();
        	}
        	else
        	{
        		onBackPressed();
        	}
        }
        else if(id == R.id.overflow) 
        {
            View menuItemView = findViewById(R.id.overflow);
            PopupMenu popupMenu = new PopupMenu(this, menuItemView);
            popupMenu.inflate(R.menu.popup_start);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					if(item.getItemId() == R.id.popup_show)
					{
						ShowPrevWorkoutDialog dialog = new ShowPrevWorkoutDialog();
			        	Bundle arg = new Bundle();
			    		arg.putLong("exerciseId", ids[exer_count]);
			    		
		    			dbHelper.open();
		    			lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]);
			    		Log.d("nlubej", "last exercise num prera�un! = " + lastWorkoutNum);
			    		dbHelper.close();
			    		
			    		if(numSetsAdded  < 2 && setsNotDeleted){
			    			Log.d("nlubej", setsNotDeleted + "     last workout: " + lastWorkoutNum);
			    			arg.putInt("lastWorkoutNum", lastWorkoutNum);//�e ni dodal, vrnemo -1
			    		}
			    		else{
			    			Log.d("nlubej", setsNotDeleted + "     last workout2: " + lastWorkoutNum);
			    			arg.putInt("lastWorkoutNum", lastWorkoutNum-1);//last workout se je �e zamenjal z trenutnim, vrnemo trenutni -1
			    		}
			    		
			    		
			    		dialog.setArguments(arg);
			        	dialog.show(getFragmentManager(), "show previous log");
					}
					else if(item.getItemId() == R.id.popup_copy)
					{
						if(exerciseType[exer_count].compareTo("Strength") == 0)
							deleteAndAddPrevious(0);
						else 
							deleteAndAddPrevious(1);
					}
					return false;
				}
			});
		}
        else if(id == R.id.comment)
        {
        	AddExerciseCommentDialog dialog = new AddExerciseCommentDialog();
    		Bundle arg = new Bundle();
    		arg.putLong("exerciseId", ids[exer_count]);
    		
    		dbHelper.open();
			lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]);
    		Log.d("nlubej", "last exercise num prera�un! = " + lastWorkoutNum);
    		dbHelper.close();
    		
    		if(numSetsAdded  < 2 && setsNotDeleted){
    			Log.d("nlubej", setsNotDeleted + "     last workout: " + lastWorkoutNum+1);
    			arg.putInt("workoutNum", lastWorkoutNum+1);//�e ni dodal, vrnemo -1
    		}
    		else{
    			Log.d("nlubej", setsNotDeleted + "     last workout2: " + lastWorkoutNum+1);
    			arg.putInt("workoutNum", lastWorkoutNum);//last workout se je �e zamenjal z trenutnim, vrnemo trenutni -1
    		}
    		arg.putString("mode","Edit");
    		dialog.setArguments(arg);
    		dialog.show(getFragmentManager(), "add comment"); 
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        getMenuInflater().inflate(R.menu.menu_start, menu);
        //menu.getItem(0).setTitle(" Copy ");
       // menu.getItem(1).setTitle(" Show ");
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
 
        Log.d("nlubej", "Screen type: " + screenType);
    	if(screenType == 1 || screenType == 0)
    	{
    		menu.getItem(0).setVisible(true);
    		menu.getItem(1).setVisible(true);
    	//	menu.getItem(1).setVisible(true);
    	}
    	else
    	{
    		menu.getItem(0).setVisible(false);
    		menu.getItem(1).setVisible(false);
    	//	menu.getItem(1).setVisible(false);
    	}
        return super.onPrepareOptionsMenu(menu);
    }
    
	
    public void init() {
    	dbHelper = new DBAdapter(this);
		
    	exercises = null;
		ids = null;
		dbHelper.open(); 
		Cursor c = dbHelper.getExercises(routineID);
		android.util.Log.d("nlubej", "count : " + c.getCount() + "  routineID: " + routineID );
		try {
			if(c.getCount() != 0) {
				exercises = new String[c.getCount()]; // za exercises, id, int tip exercise
				prevId = new int[c.getCount()];
				ids = new long[c.getCount()];
				Log.d("nlubej", "NOTR JIH JE PAAAAAAAAAA " + c.getCount());
				exerciseType = new String[c.getCount()];
				setNum = new int[c.getCount()]; // za �tevilo sets ki jih bo exercise mela
	
				int i=0;
				while(c.moveToNext()){
					ids[i] = c.getLong(0);
					exercises[i] = c.getString(1);		
					exerciseType[i] = c.getString(2);
					i++;
					workoutSets.add(new ArrayList<WorkoutSet>());
					layouts.add(new ArrayList<RelativeLayout>());
				}
			
			
				for(int j = 0; j<setNum.length; j++){
					setNum[j] = 1;
					if(exerciseType[j].compareTo("Cardio")==0) {
						prevId[j] = R.id.space2;
					}
					else {
						prevId[j] = R.id.space;
					}
				}
				numExercises = exercises.length;
			}

			
		}finally {
		c.close();
		}
		
		
		
		
		progressValue = 0;
		progressBar.setMax(10000);
		progressBar2.setMax(10000);
		zacProgressBar.setMax(10000);

		progressBar.setProgress((int)progressValue);
		progressBar2.setProgress((int)progressValue);
		zacProgressBar.setProgress((int)progressValue);

    }
    
    
    
    public void redraw() {
    	for(int i=0; i<layouts.get(exer_count).size(); i++){
    		layouts.get(exer_count).get(i).setBackgroundResource(android.R.color.transparent);
    	}
    }

    
    public void appendSetToFirstLayout(int id, int previousId, String sett, String kgg, String repp) {
    	
    	
    	Log.d("nlubej", "id: " + id + " prevID: " + previousId);
    	mInputView = new RelativeLayout(this);
	 	mInputView = (RelativeLayout) getLayoutInflater().inflate(R.layout.start_sets_list_row, null);
	 	  //initialise
	 	final TextView set = (TextView) mInputView.findViewById(R.id.start_set);
	 	final TextView kg = (TextView) mInputView.findViewById(R.id.start_kg);
		final TextView rep = (TextView) mInputView.findViewById(R.id.start_rep);
		  
		
		
		Log.d("nlubej", "unit system" + prefs.getString("unitSystem", ""));
		
		
		set.setText(sett + " set");
		if(prefs.getString("unitSystem", "").compareTo("metric")==0)
			kg.setText(kgg + " kg");
		else
			kg.setText(kgg + " lbs");
		
		rep.setText(repp + " reps");
		  
	 	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	 	params.addRule(RelativeLayout.BELOW, previousId);
	 	   
	 	mInputView.setTag(numSetsAdded);
	 	mInputView.setId(id);
	 	mInputView.setLayoutParams(params);
	 	rlayout.addView(mInputView);
	 	
	 	layouts.get(exer_count).add(mInputView);
	 	mInputView.setOnClickListener(new OnClickListener() {
			
	 		  @Override
	 		  public void onClick(View v) {
	 			  redraw();
	 			  String[] num_reps = rep.getText().toString().split(" ");
		 		  String[] num_kg = kg.getText().toString().split(" ");
		 		  clickedPosition = (Integer) v.getTag();
		 		  edit_reps.setText(num_reps[0]);
		 		  edit_kg.setText(trimNumber(num_kg[0]));
		 		  v.setBackgroundResource(R.color.gray);
		 		  save.setText("Update");
		 		  clear.setText("Delete");
		 		
	 		  }
	 	  });
	 	
	 	numSetsAdded++;
    }

    
   
    
    public void appendSetToSecondLayout(int id, int previousId, String sett, String h, String m, String s) {
    	
    	Log.d("nlubej", "id: " + id + " prevID: " + previousId);
    	mInputView = new RelativeLayout(this);
	 	mInputView = (RelativeLayout) getLayoutInflater().inflate(R.layout.start_sets_list_row2, null);
	 	  //initialise
	 	final TextView set = (TextView) mInputView.findViewById(R.id.start_set2);
	 	final TextView duration = (TextView) mInputView.findViewById(R.id.start_duration);
		  
		set.setText(sett);
		duration.setText(h + ":" + m + ":"+ s);
		 
	 	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	 	params.addRule(RelativeLayout.BELOW, previousId);
	 	
	 	mInputView.setTag(numSetsAdded);
	 	mInputView.setId(id);
	 	mInputView.setLayoutParams(params);
	 	rlayout2.addView(mInputView);
	 	
	 	layouts.get(exer_count).add(mInputView);
	 	mInputView.setOnClickListener(new OnClickListener() {
			
	 		  @Override
	 		 public void onClick(View v) {
	 			 redraw();
	 			 String[] time = duration.getText().toString().split(":");
		 		 edit_h.setText(time[0].toString());
		 		 edit_m.setText(time[1].toString());
		 		 edit_s.setText(time[2].toString());
		 		 clickedPosition = (Integer) v.getTag();
		 		 v.setBackgroundResource(R.color.gray);
		 		 save2.setText("Update");
		 		 clear2.setText("Delete");
	 		  }
	 	  });	
	 	numSetsAdded++;
    }

    
    public void reloadViews() {
    	for(int i=0; i<layouts.size(); i++) {
    		for(int j=0; j<layouts.get(i).size(); j++) {
	    		if(i==exer_count) 
	    			layouts.get(i).get(j).setVisibility(View.VISIBLE);
	    		else 
	    			layouts.get(i).get(j).setVisibility(View.GONE);
    		}
    	}
    }
	
    @Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
    	routineID = routineIds.get(position);
    	init();
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {	
	}
    
    @Override
    public void onClick(View v) {
    	if(v==next) {
    		Log.d("nlubej", "next");
    		 redraw();
    		doMagic("+");
    		
    	}
    	
    	else if(v==prev){
    		redraw();
    		doMagic("-");
    	}
    	
    	else if(v==next2) {	
    		Log.d("nlubej", "next2");
    		redraw();
    		doMagic("+");
    		
    	}
    	
    	else if(v==prev2) {
    		Log.d("nlubej", "prev2");
    		redraw();
    		doMagic("-");
    	}
    	
    	else if(v==save) {
    		if(save.getText().toString().compareTo("Add")==0) {
    			
    			String set = numSetsAdded+"";
    			String kg = trimNumber(edit_kg.getText().toString());
    			if(kg.charAt(0) == '.')
    				kg = "0"+kg;
    			String rep = edit_reps.getText().toString();
    			workoutSet = new  FirstWorkoutSet(set,kg,rep);
    			
    			workoutSets.get(exer_count).add(workoutSet);
    			appendSetToFirstLayout(id, prevId[exer_count], set,kg,rep);
    			Calendar c = Calendar.getInstance();
    			c.getTime();
    			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    			String date = df.format(c.getTime());
    			if(numSetsAdded == 2 && setsNotDeleted) { //get last workout number at teh begining when u add teh first set
    				dbHelper.open();
    				lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]) +1;
	    			Log.d("nlubej", "last exercise num = " + lastWorkoutNum);
	    			dbHelper.close();
    			}
    			
    			int sett = tryNumInt(set);
    			int repp = tryNumInt(rep);
    			double weight = tryNumDouble(trimNumber(kg));
    			dbHelper.open();
    			Log.i("nlubej",sett + " " + weight + " " + repp + " " + lastWorkoutNum + " exerciseId = " + ids[exer_count]);
    			dbHelper.insertLog(sett, weight, repp, date, lastWorkoutNum, "Strength", routineID, ids[exer_count]);
    			dbHelper.close();
    			prevId[exer_count] = id;
    		 	id++;
    			redraw();
    			
    		}
    		else 
    		{
    			String kg = trimNumber(edit_kg.getText().toString());
    			if(kg.charAt(0) == '.')
    				kg = "0"+kg;
    			
    			dbHelper.open();
    			dbHelper.updateLog(clickedPosition, lastWorkoutNum, ids[exer_count], tryNumDouble(trimNumber(edit_kg.getText().toString())), tryNumInt(edit_reps.getText().toString()));
    			dbHelper.close();
    			((FirstWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setKg(kg);
    			((FirstWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setRep(tryNumInt(edit_reps.getText().toString())+"");
    			save.setText("Add");
    			clear.setText("Clear");
    		 	redraw(0);
    			redraw();
    		}
    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edit_kg.getWindowToken(), 0);
    	}
    	else if(v == save2) {
    		if(save2.getText().toString().compareTo("Add")==0) {
    			Log.d("nlubej",exer_count+"");
    			String set = numSetsAdded+"";

    			
    			String hour = trimNum(edit_h.getText().toString());
    			String minute = trimNum(edit_m.getText().toString());
    			String second = trimNum(edit_s.getText().toString());
    			String duration = hour+":"+minute+":"+second;
    			double distance = tryNumDouble(edit_distance.getText().toString());
    			
    			workoutSet = new  SecondWorkoutSet(hour, minute, second, duration);
    			
    			workoutSets.get(exer_count).add(workoutSet);
    			appendSetToSecondLayout(id, prevId[exer_count], set, hour, minute, second);
    			
    			Calendar c = Calendar.getInstance();
    			c.getTime();
    			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    			String date = df.format(c.getTime());
    			
    			if(numSetsAdded == 2 && setsNotDeleted) { //get last workout number at teh begining when u add teh first set
    				dbHelper.open();
    				lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]) +1;
	    			Log.d("nlubej", "last exercise num = " + lastWorkoutNum );
	    			dbHelper.close();
    			}
    			int sett = tryNumInt(set);
    			
    			dbHelper.open();
    			dbHelper.insertLog(sett, duration, distance, date, lastWorkoutNum, "Cardio", routineID, ids[exer_count]);
    			Log.i("nlubej",sett + "    " +duration);
    			dbHelper.close();
    			
    			prevId[exer_count] = id;
    		 	id++;
    			redraw();
    			
    		}
    		else 
    		{
    			String hour = trimNum(edit_h.getText().toString());
    			String minute = trimNum(edit_m.getText().toString());
    			String second = trimNum(edit_s.getText().toString());
    			String duration = hour+":"+minute+":"+second;
    			double distance = tryNumDouble(edit_distance.getText().toString());
    			
    			dbHelper.open();
    			dbHelper.updateLog(clickedPosition, lastWorkoutNum, ids[exer_count], duration, distance, 0);
    			dbHelper.close();
    			((SecondWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setHours(trimNum(edit_h.getText().toString()));
    			((SecondWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setMinute(trimNum(edit_m.getText().toString()));
    			((SecondWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setSecond(trimNum(edit_s.getText().toString()));
    			((SecondWorkoutSet) workoutSets.get(exer_count).get(clickedPosition-1)).setDistance(tryNumDouble(edit_distance.getText().toString())+"");
    			
    			save2.setText("Add");
    			clear2.setText("Clear");
    		 	redraw(1);
    			redraw();
    		}
    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edit_kg.getWindowToken(), 0);
    	}
    	else if(v == clear) {
    		if(clear.getText().toString().compareTo("Clear")==0) {
    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    			imm.hideSoftInputFromWindow(edit_kg.getWindowToken(), 0);
    			edit_kg.setText("0");
    			edit_reps.setText("0");
    		}
    		else {
    			dbHelper.open();
    			int set = 1;
    			for(int i=1; i<workoutSets.get(exer_count).size()+1; i++) {
    				if(i != clickedPosition){
    					
    					Log.i("nlubej","update:   " + set +" last: " + lastWorkoutNum + "prev set:" + i + " exercise= " + ids[exer_count]);
    					dbHelper.updateLogAfterDelete(set,lastWorkoutNum , i, routineID, ids[exer_count]);
    					set++;
    				}
    				else {
    					Log.i("nlubej","update:   set: set->" + -1 +" last: " + lastWorkoutNum + "prev set:" + i + " exercise= " + ids[exer_count]);
    					dbHelper.updateLogAfterDelete(-1,lastWorkoutNum , i, routineID, ids[exer_count]);
    				}
    				
    			}
    			Cursor d = dbHelper.getLog(ids[exer_count]);
    			if(d.getCount() != 0)
    			{
    				while(d.moveToNext())
    				{//Cursor cursor =  db.query(DATABASE_TABLE_LOG, new String[] {KEY_TYPE, KEY_SET, KEY_WEIGHT,KEY_REP, KEY_DURATION,KEY_DISTANCE, KEY_WORKOUT_NUM}, sql, null, null, null, null);	
    					Log.i("nlubej","notri: Set->" + d.getInt(1) + " Weight->" + d.getDouble(2) + " Rep->" + d.getInt(3) + " WorkoutNum->" + d.getString(6));
    				}
    			}
    			else 
    			{
    				Log.i("nlubej","nc ni notr");
    			}
    			
				Log.i("nlubej","delete: where set->  " + -1 + " last: " + lastWorkoutNum + " exercise= " + ids[exer_count]);
    			dbHelper.deleteLog(lastWorkoutNum, -1, ids[exer_count], routineID);
    			dbHelper.close();
    			redraw();
    			deleteAndRedraw(0);
    			Log.d("Nlubej","possition " + clickedPosition);
    			save.setText("Add");
    			clear.setText("Clear");
    		}
    	}
    	else if( v == clear2) {
    		if(clear2.getText().toString().compareTo("Clear")==0) {
    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    			imm.hideSoftInputFromWindow(edit_kg.getWindowToken(), 0);
    			edit_h.setText("00");
    			edit_m.setText("00");
    			edit_s.setText("00");
    			edit_m.setText("0");
    			edit_distance.setText("0");
    		}
    		else {
    			redraw();
    			deleteAndRedraw(1);
    			save2.setText("Add");
    			clear2.setText("Clear");
    		}
    	}
    	else if(v == zacScreenNext){
    		Log.d("nlubej", exer_count+"");
    		exerciseName.setText(exercises[exer_count]);
    		exerciseName2.setText(exercises[exer_count]);
    		doSpecialMagic("+");
    	}
    	else if( v == zacScreenPrev) {
    		Log.d("nlubej", "zacScreenPrev");
    		redraw();
    		doSpecialMagic("-");
    	}
    }

	private int tryNumInt(String num) {
		try {
			return Integer.parseInt(num);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private double tryNumDouble(String num) {
		try {
			return Double.parseDouble(num);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}


	public void doMagic(String operator) {

		
    	if(operator.compareTo("-") == 0) {
    		if(exer_count > 0) {
    			progressValue = progressValue-(10d/numExercises)*1000d;
				progressBar.setProgress((int)progressValue);
				progressBar2.setProgress((int)progressValue);
				zacProgressBar.setProgress((int)progressValue);
				setNum[exer_count] = numSetsAdded;
				exer_count--;
				exerciseName.setText(exercises[exer_count]);
				exerciseName2.setText(exercises[exer_count]);
				numSetsAdded = setNum[exer_count];
				
				if(exerciseType[exer_count].compareTo("Cardio") == 0)
				{
					if(setNum[exer_count] >=2)
	    			{
	    				edit_h.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getHours());
	    				edit_m.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getMinute());
	    				edit_s.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getSecond());
	    				edit_distance.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getDistance());
	    			}
					switchScreen(1);
				}
					
				else 
				{
					if(setNum[exer_count] >=2)
	    			{
	    				Log.i("nlubej", "first set weight : " + ((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getKg());
	    				Log.i("nlubej", "first set rep : " + ((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getRep());
	    				edit_reps.setText(((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getRep());
	    				edit_kg.setText(((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getKg());
	    			}
					switchScreen(0);
				}
					
				
				reloadViews();
    		}
    		else {
    			Log.d("nlubej","prsl");
    			switchScreen(-1);
    			progressValue = 0;
				progressBar.setProgress((int)progressValue);
				progressBar2.setProgress((int)progressValue);
				zacProgressBar.setProgress((int)progressValue);
    			
    		}
    	}
    	else {
    		if(exer_count < numExercises-1) {
	    		progressValue = progressValue+(10d/numExercises)*1000d;
				android.util.Log.d("nlubej", "Value : " + progressValue+ "");
	    		progressBar.setProgress((int)progressValue);
	    		progressBar2.setProgress((int)progressValue);
	    		setNum[exer_count] = numSetsAdded;
	    		exer_count++;
	    		exerciseName.setText(exercises[exer_count]);
	    		exerciseName2.setText(exercises[exer_count]);
	    		numSetsAdded = setNum[exer_count];
	    		
	    		
	    		if(exerciseType[exer_count].compareTo("Cardio") == 0)
	    		{
	    			if(setNum[exer_count] >2)
	    			{

	    					edit_h.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getHours());
		    				edit_m.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getMinute());
		    				edit_s.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getSecond());
		    				edit_distance.setText(((SecondWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getDistance());
	    			}
    				switchScreen(1);
	    		}
	    		else 
	    		{
	    			Log.i("nlubej", "exercise count: " + setNum[exer_count]);
	    			if(setNum[exer_count] >=2)
	    			{
	    				Log.i("nlubej", "first set weight : " + ((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getKg());
	    				Log.i("nlubej", "first set rep : " + ((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getRep());
	    				edit_reps.setText(((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getRep());
	    				edit_kg.setText(((FirstWorkoutSet) workoutSets.get(exer_count).get(setNum[exer_count]-2)).getKg());
	    			}
	    			switchScreen(0);
	    		}

	    		reloadViews();
    		}
    		else {
    			new AlertDialog.Builder(Start.this)
			    .setTitle("Finish Workout")
			    .setMessage("Are you sure you want to finish the workout?")
			    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	prevscreenType = screenType;
		    			switchScreen(-2);
			        	
			        }
			     })
			    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        }
			     })
			    
			     .show();
    			
    		}
    	}
    }
	
	private void doSpecialMagic(String operator) {
		if(operator.compareTo("-") == 0) { 
			
		}
		else {
			progressValue = progressValue+(10d/numExercises)*1000d;
			android.util.Log.d("nlubej", "Value : " + progressValue+ "");
    		progressBar.setProgress((int)progressValue);
    		progressBar2.setProgress((int)progressValue);
    		zacProgressBar.setProgress((int)progressValue);
			Log.d("Nlubej", "i do magic");
			if(exerciseType[exer_count].compareTo("Cardio") == 0){
				switchScreen(1);
			}
			else {
				switchScreen(0);
				Log.d("Nlubej", "i do magic. and it works");
			}
		}
		reloadViews();
	}
	// screen type:
	//1 = cardio
	//0 = strength
	//-1 strat 
	// -2 end
	public void switchScreen(int type) 
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edit_kg.getWindowToken(), 0);
		
		if(screenType == -1 && type == 0)
		{
			switcher.showNext();
    		screenType = 0;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == -1 && type == 1)
		{
			switcher.showNext();
			switcher.showNext();
    		screenType = 1;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == 0 && type == -1)
		{
			switcher.showPrevious();
    		screenType = -1;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == 1 && type == -1)
		{
			switcher.showPrevious();
			switcher.showPrevious();
    		screenType = -1;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == 0 && type == 1)
		{
			switcher.showNext();
    		screenType = 1;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == 1 && type == 0)
		{
			switcher.showPrevious();
    		screenType = 0;
    		save.requestFocus();
    		save2.requestFocus();
		}
		else if(screenType == 1 && type == -2)
		{
			switcher.showNext();
			screenType = -2;
		}
		else if(screenType == 0 && type == -2)
		{
			switcher.showNext();
			switcher.showNext();
			screenType = -2;
		}
		else if(screenType == -1 && type == -2)
		{
			switcher.showNext();
			switcher.showNext();
			switcher.showNext();
			screenType = -2;
		}
		
		save2.setText("Add");
		clear2.setText("Clear");
		save.setText("Add");
		clear.setText("Clear");
		lastWorkoutNum = -1;
		setsNotDeleted = true;
		this.invalidateOptionsMenu();
    }

	
	public String trimNumber(String number) {
		double num = 0;
		try {
			num = Double.parseDouble(number);
		}
		catch(NumberFormatException e) {
			return "0";
		}
		
		if(num==(int)num) {
			return (int)num+"";
		}
		else
			return number;
	}
	
	public String trimNum(String num) {
		if(num.length() == 0)
		{
			return "00";
		}
		else if(num.length() == 1) {
			return "0"+num;
		}
		else
			return num;
	}
	
	public void initStartValues() {
		edit_h.setText("00");
		edit_m.setText("00");
		edit_s.setText("00");
		edit_kg.setText("0");
		edit_reps.setText("0");
		edit_distance.setText("0");
	}
	
	public void redraw(int mode) 
	{
		
		int[] Ids = new int[layouts.get(exer_count).size()];
		
		for(int i=0; i<layouts.get(exer_count).size(); i++){
				Ids[i] = layouts.get(exer_count).get(i).getId();
		}
		
		for(int i=0; i<layouts.get(exer_count).size(); i++){
			RelativeLayout l = (RelativeLayout)findViewById(layouts.get(exer_count).get(i).getId());
			if(mode == 0) 
				rlayout.removeView(l);
			else 
				rlayout2.removeView(l);
		}
		
		

		layouts.get(exer_count).clear();
		
		if(mode == 0) 
			prevId[exer_count] = R.id.space;
		else 
			prevId[exer_count] = R.id.space2;
		
		
		String set = "";
		String kg= "";
		String rep = "";
		String h,m,s = "";
		
		numSetsAdded = 1;
		
		
		
		for(int i=0; i<Ids.length; i++) {
			if(Ids[i] != -1) {
				if(mode == 0){
					set = numSetsAdded+"";
					kg = ((FirstWorkoutSet) workoutSets.get(exer_count).get(i)).getKg();
					rep = ((FirstWorkoutSet) workoutSets.get(exer_count).get(i)).getRep();
					
					appendSetToFirstLayout(Ids[i], prevId[exer_count], set,kg,rep);
					
					prevId[exer_count] = Ids[i];
				}
				else {
					set = numSetsAdded+"";
					h = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getHours();
					m = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getMinute();
					s = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getSecond();
					
					appendSetToSecondLayout(Ids[i], prevId[exer_count], set, h, m, s);
					prevId[exer_count] = Ids[i];

					
				}
			}
		}
	}
	
	
	
	
	public void deleteAndRedraw(int mode) {
		
		int[] newIds = new int[layouts.get(exer_count).size()];
		
		for(int i=0; i<layouts.get(exer_count).size(); i++){
			if(i!=clickedPosition-1) { 
				newIds[i] = layouts.get(exer_count).get(i).getId();
			}
			else {
				newIds[i] = -1;
			}
		}
		
		for(int i=0; i<layouts.get(exer_count).size(); i++){
			RelativeLayout l = (RelativeLayout)findViewById(layouts.get(exer_count).get(i).getId());
			if(mode == 0) 
				rlayout.removeView(l);
			else 
				rlayout2.removeView(l);
		}
		
		

		layouts.get(exer_count).clear();
		
		if(mode == 0) 
			prevId[exer_count] = R.id.space;
		else 
			prevId[exer_count] = R.id.space2;
		
		
		String set = "";
		String kg= "";
		String rep = "";
		String h,m,s = "";
		
		numSetsAdded = 1;
		
		
		
		for(int i=0; i<newIds.length; i++) {
			if(newIds[i] != -1) {
				if(mode == 0){
					set = numSetsAdded+"";
					kg = ((FirstWorkoutSet) workoutSets.get(exer_count).get(i)).getKg();
					rep = ((FirstWorkoutSet) workoutSets.get(exer_count).get(i)).getRep();
					
					appendSetToFirstLayout(newIds[i], prevId[exer_count], set,kg,rep);
					
					prevId[exer_count] = newIds[i];
				}
				else {
					set = numSetsAdded+"";
					h = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getHours();
					m = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getMinute();
					s = ((SecondWorkoutSet) workoutSets.get(exer_count).get(i)).getSecond();
					
					appendSetToSecondLayout(newIds[i], prevId[exer_count], set, h, m, s);
					Log.d("nlubej", "DODAN ID: " + newIds[i] + "   PREV ID: " + prevId[exer_count]);
					prevId[exer_count] = newIds[i];

					
				}
			}
		}
		workoutSets.get(exer_count).remove(clickedPosition-1); //zbri�emo iz workout sets
		if(workoutSets.get(exer_count).size() == 0){
			setsNotDeleted = false;
		}
	}
	
	public void deleteAndAddPrevious(int mode) {
		
		int lastWorkout = 0;
		if(numSetsAdded  < 2 && setsNotDeleted){
			lastWorkout = lastWorkoutNum;
		}
		else{
			lastWorkout = lastWorkoutNum-1;
		}
		
		if(lastWorkoutNum == -1) 
		{
			dbHelper.open();
			lastWorkout = dbHelper.getLastWorkoutNum(ids[exer_count]);
			dbHelper.close();
		}
		
		
		for(int i=0; i<layouts.get(exer_count).size(); i++){
			RelativeLayout l = (RelativeLayout)findViewById(layouts.get(exer_count).get(i).getId());
			if(mode == 0) 
				rlayout.removeView(l);
			else 
				rlayout2.removeView(l);
		}
		layouts.get(exer_count).clear();
		
		if(mode == 0) 
			prevId[exer_count] = R.id.space;
		else 
			prevId[exer_count] = R.id.space2;
		numSetsAdded = 1;
		
		
		if(mode == 0) 
		{
			dbHelper.open();
			Cursor c = dbHelper.getPrevLog(ids[exer_count], lastWorkout);
			if(c.getCount() != 0) 
			{
				Calendar cal = Calendar.getInstance();
				cal.getTime();
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				String date = df.format(cal.getTime());
				String set;
				String kg;
				String rep;
				
				while(c.moveToNext()) 
				{
					set = c.getInt(1)+"";
	    			kg = c.getDouble(2)+"";
	    			rep = c.getInt(3)+"";
	    			
	    			workoutSet = new  FirstWorkoutSet(set,kg,rep);
	    			workoutSets.get(exer_count).add(workoutSet);
	    			appendSetToFirstLayout(id, prevId[exer_count], set,kg,rep);
	    			
	    			if(numSetsAdded == 2 && setsNotDeleted) { //get last workout number at teh begining when u add teh first set
	    				lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]) +1;
	    			}
	    			
	    			redraw();
	    			int sett = tryNumInt(set);
	    			int repp = tryNumInt(rep);
	    			double weight = tryNumDouble(kg);
	    			Log.i("nlubej","INSERT:  set: " + sett + " weight: " + weight + " rep: " + repp+ " lastW: " + lastWorkoutNum);
	    			dbHelper.insertLog(sett, weight, repp, date, lastWorkoutNum, "Strength", routineID, ids[exer_count]);
	    			prevId[exer_count] = id;
	    		 	id++;
				}
			}
			c.close();
			dbHelper.close();
		}
		else //mode 1
		{
			dbHelper.open();
			Cursor c = dbHelper.getPrevLog(ids[exer_count], lastWorkout);
			if(c.getCount() != 0) 
			{
				Calendar cal = Calendar.getInstance();
				cal.getTime();
				SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
				String date = df.format(cal.getTime());
				String set;
				String duration;
				String h;
				String m;
				String s;
				double distance;
				//KEY_TYPE, KEY_SET, KEY_WEIGHT,KEY_REP, KEY_DURATION,KEY_DISTANCE, KEY_WORKOUT_NUM
				while(c.moveToNext()) 
				{
					duration = c.getString(4)+"";
	    			distance = c.getDouble(5);
	    			
					String[] dur = duration.split(":");
					set = c.getInt(1)+"";
					h = dur[0];
	    			m = dur[1];
	    			s = dur[2];
	    			
	    			workoutSet = new  SecondWorkoutSet(h,m,s,distance+"");
	    			workoutSets.get(exer_count).add(workoutSet);
	    			appendSetToSecondLayout(id, prevId[exer_count],set,h,m,s);
	    			
	    			if(numSetsAdded == 2 && setsNotDeleted) { //get last workout number at teh begining when u add teh first set
	    				lastWorkoutNum = dbHelper.getLastWorkoutNum(ids[exer_count]) +1;
	    			}
	    			
	    			int sett = tryNumInt(set);
	    			
	    			dbHelper.open();
	    			dbHelper.insertLog(sett, duration, distance, date, lastWorkoutNum, "Cardio", routineID, ids[exer_count]);
	    			dbHelper.close();
	    			
	    			prevId[exer_count] = id;
	    		 	id++;
	    			redraw();
				}
			}
			c.close();
			dbHelper.close();
		}
	}
}