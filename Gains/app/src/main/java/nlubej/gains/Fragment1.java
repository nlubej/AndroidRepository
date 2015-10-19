package nlubej.gains;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */

public class Fragment1 extends Fragment{


	Button gains;
	View fragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment = inflater.inflate(R.layout.fragment1, container, false);
		gains = (Button)fragment.findViewById(R.id.gains);
		
		gains.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(fragment.getContext(), nlubej.gains.Start.class);
		        startActivity(i);
		        
				
			}
		});
		
		getActivity().getActionBar().setLogo(R.drawable.logo);
		return fragment;
		
	}

}

