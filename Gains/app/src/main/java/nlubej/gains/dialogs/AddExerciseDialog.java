package nlubej.gains.dialogs;

import nlubej.gains.DBAdapter;
import nlubej.gains.R;
import nlubej.gains.dialogs.AddRoutineDialog.NoticeDialogListener;
import nlubej.gains.listeners.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AddExerciseDialog extends DialogFragment {
	
	
	DBAdapter dbHelper;
	EditText name;
	EditText note;
	RadioButton radioButton;
	String type;
	View view;
	Dialog dialogg;
	RadioGroup rg;
	TextView tw;
	int checked = -1;
	ExerciseDialogListener mListener;
	
	String exerciseType;
	String exerciseName;
	String mode;
	long exerciseId;
	
	public interface ExerciseDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String action);
    }
   
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	     try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	    	 mListener = (ExerciseDialogListener) activity;
	     } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	         throw new ClassCastException(activity.toString()
	        		 + " must implement AddExerciseDialogListener");
	     }
	 }
	 
	 
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
		type = "Strength";
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_add_exercise, null);

		
		rg = (RadioGroup) view.findViewById(R.id.radioType);
		name = (EditText) view.findViewById(R.id.nameText);	
		tw = (TextView) view.findViewById(R.id.show);
		tw.setAlpha(0);

		final long programID = getArguments().getLong("programID");
		final long routineID = getArguments().getLong("routineID");
		mode = getArguments().getString("mode");
		
		if(mode.compareTo("Edit") == 0) 
		{
			
			exerciseType = getArguments().getString("exerciseType");
			Log.d("nlubej","exerciseType:");
			Log.d("nlubej","exerciseType: "  + exerciseType);
			exerciseName = getArguments().getString("exerciseName");
			exerciseId = getArguments().getLong("exerciseId");
			
			checked = 0;
			name.setText(exerciseName);
			if(exerciseType.compareTo("Strength") == 0)
			{
				checked = 1;
				rg.check(R.id.strength);
			}
			else 
				rg.check(R.id.cardio);
			
		}
		
		builder.setView(view);
		builder.setTitle("New Exercise");
		
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {			
			}
		}); 
		
		
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		final AlertDialog alertDialog = builder.create();
		alertDialog.show();
		
		final AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
		final AlphaAnimation alpha2 = new AlphaAnimation(1.0f, 0.0f);
		final AlphaAnimation pause = new AlphaAnimation(1.0f, 1.0f);
		
		
		alpha.setDuration(800);
		alpha2.setDuration(800);
		pause.setDuration(1000);
		
		alpha.setAnimationListener(new CustomAnimationListenerAlpha(tw, pause));
		pause.setAnimationListener(new CustomAnimationListenerPause(tw, alpha2));

		
		
		
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	              Boolean wantToCloseDialog = true;
	              
	              try {
	            	  if(mode.compareTo("Edit") == 0) 
	            	  {
	            		  int selectedId = rg.getCheckedRadioButtonId();
							radioButton = (RadioButton) view.findViewById(selectedId);
							type = radioButton.getText().toString();
							wantToCloseDialog = true;
							dbHelper.open();
							boolean i = dbHelper.updateExercise(exerciseId, type, name.getText().toString().trim());			
							
							if(rg.getCheckedRadioButtonId() == R.id.strength && checked == 0)
								dbHelper.deleteLogByExercise(exerciseId);
							
							else if(rg.getCheckedRadioButtonId() == R.id.cardio && checked == 1)
								dbHelper.deleteLogByExercise(exerciseId);
							
							dbHelper.close();
	            		  
	            		  
	            	  }
	            	  else
	            	  {
						int selectedId = rg.getCheckedRadioButtonId();
						radioButton = (RadioButton) view.findViewById(selectedId);
						type = radioButton.getText().toString();
						wantToCloseDialog = true;
						dbHelper.open();
						long i = dbHelper.insertExercise(name.getText().toString().trim(), programID, routineID, type);			
						dbHelper.close();
						Log.d("nlubej", type);
	            	  }
					}
					catch(Exception e) {
						wantToCloseDialog = false;
						tw.setText("Choose a Type");
						
					}
	              
	              
	              if(wantToCloseDialog){
	            	  if(mode.compareTo("Edit") == 0)
	            		  mListener.onDialogPositiveClick(AddExerciseDialog.this,"Update");
	            	  else 
	            		  mListener.onDialogPositiveClick(AddExerciseDialog.this, "Add");
	            	  
	            	  alertDialog.dismiss();
	              }       
	              else {
	          		tw.setAlpha(1);
	          		tw.startAnimation(alpha); 
	              }
	          }
	      });
		
		alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	        	  alertDialog.dismiss();
	          }
	      });

		return alertDialog;
	}
}