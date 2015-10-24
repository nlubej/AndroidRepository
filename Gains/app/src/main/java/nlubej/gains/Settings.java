package nlubej.gains;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;



import nlubej.gains.xmlbackup.XmlExercise;
import nlubej.gains.xmlbackup.XmlLog;
import nlubej.gains.xmlbackup.XmlProgram;
import nlubej.gains.xmlbackup.XmlRoutine;
 
public class Settings extends PreferenceActivity {
 /*
	ListPreference m_updateList;
	DBAdapter dbHelper;
	String line = "\r\n";
	String state = Environment.getExternalStorageState();
	 // action bar
    private Menu menu;
 
    // Refresh menu item
    private MenuItem refreshMenuItem;
	private String[] names;
	private String[] ids;
	private UiLifecycleHelper uiHelper;
	SharedPreferences prefs;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new DBAdapter(getApplicationContext());
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.setDefaultValues(Settings.this, R.xml.settings,
            true);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(null);
        uiHelper = new UiLifecycleHelper(Settings.this, null);
		uiHelper.onCreate(savedInstanceState);
        initSummary(getPreferenceScreen());   
        
        //feedback
        Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("feedback");
        dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
                public boolean onPreferenceClick(Preference preference) 
                {
                	Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "gains.workoutlog@gmail.com"));
    				intent.putExtra(Intent.EXTRA_SUBJECT, "");
    				intent.putExtra(Intent.EXTRA_TEXT, "");
    				startActivity(intent);
					return false;
                
                }
            });
        
        
        dialogPreference = (Preference) getPreferenceScreen().findPreference("share");
        dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
                public boolean onPreferenceClick(Preference preference) 
                {
                	Toast.makeText(getApplicationContext(), "Wait a moment..", Toast.LENGTH_LONG).show();
                	if (FacebookDialog.canPresentShareDialog(Settings.this,
        					FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
                				Log.i("nlubej","share dialog");
        					    // Publish the post using the Share Dialog
        					    FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(Settings.this)
        					            .setLink("https://www.facebook.com/pages/Gains-Workout-Log/1453435284933125?ref_type=bookmark")
        					            .setPicture("https://fbcdn-sphotos-d-a.akamaihd.net/hphotos-ak-xpa1/t1.0-9/12499_1453924478217539_67424883590781770_n.png")
        					            .setName("Gains - Workout Log")
        					            .setCaption("")
        					            .setDescription("Log your Workouts! You gonna get gains.. all kinds of gains!")
        					            .build();
        					    uiHelper.trackPendingDialogCall(shareDialog.present());

        					} else {
        					    // Fallback. For example, publish the post using the Feed Dialog
        					    publishFeedDialog();
        					    Log.i("nlubej","share dialog 2");
        					}
					return false;
                
                }
            });
        
        //create backup
        dialogPreference = (Preference) getPreferenceScreen().findPreference("create_backup");
        dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
                public boolean onPreferenceClick(Preference preference) {
                	refreshMenuItem = menu.findItem(R.id.refresh_progress);
                	refreshMenuItem.setVisible(true);
                	new SyncData().execute("create");
					return true;
                }
            });
        
        
        //restore backup
        dialogPreference = (Preference) getPreferenceScreen().findPreference("restore_backup");
        dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        	
                public boolean onPreferenceClick(Preference preference) {
                	new AlertDialog.Builder(Settings.this)
				    .setTitle("Restore Backup")
				    .setMessage("Are you sure you want to Restore Backup? If no backup, all data will be lost.")
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) 
				        { 
				        	if(Environment.MEDIA_MOUNTED.equals(state))
				    		{

				    			XmlPullParserFactory pullParserFactory;
				    			FileInputStream fis = null;
				    			try 
				    			{ 	Log.d("Nlubej","nlubej");
				    				pullParserFactory = XmlPullParserFactory.newInstance();
				    				XmlPullParser parser = pullParserFactory.newPullParser();
				    				File[] files = Environment.getExternalStoragePublicDirectory("Gains").listFiles();
				    				File file = null;
				    				if(files.length != 0)
				    				{
					    				refreshMenuItem = menu.findItem(R.id.refresh_progress);
					                	refreshMenuItem.setVisible(true);
					                	dbHelper.open();
					                	getApplicationContext().deleteDatabase("fitTrackerDB.db");
					                	dbHelper.Close();
					                	dbHelper = new DBAdapter(getApplicationContext());
					                	dbHelper.open();
					                	dbHelper.insertUser();
					               		dbHelper.insertAchievements();
					                	dbHelper.Close();
					                	new SyncData().execute("restore");
				    				}
				    				else
				    				{
				    					Toast.makeText(getApplicationContext(), "No backup found", Toast.LENGTH_SHORT).show();
				    				}
				    			} catch(Exception e) 
				    			{
				    				e.printStackTrace();
				    				Toast.makeText(getApplicationContext(), "No backup found", Toast.LENGTH_SHORT).show();
				    				
				    			}	
				    	}
				    			
				        }
				     })
				    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	//Toast.makeText(c, "no", Toast.LENGTH_SHORT).show();
				        }
				     }).show();

					return true;
                }
            });

        init();
    }
	
	
	public void init() 
	{
		ListPreference programList = (ListPreference)getPreferenceScreen().findPreference("DefaultProgram");
        dbHelper.open();
        names = null;
        ids = null;
		Cursor c = dbHelper.getPrograms();
		if(c.getCount() != 0) {
			names = new String[c.getCount()];
			ids = new String[c.getCount()];
			
			int i=0;
				while(c.moveToNext()){
				ids[i] = c.getLong(0)+"";
				Log.i("nlubej","id default: " + ids[i]);
				names[i] = c.getString(1);
				i++;
				
			}
				
			programList.setEntries(names);
			programList.setEntryValues(ids);
			programList.setSelectable(true);
		    prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    String default_programId = prefs.getString("DefaultProgram", "1");
	        Log.d("nlubej", "default: " + default_programId);
	        
		        if(default_programId == null || Integer.parseInt(default_programId) <=0) {
		        	programList.setSummary(names[0]);
		        	programList.setValue(ids[0]);
		        }
		        
		        else 
		        {
		        	int indeks = GetIndex(default_programId);
		        	Log.i("nlubej","indeks: " +  GetIndex(default_programId));
		        	programList.setSummary(names[indeks]);
		        	programList.setValue(ids[indeks]);
		      //  	Log.i("nlubej","summary name: " +names[GetIndex(Integer.parseInt(default_programId)+"")] + " indeks: " + default_programId + " -1");
		      //  	programList.setSummary(names[GetIndex(Integer.parseInt(default_programId)+"")]);
		       // 	programList.setValue(ids[0]);
		        }

		}
		else
			programList.setSelectable(false);
        dbHelper.Close();
        c.Close();
	}
	
	
	public int GetIndex(String id) {
		for(int i=0; i<ids.length; i++)
		{
			if(id.compareTo(ids[i])== 0) 
				return i;
		}
		return 0;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_settings, menu);
	    this.menu = menu;
	    return super.onCreateOptionsMenu(menu);
	}

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case android.R.id.home:
		    	onBackPressed();
		    	return true;
		    }
		    return super.onOptionsItemSelected(item);
		}
    
	 
    
	private void initSummary(Preference p) {
		if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
		
	}

	@Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        uiHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
	    uiHelper.onPause();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updatePrefSummary(findPreference(key));
		PreferenceManager.setDefaultValues(Settings.this, R.xml.settings, true);
		initSummary(getPreferenceScreen());   
		if(key.compareTo("unitSystem") == 0)
		{
			dbHelper.open();
			dbHelper.updateUser("weight", "0");
			dbHelper.updateUser("height", "0");
			dbHelper.Close();
		}
		 
        // Now, manually update it's value to default/empty
     // Now, if you click on the item, you'll see the value you've just set here
		//prefsEditr.putString(key, findPreference(key).getSummary().toString()).commit();
		
	}



	
	private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
            Log.d("nlubej", "preference updated");
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }
	
	
	
	//create backup
	public int createBackup() 
	{
		String data = ""; // Using default 16 character size
		data = getBackupData();
		if(Environment.MEDIA_MOUNTED.equals(state))
		{	
			File folder = Environment.getExternalStoragePublicDirectory("Gains");
			if(!folder.exists())
				folder.mkdir();
			
			
			File myFile = new File(folder,"backup.xml");
			boolean t = writeData(myFile, data);
			if(t)
				return 1;
			else
				return 0;
		}
		else {
			Log.d("nlubej","not mounted");
			return -1;
		}
	}
		
	public String getBackupData() {
		String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+line;
		data += "<!-- Backup was intentionally stored into xml, so user can change its content easier.\n"+
		"\t You can change your programs, routines, exercises here. Just make sure the xml layout stays the same.\n"+
				"\t If you aren't a douche, you wont change the achievement backup! ;)\n"+
				"\t Now go make some gains bitch!-->" + line;
		data += "<backup>" + line;
		data += "\t<programs>" + line;
		dbHelper.open();
		Cursor routines = null;
		Cursor log = null;
		Cursor exercises = null;
		Cursor programs = dbHelper.getAllProgramsBackup();
		
		try 
		{
			if(programs.getCount() != 0)  //then party begins
			{
	           
				while(programs.moveToNext())
				{
					data +=addXml("program", new String[] {"name"}, new String[] {programs.getString(1)}, 2,false);
					routines = dbHelper.getRoutinesBackup(programs.getLong(0));
					if(routines.getCount() != 0) 
					{ Log.d("nlubej",routines.getCount()+"");
						while(routines.moveToNext())
						{
							data += addXml("routine", new String[] {"name"}, new String[] {routines.getString(1)}, 3,false);
							exercises = dbHelper.getAllExercisesBackup(routines.getLong(0));
							if(exercises.getCount() != 0) 
							{
								while(exercises.moveToNext())
								{
									data += addXml("exercise", new String[] {"name","type"}, new String[] {exercises.getString(1), exercises.getString(2)}, 4,false);
									log = dbHelper.getAllLogBackup(routines.getLong(0), exercises.getLong(0));
									if(log.getCount() != 0) 
									{
										while(log.moveToNext())
										{
											data += addXml("log", new String[] {"set","weight","rep", "duration", "distance", "date", "workoutNum", "type"}, new String[] {log.getInt(0)+"", log.getDouble(1)+"", log.getInt(2)+"", log.getString(3), log.getDouble(4)+"", log.getString(5), log.getInt(6)+"", log.getString(7)}, 5,true);
										}
									}
									data += "\t\t\t\t</exercise>"+line;
								}
								exercises.Close();
							}
							data += "\t\t\t</routine>"+line;
						}
						routines.Close();
					}
					data += "\t\t</program>"+line;
				}
				programs.Close();
				data += "\t</programs>"+line;
			}
			else
			{
				data += "\t</programs>"+line;
			}
			
			Cursor user = dbHelper.getUserData();
			if(user.getCount() != 0) 
			{
				while(user.moveToNext()) 
				{
					Log.i("nlubej","weight: " + user.getDouble(0)+"  height: " +  user.getString(1));
					data += addXml("user", new String[] {"weight", "height", "showStartTutorial", "showMainTutorial"}, new String[] {user.getDouble(0)+"", user.getString(1), user.getInt(2)+"", user.getInt(3)+""}, 1,true);
				}
			}
			user.Close();
			
			Cursor a = dbHelper.getAchievements();
			if(a.getCount()!= 0) 
			{
				data += "\t<achievements>" + line;
				while(a.moveToNext())
				{
					data += addXml("achievement", new String[] {"name","value"}, new String[] {a.getString(0), a.getInt(1)+""}, 2,true);
				}
				data += "\t</achievements>" + line;
			}
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			dbHelper.Close();
		}
		data += "</backup>"+ line;

		return data;
	}
	
	public String addXml(String startTag, String[] attributes, String[] values, int tabNum, boolean closeTag) 
	{	
		String data = "";
		for(int i=0; i<tabNum; i++) 
			data += "\t";
		data += "<"+startTag+" ";
		for(int i=0; i<attributes.length; i++)
		{
			data+= attributes[i] + "=\"" + values[i] + "\" ";
		}
		if(closeTag)
			data+= "/>" + line;
		else
			data+= ">" + line;
		return data;
	}
	
	private boolean writeData(File myFile, String data) 
	{
		FileOutputStream output = null;
		try{
			
			output = new FileOutputStream(myFile);
			output.write(data.getBytes());
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(output != null) 
			{
				try{
					output.Close();
					return true;
				} catch(IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	//resotre backup	
	public int restoreBackup()
	{
		if(Environment.MEDIA_MOUNTED.equals(state))
		{

			XmlPullParserFactory pullParserFactory;
			FileInputStream fis = null;
			try 
			{ 	Log.d("Nlubej","nlubej");
				pullParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser parser = pullParserFactory.newPullParser();
				File[] files = Environment.getExternalStoragePublicDirectory("Gains").listFiles();
				File file = null;
				File compareFile = new File(Environment.getExternalStoragePublicDirectory("Gains"),"backup.xml");
				for (File f : files) {
					if(f.compareTo(compareFile) == 0)
					{
						file = f;
					}
				}

					
				fis = new FileInputStream(file);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		        parser.setInput(new InputStreamReader(fis));
		        parseXML(parser);
	
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return 0;
			} catch (IOException e) {
				e.printStackTrace();
				return 0;
			} finally {
				try {
					fis.Close();
				} catch (IOException e) {
					e.printStackTrace();
					return 0;
				}
			}
			return 1;
		}
		else 
		{
			return -1;
		}
	}

	private void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		XmlProgram program = null;
		ArrayList<XmlRoutine> routine = null;
		ArrayList<ArrayList<XmlExercise>> exercise = null;
		ArrayList<ArrayList<ArrayList<XmlLog>>> log = null;
		
        int eventType = parser.getEventType();
        String name = null;
        int routineIndexs = 0;
        int exerciseIndeks = 0;
        boolean programAdded = false;
        while (eventType != XmlPullParser.END_DOCUMENT){

            switch (eventType)
            {
                case XmlPullParser.START_DOCUMENT:
                	program = new XmlProgram();
                	routine = new ArrayList<XmlRoutine>();
                	exercise = new ArrayList<ArrayList<XmlExercise>>();
                	log = new ArrayList<ArrayList<ArrayList<XmlLog>>>();
                    break;
                    
                case XmlPullParser.START_TAG:
                	name = parser.getName();
                	Log.d("nlubej","start: " + name);
                	if(name.compareToIgnoreCase("program") == 0) 
                	{
                		program.setName(parser.getAttributeValue(0));
                		programAdded = true;
                	}
                	else if(name.compareToIgnoreCase("routine") == 0) 
                	{
                		routine.add(new XmlRoutine(parser.getAttributeValue(0)));
                		exercise.add(new ArrayList<XmlExercise>());
                		log.add(new ArrayList<ArrayList<XmlLog>>());
                		
                	}
                	else if(name.compareToIgnoreCase("exercise") == 0) 
                	{
                		
                		log.get(routineIndexs).add(new ArrayList<XmlLog>());
                		exercise.get(routineIndexs).add(new XmlExercise(parser.getAttributeValue(0), parser.getAttributeValue(1)));
                		Log.d("nlubej","exercise: " + routineIndexs + " " + exerciseIndeks);
                	}
                	else if(name.compareToIgnoreCase("log") == 0) 
                	{
                		
                		log.get(routineIndexs).get(exerciseIndeks).add(new XmlLog(parser.getAttributeValue(0), parser.getAttributeValue(1), parser.getAttributeValue(2), parser.getAttributeValue(3), parser.getAttributeValue(4),parser.getAttributeValue(5), parser.getAttributeValue(6), parser.getAttributeValue(7)));
                		Log.d("nlubej","routine: " + routineIndexs + " " + exerciseIndeks);
                	}
                	else if(name.compareToIgnoreCase("user") == 0) 
                	{
                		InsertUserData(parser.getAttributeValue(1), parser.getAttributeValue(0), parser.getAttributeValue(2), parser.getAttributeValue(3));
                	}
                	else if(name.compareToIgnoreCase("achievement") == 0) 
                	{
                		
                	}
                		          	
                    break;
                    
                case XmlPullParser.END_TAG:
                	name = parser.getName();
                	Log.d("nlubej","kones: " + name);
                	if(name.compareToIgnoreCase("program") == 0) 
                	{
                		InsertData(program,routine,exercise,log);
                		routineIndexs = 0;
                		exerciseIndeks = 0;
                		
                		
                		routine.clear();
                		exercise.clear();
                		log.clear();
                	}
                	else if(name.compareToIgnoreCase("routine") == 0)
                	{
                		routineIndexs++;
                		exerciseIndeks = 0;
                	}
                	else if(name.compareToIgnoreCase("exercise") == 0) 
                	{
                		exerciseIndeks++;
                	}
                	else if(name.compareToIgnoreCase("log") == 0) 
                	{
                	}
                    break;
            }
            eventType = parser.next();
        }
        	
	}

	private void InsertUserData(String a1, String a2,
			String a3, String a4) {
		dbHelper.open();
		Log.i("nlubej",""+ dbHelper.updateUser(a1,a2,a3,a4));
		dbHelper.Close();
		
	}


	private void InsertData(XmlProgram program,
			ArrayList<XmlRoutine> routine,
			ArrayList<ArrayList<XmlExercise>> exercise,
			ArrayList<ArrayList<ArrayList<XmlLog>>> log) {
		
		long lastProgramId = 0;
		long lastRoutineId = 0;
		long lastExerciseId = 0;
		dbHelper.open();
		
		lastProgramId = dbHelper.insertProgram(program.getName());
		for(int i=0; i<routine.size(); i++) 
		{
			lastRoutineId = dbHelper.insertRoutine(routine.get(i).getName(), lastProgramId);
			for(int j=0; j<exercise.get(i).size(); j++) 
			{
				lastExerciseId = dbHelper.insertExercise(exercise.get(i).get(j).getName(), lastProgramId, lastRoutineId, exercise.get(i).get(j).getType());
				for(int k=0; k<log.get(i).get(j).size(); k++) 
				{
					dbHelper.insertLog(Integer.parseInt(log.get(i).get(j).get(k).getSet()), Double.parseDouble(log.get(i).get(j).get(k).getWeight()), 
							Integer.parseInt(log.get(i).get(j).get(k).getRep()), log.get(i).get(j).get(k).getDuration(), Double.parseDouble(log.get(i).get(j).get(k).getDistance()),
							log.get(i).get(j).get(k).getDate(), Integer.parseInt(log.get(i).get(j).get(k).getWorkoutNum()), log.get(i).get(j).get(k).getType(),
							lastRoutineId, lastExerciseId);
				}
			}
		}
		dbHelper.Close();
		
	}
	 
	 
	 
	 private class SyncData extends AsyncTask<String, Void, String> {
	        @Override
	        protected void onPreExecute() {
	            // set the progress bar view
	            refreshMenuItem.setActionView(R.layout.settings_progress_bar);
	 
	            refreshMenuItem.expandActionView();
	        }
	 
	        @Override
	        protected String doInBackground(String... params) {
	        	
	        	Log.d("nlubej",params[0]+"");
	        	if(params[0].compareTo("create") == 0 ) 
	        	{
	        		int createBackup = createBackup();
	        		if(createBackup == 1) 
		        		return "1";
		        	else if(createBackup == 0)
		        		return "-1";
		        	else 
		        		return "11";
	        	}
	        	else
	        	{
	        		Log.d("Nlubej","restored");
	        		int restoreResult = restoreBackup();
	        		if(restoreResult == 1) 
		        		return "2";
		        	else if(restoreResult == 0)
		        		return "-2";
		        	else
		        		return "22";
	        	}
	        }
	 
	        @Override
	        protected void onPostExecute(String result) {
	            refreshMenuItem.collapseActionView();
	            if(result.compareTo("1") == 0) 
	            	Toast.makeText(getApplicationContext(), "Backup Created",Toast.LENGTH_SHORT).show();
	            else if(result.compareTo("-1") == 0) 
	            	Toast.makeText(getApplicationContext(), "Opps, something went wrong",Toast.LENGTH_SHORT).show();
	            else if(result.compareTo("11") == 0)
	            	Toast.makeText(getApplicationContext(), "External SD card not mounted", Toast.LENGTH_LONG).show();
	            else if(result.compareTo("2") == 0) {
	            	Toast.makeText(getApplicationContext(), "Backup Restored",Toast.LENGTH_SHORT).show();
	            
	            }
	            else if(result.compareTo("-2") == 0) 
	            	Toast.makeText(getApplicationContext(), "Eror.. Bad xml formating or creating a blank backup",Toast.LENGTH_SHORT).show();
	            else if(result.compareTo("22") == 0)
	            	Toast.makeText(getApplicationContext(), "External SD card not mounted", Toast.LENGTH_LONG).show();
	            
	            refreshMenuItem.setActionView(null);
            	refreshMenuItem.setVisible(false);
	        }
	    };
	    
	    
	    
	    
	    
		 @Override
	     public void onActivityResult(int requestCode, int resultCode, Intent data) {
	         super.onActivityResult(requestCode, resultCode, data);
	         uiHelper.onActivityResult(requestCode, resultCode, data);
	     }

		@Override
		public void onSaveInstanceState(Bundle outState) {
		    super.onSaveInstanceState(outState);
		    uiHelper.onSaveInstanceState(outState);
		}


		@Override
		public void onDestroy() {
		    super.onDestroy();
		    uiHelper.onDestroy();
		}
		
		
		private void publishFeedDialog() {
		    Bundle params = new Bundle();
		    params.putString("name", "Gains - Workout Log");
		    params.putString("caption", "Build great social apps and get more installs.");
		    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
		    params.putString("link", "https://developers.facebook.com/android");
		    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(getApplication(),
		            Session.getActiveSession(),
		            params))
		        .setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values, FacebookException error) {
						if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                        Toast.makeText(getApplication(),
		                            "Posted story, id: "+postId,
		                            Toast.LENGTH_SHORT).show();
		                    } else {
		                        // User clicked the Cancel button
		                        Toast.makeText(getApplication().getApplicationContext(), 
		                            "Publish cancelled", 
		                            Toast.LENGTH_SHORT).show();
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                    // User clicked the "x" button
		                    Toast.makeText(getApplication().getApplicationContext(), 
		                        "Publish cancelled", 
		                        Toast.LENGTH_SHORT).show();
		                } else {
		                    // Generic, ex: network error
		                    Toast.makeText(getApplication().getApplicationContext(), 
		                        "Error posting story", 
		                        Toast.LENGTH_SHORT).show();
		                }
					}

		        })
		        .build();
		    feedDialog.show();
		}*/
}