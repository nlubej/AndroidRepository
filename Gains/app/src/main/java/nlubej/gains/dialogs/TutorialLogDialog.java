package nlubej.gains.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import nlubej.gains.DBAdapter;
import nlubej.gains.R;

public class TutorialLogDialog extends DialogFragment {	
	
	DBAdapter dbHelper;
	View view;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
	Log.i("nlubej","log utorial je kriv");
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_tutorial_log, null);	
		
		
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				
			}
		});
		
		builder.setView(view);
		
		AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().requestFeature(STYLE_NO_TITLE);
		alertDialog.show();
		

		return alertDialog;
	}
}