package nlubej.gains.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class UpdateListener implements  OnClickListener {

	private Button update;
	private Button delete;
	private String mode;
	
	public UpdateListener(Button update, Button delete){
		this.update = update;
		this.delete = delete;
	}
	
	@Override
	public void onClick(View v) {
		delete.setText("Clear");
		update.setText("Add");
		
	}
	


}
