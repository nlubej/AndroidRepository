package nlubej.gains.listeners;

import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class WeightListener implements  OnClickListener {

	SharedPreferences prefs;
	private EditText edit;
	private String mode;
	private double increase;
	
	public WeightListener(EditText edit, String mode, double increase){
		this.edit = edit;
		this.mode = mode;
		this.increase = increase;
	}
	
	@Override
	public void onClick(View v) {
		if(mode.compareTo("+")==0){
			try {
				double value = Double.parseDouble(edit.getText().toString());
				value = value+increase;
				edit.setText(value+"");
			}
			catch(NumberFormatException e){
				edit.setText("0");
			}
			
		}
		else {
			try {
				double value = Double.parseDouble(edit.getText().toString());
				
				if(value > 0.0) {
					if(value-increase >= 0){
						value = value-increase;
						edit.setText(value+"");
					}
				}
			}
			catch(NumberFormatException e){
				edit.setText("0");
			}
		}
		
	}
	


}
