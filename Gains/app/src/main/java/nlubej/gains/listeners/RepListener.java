package nlubej.gains.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class RepListener implements  OnClickListener {

	private EditText edit;
	private String mode;
	
	public RepListener(EditText edit, String mode){
		this.edit = edit;
		this.mode = mode;
	}
	
	@Override
	public void onClick(View v) {
		if(mode.compareTo("+")==0){
			try {
				int value = Integer.parseInt(edit.getText().toString());
				value = value+1;
				edit.setText(value+"");
			}
			catch(NumberFormatException e){
				edit.setText("0");
			}
			
		}
		else {
			try {
				int value = Integer.parseInt(edit.getText().toString());
				
				if(value > 0) {
					value = value-1;
					edit.setText(value+"");
				}
			}
			catch(NumberFormatException e){
				edit.setText("0");
			}
		}
		
	}
	


}
