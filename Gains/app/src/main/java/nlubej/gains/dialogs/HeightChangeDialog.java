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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class HeightChangeDialog extends DialogFragment {
	
	EditText height;
	DBAdapter dbHelper;
	View view;
	MyStats fragmentClass;
	boolean deleted = false;
	int prevLength = 0;
	boolean con = true;
	
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
		view = inflater.inflate(R.layout.dialog_change_height, null);
		height = (EditText)view.findViewById(R.id.edit_height);
		
		dbHelper.open();
		Cursor user = dbHelper.getUserData();
		if(user.getCount() != 0) 
		{
			user.moveToFirst();
			height.setText(user.getString(1));
		}
		user.close();
		dbHelper.close();
		
		final String unitSystem = getArguments().getString("unitSystem");
		height.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(unitSystem.compareTo("imperial") == 0)
				{
					if(s.length() == 4)
					{
						Log.i("nlubej",  s.charAt(2) + "    " + s.charAt(3));
						
							if(s.charAt(3) != '1' && s.charAt(3) != '2')
							{
								height.setText(s.subSequence(0, 2));
								height.setSelection(height.getText().length());
								Log.i("nlubej",  "remove 2");
							}
							if(s.charAt(2) != '1' && (s.charAt(3) == '1' || s.charAt(3) == '2'))
							{
								height.setText(s.subSequence(0, 2));
								height.setSelection(height.getText().length());
								Log.i("nlubej",  "remove 3");
							}
							
							
						Log.i("Nlubej","char zdej: " + s.length());
						Log.i("nlubej", "before: " + before  + " now: " + count);
					}
					else if(s.length() > 4)
					{
						height.setText(s.subSequence(0, 4));
						height.setSelection(height.getText().length());
					}
					if(s.length() >= 1)
					{
						if(s.charAt(0) == '\'' || !s.toString().contains("'"))
						{
							height.setText("");
							height.setSelection(height.getText().length());
						}
					}
					if(s.length() ==3)
					{
						if(s.charAt(2) == '\'')
						{
							height.setText(s.subSequence(1, 2));
							height.setSelection(height.getText().length());
						}
					}
					if(s.length() == 1 && before == 0)
					{
						height.setText(s.toString()+"'");
						height.setSelection(height.getText().length());
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				 prevLength = count;
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				/*if(unitSystem.compareTo("imperial") == 0) //preverja samo ko ni metric system
				{
				    int count = s.length();
				    String str = s.toString();
				    Log.i("nlubej", "length :" + count + "  prev: " + prevLength);
				    if(count < prevLength)
				    	deleted = true;
				    if(count == 0)
				    	deleted = false;
				    if (count == 1 && !deleted) {
				        str = str + "'";
				        deleted = true;
				    }
				    else {
				        return;  
				    }
				    height.setText(str);
				    height.setSelection(height.getText().length());
				}*/
			}
		});
		
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
	        	 
	        	  dbHelper.open();
	        	  Log.i("nlubej","height: " + height.getText().toString());
					dbHelper.updateUser("height",height.getText().toString());
					dbHelper.close();
					callback.OnSubmit("Update");
	        	  alertDialog.dismiss();
	          }
	      });
		
		alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
	      {

			@Override
			public void onClick(View v) {
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