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

public class AddExerciseCommentDialog extends DialogFragment {
	
	private DBAdapter dbHelper;
	private EditText exerciseNote;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_exercise_comment, null);
		exerciseNote = (EditText)view.findViewById(R.id.textComment);
		final long exerciseId = getArguments().getLong("exerciseId");
		final int workoutNum = getArguments().getInt("workoutNum");
		final String mode = getArguments().getString("mode");
		builder.setView(view);
		builder.setTitle("Exercise Note");
		if(mode.compareTo("Edit") != 0)
		{
			exerciseNote.setEnabled(false);
			exerciseNote.setTextColor(getResources().getColor(R.color.menu));
		}
		Log.i("nlubej", "last workout v add comment: " + workoutNum);
		
		dbHelper.open();
		final String note = dbHelper.getNote(exerciseId, workoutNum);
		
		if(note != null)
			exerciseNote.setText(note);
		else
			exerciseNote.setText("");
		
		dbHelper.close();
		
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
		
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	        	 dbHelper.open();
	 			 if(note == null && exerciseNote.getText().toString().trim().compareTo("") != 0)
	 			 {
	 				 Log.e("nlubej","inserted : " + exerciseNote.getText().toString().trim());
	 				 dbHelper.insertNote(exerciseId, exerciseNote.getText().toString(), workoutNum);
	 			 }
	 			 else if(note != null)
	 			 {
	 				dbHelper.updateNote(exerciseId, exerciseNote.getText().toString(), workoutNum);
	 			 }
	             dbHelper.close();
	             alertDialog.dismiss();
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