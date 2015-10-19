package nlubej.gains.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;


public class StartEditListener implements  OnFocusChangeListener {

	private EditText edit;
	private String prevValue;
	
	public StartEditListener(EditText edit, String prevValue){
		this.edit = edit;
		this.prevValue= prevValue;
		edit.setSelectAllOnFocus(true);
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus)
			edit.selectAll();
		if(!hasFocus)
		{
			if(edit.getText().toString().compareTo("") == 0)
				edit.setText(prevValue);
				
			else if(prevValue.compareTo("00") == 0) {
				if(edit.getText().toString().length() == 1) {
					edit.setText("0"+edit.getText().toString());
				}
			}
		}
	}
	


}
