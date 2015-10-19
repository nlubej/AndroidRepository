package nlubej.gains.adapters;

import java.util.ArrayList;

import nlubej.gains.DBAdapter;
import nlubej.gains.Logg;
import nlubej.gains.Program;
import nlubej.gains.R;
import nlubej.gains.dialogs.AddExerciseCommentDialog;
import nlubej.gains.dialogs.AddProgramDialog;
import nlubej.gains.interfaces.onSubmit;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;





public class StrengthLogAdapter extends BaseAdapter
{
	ArrayList<SingleLogRow> list;
	Context c;
	int[] workoutNums = null;
	String[] dates = null;
	long id = 0	;
	String unitSystem;
	Logg fragmentClass;
	private onSubmit callback;
	DBAdapter dbHelper;
	FragmentManager fm;
	
	
	public StrengthLogAdapter(FragmentManager fm, Logg l, Context c, long id, int[] sets, double[] weights, int[] reps, int[] workoutNum, String[] dates, String unitSystem) {
		this.fragmentClass = l;
		
		try {
	        callback = (onSubmit) fragmentClass;
	    } catch (ClassCastException e) {
	        throw new ClassCastException("Calling Fragment must implement OnAddFriendListener"); 
	    }
		this.fm = fm;
		this.c = c;
		dbHelper = new DBAdapter(c);
		this.workoutNums = workoutNum;
		this.unitSystem = unitSystem;
		this.dates = dates;
		this.id = id;
		list = new ArrayList<SingleLogRow>();
		if(sets != null){
			Log.d("Nlubej","ni null");
			for(int i=0; i<sets.length; i++){
			
				list.add(new SingleLogRow(sets[i], weights[i], reps[i], workoutNum[i], dates[i])); //v list dodamo nov item
			}
		}
	}
	
	public StrengthLogAdapter(FragmentManager fm, Context c, long id, int[] sets, double[] weights, int[] reps, int[] workoutNum, String[] dates, String unitSystem) {
		this.c = c;
		dbHelper = new DBAdapter(c);
		this.workoutNums = workoutNum;
		this.unitSystem = unitSystem;
		this.id = id;
		this.fm = fm;
		this.dates = dates;
		list = new ArrayList<SingleLogRow>();
		if(sets != null){
			Log.d("Nlubej","ni null");
			for(int i=0; i<sets.length; i++){
			
				list.add(new SingleLogRow(sets[i], weights[i], reps[i], workoutNum[i], dates[i])); //v list dodamo nov item
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
		}
		else 
		{
			row = inflater.inflate(R.layout.log_sets_list_row, parent ,false);
			mode = 0;
		}
		
		if(mode== 0)
		{
			TextView set = (TextView) row.findViewById(R.id.log_set);
			TextView weight = (TextView) row.findViewById(R.id.log_kg);
			TextView rep = (TextView) row.findViewById(R.id.log_rep);
			SingleLogRow temp = list.get(position);
			
			set.setText(temp.getSet()+" SET");
			Log.d("nlubej", "unit system tam not : " + unitSystem);
			if(unitSystem.compareTo("metric") == 0)
				weight.setText(temp.getWeight()+" KG");
			else
				weight.setText(temp.getWeight()+" LBS");
			
			rep.setText(temp.getRep()+" REP");
		}
		else 
		{
			TextView workout = (TextView) row.findViewById(R.id.workoutHeader);
			TextView workoutDate = (TextView) row.findViewById(R.id.textDate);
			ImageView noteImage = (ImageView) row.findViewById(R.id.noteImage);
			final SingleLogRow temp = list.get(position);
			
			workout.setText("WORKOUT #" +temp.getWorkoutNum());
			workoutDate.setText(temp.getDate());
			
			dbHelper.open();
			String note = dbHelper.getNote(id, temp.getWorkoutNum());
			Log.i("Nlubej", "workout number in StrengthAdapter: " + temp.getWorkoutNum());
			if(note == null || note.trim() == "")
			{
				noteImage.setBackgroundResource(R.drawable.ic_action_chat_blue);
			}
			else
			{
				noteImage.setBackgroundResource(R.drawable.ic_action_chat_red);
			}
			dbHelper.close();
			
			noteImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AddExerciseCommentDialog dialog = new AddExerciseCommentDialog();
		    		Bundle arg = new Bundle();
		    		arg.putLong("exerciseId", id);
		    		arg.putInt("workoutNum", temp.getWorkoutNum());//�e ni dodal, vrnemo -1
		    		dialog.setArguments(arg);
		    		arg.putString("mode","NoEdit");
		    		dialog.show(fm, "add comment"); 
		    		
				}
			});
			
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
		private double weight;
		private int rep;
		private int workoutNum;
		private String date;
		
		public SingleLogRow(int set, double weight, int rep, int workoutNum, String dates) {
			this.setSet(set);
			this.setWeight(weight);
			this.setRep(rep);
			this.setWorkoutNum(workoutNum);
			this.setDate(dates);
		}


		public int getWorkoutNum() {
			return workoutNum;
		}


		public void setWorkoutNum(int workoutNum) {
			this.workoutNum = workoutNum;
		}


		public double getWeight() {
			return weight;
		}


		public void setWeight(double weight) {
			this.weight = weight;
		}


		public int getSet() {
			return set;
		}


		public void setSet(int set) { 
			this.set = set;
		}


		public int getRep() {
			return rep;
		}


		public void setRep(int rep) {
			this.rep = rep;
		}
		
		public String getDate() {
			return date;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
	}
	
	public void setTargetFragment(Logg fragment2,
			int requestCode) {
		this.fragmentClass = fragment2;
		
	}

}