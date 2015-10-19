package nlubej.gains.dialogs;

import nlubej.gains.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class SendFeefbackDialog extends DialogFragment {
	
	EditText exercise;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
		//String ime = getArguments().getString("exerciseName");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_add_sets, null);
		//exercise = (EditText) view.findViewById(R.id.editWeight);
		//exercise.setText(ime);
		builder.setView(view);
		builder.setTitle("Send Feedback");
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}); 
		
		builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "gains.workoutlog@gmail.com"));
				intent.putExtra(Intent.EXTRA_SUBJECT, "");
				intent.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(intent);
			}
		});
		
		
		Dialog dialog = getDialog();
		dialog = builder.create();
		return dialog;
		//return inflater.inflate(R.layout.program_dialog, null);
	}


}