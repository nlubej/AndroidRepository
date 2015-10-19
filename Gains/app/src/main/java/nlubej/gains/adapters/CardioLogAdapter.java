package nlubej.gains.adapters;

import java.util.ArrayList;

import nlubej.gains.DBAdapter;
import nlubej.gains.Logg;
import nlubej.gains.R;
import nlubej.gains.interfaces.onSubmit;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class CardioLogAdapter extends BaseAdapter
{
	ArrayList<SingleLogRow> list;
	Context c;
	int[] workoutNums = null;
	long id;
	String[] dates = null;
	Logg fragmentClass;
	private onSubmit callback;
	String unitSystem;
	DBAdapter dbHelper;
	FragmentManager fm;
	
	public CardioLogAdapter(FragmentManager fm, Logg l, Context c, long id, int[] sets, String[] duration, double[] distance, int[] workoutNum, String[] dates, String unitSystem) {
		dbHelper = new DBAdapter(c);
		this.fragmentClass = l;
		this.dates = dates;
		this.c = c;
		this.unitSystem = unitSystem;
		this.workoutNums = workoutNum;
		this.id = id;
		this.fm = fm;
		
		try {
	        callback = (onSubmit) fragmentClass;
	    } catch (ClassCastException e) {
	        throw new ClassCastException("Calling Fragment must implement OnAddFriendListener"); 
	    }
		Log.d("nlubej", "prsl v cardiologadapter");

		list = new ArrayList<SingleLogRow>();
		if(sets != null){
			for(int i=0; i<sets.length; i++){
				Log.d("nlubej", "kok jih je?: " +sets.length);
				list.add(new SingleLogRow(sets[i], duration[i], distance[i], workoutNum[i], dates[i])); //v list dodamo nov item
			}
		}
	}
	
	public CardioLogAdapter(FragmentManager fm, Context c, long id, int[] sets, String[] duration, double[] distance, int[] workoutNum, String[] dates, String unitSystem) {
		dbHelper = new DBAdapter(c);
		Log.d("nlubej", "prsl v cardiologadapter");
		this.c = c;
		this.unitSystem = unitSystem;
		this.workoutNums = workoutNum;
		this.dates = dates;
		this.id = id;
		this.fm = fm;
		
		list = new ArrayList<SingleLogRow>();
		if(sets != null){
			for(int i=0; i<sets.length; i++){
				Log.d("nlubej", "kok jih je?: " +sets.length);
				list.add(new SingleLogRow(sets[i], duration[i], distance[i], workoutNum[i], dates[i])); //v list dodamo nov item
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		int mode = 0;
		LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View row = null;
		if(workoutNums[position]!= 0) 
		{
			row = inflater.inflate(R.layout.log_seperator_sets_list_row, parent ,false);
			mode = 1;
			Log.d("nlubej", "mode 1");
		}
		else 
		{
			row = inflater.inflate(R.layout.log_sets_list_row2, parent ,false);
			mode = 0;
			Log.d("nlubej", "mode 0");
		}
		
		if(mode== 0)
		{
			TextView set = (TextView) row.findViewById(R.id.log_set2);
			TextView distance = (TextView) row.findViewById(R.id.log_distance);
			TextView duration = (TextView) row.findViewById(R.id.log_duration);
			SingleLogRow temp = list.get(position);
			
			set.setText(temp.set+" SET");
			if(unitSystem.compareTo("metric") == 0)
				distance.setText(temp.distance+" KM");
			else
				distance.setText(temp.distance+" MI");
			duration.setText(temp.duration+"");
			
			Log.d("nlubej", "set: " + set.getText() + "   " + " distance: " + distance.getText() + "   " + "duration : "  +duration.getText());
		}
		else 
		{
			TextView workout = (TextView) row.findViewById(R.id.workoutHeader);
			TextView workoutDate = (TextView) row.findViewById(R.id.textDate);
			ImageView noteImage = (ImageView) row.findViewById(R.id.noteImage);
			SingleLogRow temp = list.get(position);

			workout.setText("WORKOUT #" +temp.workoutNum);
			workoutDate.setText(temp.dates);
			
			dbHelper.open();
			String note = dbHelper.getNote(id, temp.workoutNum);
			Log.i("Nlubej", "workout number in StrengthAdapter: " + temp.workoutNum);
			if(note == null)
			{
				noteImage.setBackgroundResource(R.drawable.ic_action_chat_blue);
			}
			else
			{
				noteImage.setBackgroundResource(R.drawable.ic_action_chat_red);
			}
			
			dbHelper.close();
			workout.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					PopupMenu popup = new PopupMenu(c, v);
					popup.getMenuInflater().inflate(R.menu.popup_log, popup.getMenu());
					popup.show();
					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							//Toast.makeText(fragment.getContext(), "Edit", Toast.LENGTH_SHORT).show();
							if(item.getItemId() == R.id.delete) {
								
								
								new AlertDialog.Builder(c)
							    .setTitle("Delete entry")
							    .setMessage("Are you sure you want to delete this entry?")
							    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							        	dbHelper.open();
							        	dbHelper.updateLogNumbers(id,workoutNums[position]);
							        	dbHelper.close();
							        	callback.onSumbitSubmit("update");
							        	
							        }
							     })
							    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							        	//Toast.makeText(c, "no", Toast.LENGTH_SHORT).show();
							        }
							     }).show();
								
								
								
							}
							return false;
						}
					});
					return false;
				}
			});
		}
		
		return row;
	}
	
	
	class SingleLogRow
	{
		
		private int set;
		private double distance;
		private String duration;
		private int workoutNum;
		private String dates;

		
		public SingleLogRow(int set, String duration, double distance, int workoutNum, String dates) {
			this.set = set;
			this.duration = duration;
			this.distance = distance;
			this.workoutNum = workoutNum;
			this.dates = dates;
		}

	}

}