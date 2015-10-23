package nlubej.gains.Dialogs;

import nlubej.gains.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class ExerciseSaveDialog extends DialogFragment {
	
	EditText num;
	ImageButton plus;
	ImageButton minus;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		
	
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	
		builder.setTitle("test");
		builder.setMessage("moj message");
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}); 
		
		
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				
			}
		});
		


		Dialog dialog = getDialog();
		dialog = builder.create();
		return dialog;
		//return inflater.inflate(R.layout.program_dialog, null);
	}


}