package nlubej.gains.Dialogs;

import nlubej.gains.DBAdapter;
import nlubej.gains.MyStats;
import nlubej.gains.R;
import nlubej.gains.interfaces.onActionSubmit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class WeightChangeDialog extends DialogFragment {
	
	EditText weight;
	DBAdapter dbHelper;
	View view;
	MyStats fragmentClass;
	boolean deleted = false;
	int prevLength = 0;
	String prevWeight;
	private onActionSubmit callback;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
             callback = (onActionSubmit) fragmentClass;
         } catch (ClassCastException e) {
             throw new ClassCastException("Calling Fragment must implement OnAddFriendListener"); 
         }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		dbHelper = new DBAdapter(getActivity().getApplicationContext());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.dialog_change_weight, null);
		weight = (EditText)view.findViewById(R.id.edit_weightt);
		prevWeight = "";
		dbHelper.open();
		Cursor user = dbHelper.getUserData();
		if(user.getCount() != 0) 
		{
			user.moveToFirst();
			prevWeight = user.getString(0);
			weight.setText(user.getString(0));
			weight.selectAll();
		}
		user.close();
		dbHelper.close();
		builder.setView(view);
		
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
		alertDialog.getWindow().requestFeature(STYLE_NO_TITLE);
		alertDialog.show();
		
		
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
	      {            
	          @Override
	          public void onClick(View v)
	          {
	        	  Log.i("nlubej", "prev: " + prevWeight + " now: " + weight.getText().toString());
	        	  if(prevWeight.compareTo(weight.getText().toString()) != 0)
	        	  {
	        		  dbHelper.open();
	        		  Log.i("Nlubej", "inserted:  " + dbHelper.insertLog(weight.getText().toString()));
	        		  dbHelper.close();
	        		}
	        	 
	        	  dbHelper.open();
				  dbHelper.updateUser("weight",weight.getText().toString());
				  dbHelper.close();
				  callback.OnSubmit("Update");
	        	  alertDialog.dismiss();
	          }
	      });
		
		alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
	      {

			@Override
			public void onClick(View v) {
				weight.getText().toString();
				alertDialog.dismiss();
			}            
	      });

		return alertDialog;
	}
	
	
	public void setTargetFragment(MyStats fragment2,
			int requestCode) {
		this.fragmentClass = fragment2;
		
	}


}