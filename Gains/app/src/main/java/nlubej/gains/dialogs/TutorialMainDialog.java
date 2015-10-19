package nlubej.gains.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.ViewFlipper;

import nlubej.gains.DBAdapter;
import nlubej.gains.R;
import nlubej.gains.Start;

public class TutorialMainDialog extends DialogFragment {
	
	SharedPreferences prefs;
	DBAdapter dbHelper;
	ViewFlipper flipper;
	Spinner weightIncrement;
	View view;
	boolean canDismiss;
	int viewcount = 0;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.i("nlubej","main utorial je kriv");
		this.setCancelable(false);
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		canDismiss = false;
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_tutorial_main, null);	
		flipper = (ViewFlipper)view.findViewById(R.id.tutorialFlipper);
		weightIncrement = (Spinner)view.findViewById(R.id.weightIncrementSpinner);
		final RadioButton r1 = (RadioButton)view.findViewById(R.id.radioButton1);
		final RadioButton r2 = (RadioButton)view.findViewById(R.id.radioButton2);
		
		r1.setChecked(true);
		
		r1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					r1.setChecked(true);
					r2.setChecked(false);
				}
			}
		});
		
		r2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					r2.setChecked(true);
					r1.setChecked(false);
				}
				
			}
		});
		
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		builder.setView(view);
		builder.setCancelable(false);
		final AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().requestFeature(STYLE_NO_TITLE);
		alertDialog.show();

		
		
		alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	        	 
	        	  if(viewcount == 0) 
					{
						if(r1.isChecked())
							prefs.edit().putString("unitSystem", "metric").commit();
						else 
							prefs.edit().putString("unitSystem", "imperial").commit();

						
						prefs.edit().putString("weightIncrement", weightIncrement.getSelectedItem().toString()).commit();
						dbHelper.open();
						Log.i("nlubej","output : " + dbHelper.updateUser("tutorialMain",0+""));
						dbHelper.close();
						flipper.showNext();
						viewcount++;
					}
					else {
						alertDialog.dismiss();
					}
	          }
	      });
		
		alertDialog.setCancelable(false);
		
		return alertDialog;
	}
	

	
}