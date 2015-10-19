package nlubej.gains.dialogs;

import nlubej.gains.DBAdapter;
import nlubej.gains.MainActivity;
import nlubej.gains.R;
import nlubej.gains.listeners.CustomAnimationListenerAlpha;
import nlubej.gains.listeners.CustomAnimationListenerPause;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AddRoutineDialog extends DialogFragment {
	
	DBAdapter dbHelper;
	EditText routine;
	NoticeDialogListener mListener;
	TextView eror;
	
	String routineName;
	String mode;
	long routineId;
	
	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog, String neki);
    }
    
    
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (NoticeDialogListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement NoticeDialogListener");
	        }
	    }
	 
    
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		
		
		String tittle = getArguments().getString("add");
		final long id = getArguments().getLong("programID");
		
		Log.d("nlubej", "prjemem id: " + id + " " + tittle + "");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_routine, null);
		routine = (EditText)view.findViewById(R.id.add_routine_name);
		eror = (TextView)view.findViewById(R.id.eror_routine);
		eror.setAlpha(0);
		
		
		mode = getArguments().getString("mode");
		
		if(mode.compareTo("Edit") == 0) 
		{
			routineName = getArguments().getString("routineName");
			routineId = getArguments().getLong("routineId");
			routine.setText(routineName);
			
		}
		
		builder.setView(view);
		builder.setTitle(tittle);
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
		
		alpha.setAnimationListener(new CustomAnimationListenerAlpha(eror, pause));
		pause.setAnimationListener(new CustomAnimationListenerPause(eror, alpha2));

		
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	              Boolean wantToCloseDialog = true;


	              if(mode.compareTo("Edit") == 0)
	              {
	            	  dbHelper.open();
	            	  dbHelper.updateRoutine(routineId, routine.getText().toString());
	            	  dbHelper.close();	
	              }
	              else 
	              {
	            	  
	              
		              if(routine.getText().toString().compareTo("") != 0){
			            	dbHelper.open();
			  				dbHelper.insertRoutine(routine.getText().toString(), id);
			  						
			
						  }
			          	  else {
								wantToCloseDialog = false;
								eror.setText("Name cant be empty");
						  }
	              
	              }
	              
	              if(wantToCloseDialog){
	            	  if(mode.compareTo("Edit") == 0)
	            		  mListener.onDialogPositiveClick(AddRoutineDialog.this,"Update");
	            	  else 
	            		  mListener.onDialogPositiveClick(AddRoutineDialog.this,"Add");
	            	  alertDialog.dismiss();
	              }       
	              else {
	            	  
	            	  eror.setAlpha(1);
	            	  //eror.getBackground().setAlpha(150);
	            	  eror.startAnimation(alpha); 
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