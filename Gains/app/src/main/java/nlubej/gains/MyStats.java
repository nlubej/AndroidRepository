package nlubej.gains;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nlubej.gains.dialogs.HeightChangeDialog;
import nlubej.gains.dialogs.WeightChangeDialog;
import nlubej.gains.interfaces.onSubmit;

public class MyStats extends Fragment implements OnClickListener, onSubmit {

	SharedPreferences prefs;
	View fragment;
	String unitSystem;
	ImageView height;
	ImageView weight;
	ImageView achievements;
	TextView textHeight;
	TextView textBmi;
	TextView textWeight;
	TextView textAchievements;
	Activity mActivity;
	DBAdapter dbHelper;
	String line = "\r\n";
	
	String state = Environment.getExternalStorageState();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		fragment = inflater.inflate(R.layout.my_stats, container, false);
		
		
		dbHelper = new DBAdapter(fragment.getContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
		// Inflate the layout for this fragment
		
		height = (ImageView)fragment.findViewById(R.id.imageHeight);
		weight = (ImageView)fragment.findViewById(R.id.imageWeightt);
		achievements = (ImageView)fragment.findViewById(R.id.imageAchievements);
		
		textHeight = (TextView)fragment.findViewById(R.id.textHeight);
		textWeight = (TextView)fragment.findViewById(R.id.textWeightt);
		textAchievements = (TextView)fragment.findViewById(R.id.textAchievements);
		textBmi = (TextView)fragment.findViewById(R.id.textBMi);
		unitSystem = "";
		if(prefs.getString("unitSystem", "").compareTo("metric")==0)
			unitSystem = "metric";
		else
			unitSystem = "imperial";
		
		init();
		height.setOnClickListener(this);
		weight.setOnClickListener(this);
		textHeight.setOnClickListener(this);
		textWeight.setOnClickListener(this);
		achievements.setOnClickListener(this);
		dbHelper = new DBAdapter(fragment.getContext());
		
		this.setHasOptionsMenu(true);
		return fragment;
	}

	
	@Override 
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    menu.clear();
	    inflater.inflate(R.menu.settings_menu, menu);
	}


	
	public void init()
	{

    	
		String height;
		String weight;
		dbHelper.open();
		Cursor user = dbHelper.getUserData();
		if(user.getCount() != 0) 
		{
			user.moveToFirst();
			height = user.getString(1);
			weight = user.getString(0);
			Log.d("nlubej","h: " + height + " " + "w: " + weight);
			if(unitSystem.compareTo("metric")== 0) 
			{
				height += " cm";
				weight += " kg";
			}
			else
			{
				height += " ft";
				weight += " lbs";
			}
			
			textHeight.setText(height);
			textWeight.setText(weight);
		}
		user.close();
		dbHelper.close();
		
		calculateBMI();
	}
	
	private void calculateBMI() {
		if(unitSystem.compareTo("metric") == 0) 
		{
			double weight = 0.0;
			double height = 0.0;
			try {
				
				String tmp = textWeight.getText().toString().substring(0,textWeight.getText().toString().length()-3);
				Log.i("nlubej","weight tmp : " + tmp);
				weight = Double.parseDouble(tmp);
				
			}catch (NumberFormatException e)
			{
				e.printStackTrace();
				weight = 0.0;
			}catch (StringIndexOutOfBoundsException e)
			{
				weight = 0.0;
			}
			
			try {
				if(textHeight.getText().toString().length() >= 3) 
				{
					String tmp = textHeight.getText().toString().substring(0,textHeight.getText().toString().length()-3);
					tmp = tmp.substring(0,1) + "." +tmp.substring(1,tmp.length());
					height = Double.parseDouble(tmp);
					Log.i("nlubej","height : " + tmp);
				}
				else if(textHeight.getText().toString().length() == 2)
				{
					String tmp = textHeight.getText().toString();
					tmp = tmp.substring(0,1) + "." +tmp.substring(1,1);
					height = Double.parseDouble(tmp);
					Log.i("nlubej","height2 : " + tmp);
				}
				else
				{
					String tmp = textHeight.getText().toString();
					tmp = tmp.substring(0,1);
					height = Double.parseDouble(tmp);
				}
							
			}catch (NumberFormatException e)
			{
				e.printStackTrace();
				height = 0.0;
			}catch (StringIndexOutOfBoundsException e)
			{
				height = 0.0;
			}
			
			if(weight == 0 || height == 0)
				textBmi.setText("/");
			else
				textBmi.setText(String.format("%.2f",(weight/height/height)));
		}
		else 
		{
			double weight = 0.0;
			double height = 0.0;
			try {
				
				String tmp = textWeight.getText().toString().substring(0,textWeight.getText().toString().length()-4);
				Log.i("nlubej","weight tmp : " + tmp);
				weight = Double.parseDouble(tmp);
				
			}catch (NumberFormatException e)
			{
				e.printStackTrace();
				weight = 0.0;
			}catch (StringIndexOutOfBoundsException e)
			{
				weight = 0.0;
			}
			
			try {
				if(textHeight.getText().toString().length() >= 4) 
				{
					String tmp = textHeight.getText().toString().substring(0,textHeight.getText().toString().length()-3);
					double inch = Double.parseDouble(tmp.substring(0,1)) *12;
					double inch2 = Double.parseDouble("0."+tmp.substring(2,tmp.length()));
					Log.i("nlubej","inch : " + inch + "inch2: " + inch2);
					height = (inch+inch2);
					
				}
				else if(textHeight.getText().toString().length() == 3)
				{
					String tmp = textHeight.getText().toString();
					tmp = tmp.substring(0,1) + "." +tmp.substring(1,1);
					height = Double.parseDouble(tmp);
					Log.i("nlubej","height2 : " + tmp);
				}
				else
				{
					String tmp = textHeight.getText().toString();
					tmp = tmp.substring(0,1);
					height = Double.parseDouble(tmp);
				}
							
			}catch (NumberFormatException e)
			{
				e.printStackTrace();
				height = 0.0;
			}catch (StringIndexOutOfBoundsException e)
			{
				height = 0.0;
			}
			
			if(weight == 0 || height == 0)
				textBmi.setText("/");
			else
				textBmi.setText(String.format("%.2f",((weight*703)/height/height)));	
		}
		
	}


	@Override 
	public void onResume(){
	    super.onResume();
	    init();

	}
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.settings) 
        {
        	Intent i = new Intent(fragment.getContext(), nlubej.gains.Settings.class);
            startActivity(i);   
        }	
        if(id == android.R.id.home){
        	
        	
        }
        return true;
    }
	
	@Override
	public void onClick(View v) {
		if(v == height || v == textHeight)
		{	
			HeightChangeDialog dialog = new HeightChangeDialog();
			dialog.setTargetFragment(MyStats.this, 0);
			Bundle arg = new Bundle();
	        arg.putString("unitSystem", unitSystem); 
	        dialog.setArguments(arg);
			dialog.show(MyStats.this.getActivity().getFragmentManager(), "updateHeight");	
		}
		else if(v == weight || v == textWeight)
		{

			WeightChangeDialog dialog = new WeightChangeDialog();
			dialog.setTargetFragment(MyStats.this, 0);
			dialog.show(MyStats.this.getActivity().getFragmentManager(), "updateWeight");
		}
		else if(v == achievements) 
		{
			Intent i = new Intent(fragment.getContext(), nlubej.gains.Achievements.class);
	        startActivity(i);
		}
	}


	@Override
	public void onSumbitSubmit(String friendEmail) {
		Log.i("nlubej","prsl");
		init();	
	}
	
	

	
	
	
	
}

