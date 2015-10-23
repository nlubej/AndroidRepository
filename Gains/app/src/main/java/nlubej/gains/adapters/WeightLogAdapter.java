package nlubej.gains.adapters;

import java.util.ArrayList;

import nlubej.gains.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;





public class WeightLogAdapter extends BaseAdapter
{
	ArrayList<SingleLogRow> list ;
	Context c;
	double[] weights = null;
	String unitSystem;
	public WeightLogAdapter(Context context, double[] weights, String[] dates,
			String unitSystem) {
		Log.e("nlubej","prsl v log");
		this.c = context;
		this.unitSystem = unitSystem;
		this.weights = weights;
		list = new ArrayList<SingleLogRow>();
		if(weights != null){
			for(int i=0; i<weights.length; i++){
			
				list.add(new SingleLogRow( weights[i], dates[i])); //v list dodamo nov item
			}
		}
	}


	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int mode = 0;
		LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View row = null;
		Log.e("nlubej","prsl je do sm, lahk zbrises");
		if(weights[position] == -1) 
		{
			row = inflater.inflate(R.layout.log_weight_seperator_sets_list_row, parent ,false);
			mode = 1;
		}
		else 
		{
			row = inflater.inflate(R.layout.log_weight_list_row, parent ,false);
			mode = 0;
		}
		
		if(mode== 0)
		{
			TextView weight = (TextView) row.findViewById(R.id.log_kg);

			SingleLogRow temp = list.get(position);
			
			if(unitSystem.compareTo("metric") == 0)
				weight.setText(temp.weight+" KG");
			else
				weight.setText(temp.weight+" LBS");
		}
		else 
		{
			TextView workout = (TextView) row.findViewById(R.id.workoutHeader);
			SingleLogRow temp = list.get(position);
			
			Log.i("nlubej","date: " + temp.date);
			workout.setText(temp.date);
		}
		
		return row;
	}
	
	
	class SingleLogRow
	{
		
		private double weight;
		private String date;

		
		public SingleLogRow(double weight, String date) {
			this.weight = weight;
			this.date = date;
		}
	}

}