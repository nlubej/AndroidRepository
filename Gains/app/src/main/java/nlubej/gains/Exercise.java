package nlubej.gains;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import nlubej.gains.dialogs.AddExerciseDialog;


public class Exercise extends Activity implements AddExerciseDialog.ExerciseDialogListener {

    private SimpleDragSortCursorAdapter adapter;
    private ExerciseAdapter adapter2;
    boolean canSwitch;
    DBAdapter dbHelper;
    DragSortListView dslv;
    long routineID = 0;
    long programID = 0;
    String[] names = null; 
	long[] idss = null;
	String[] type = null; 
	MatrixCursor cursor;


    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	canSwitch = false;
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(null);
        
        Intent intent = getIntent();
        
        String routineName = intent.getStringExtra("routineName");
        setTitle(routineName);
    
        routineID = intent.getLongExtra("routineID", 1);
        programID = intent.getLongExtra("programID", 1);
        
        
        Log.d("nlubej", "prejmem: " + routineID + " in " + programID);
        
        dslv = (DragSortListView) findViewById(R.id.list);
        init();
    }
    
    
    
    public void init() 
    {
    	dbHelper = new DBAdapter(this);
		
    	names = null;
		idss = null;
		type = null;
		
		dbHelper.open();
		Cursor c = dbHelper.getExercises(routineID);
		if(c.getCount() != 0) {
			names = new String[c.getCount()];
			idss = new long[c.getCount()];
			type = new String[c.getCount()];
			
			int i=0;
			while(c.moveToNext()){
				idss[i] = c.getInt(0);
				names[i] = c.getString(1);
				type[i] = c.getString(2);
				Log.i("nlubej","name : " + names[i]);
				i++;
			}
		}
		dbHelper.close();
		
			 String[] cols = {"name"};
		     int[] ids = {R.id.text};
		     adapter = new MAdapter(this, R.layout.exercise_list_item_rearrange_row, null, cols, ids, 0);
		     adapter2 = new ExerciseAdapter(this, names);
		       	
		     dslv.setAdapter(adapter2);
	
		     cursor = new MatrixCursor(new String[] {"_id", "name"});
		     if(names != null) {
			     for(int i=0; i< names.length; i++){
			    	 Log.d("nlubej","test");
				     Object[] fsii = new Object[2];
				     fsii[0]= i;
				     fsii[1]= names[i];
				     cursor.addRow(fsii);
			     }
		     }
			
		    adapter.changeCursor(cursor);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add) {
        	if(canSwitch){
        		AddExerciseDialog dialog = new AddExerciseDialog();
        		Bundle arg = new Bundle();
        		arg.putLong("programID", programID);
        		arg.putLong("routineID", routineID);
        		arg.putString("mode", "Add");
        		dialog.setArguments(arg);
        		dialog.show(getFragmentManager(), "add exercise");     	
        	}

        	

        }
        if(id == android.R.id.home){
        	
        	onBackPressed();
        }
        if(id == R.id.edit){
        	if(canSwitch){
        		dslv.setAdapter(adapter);
        	}
        	else
        	{
        		if(adapter.getCount() != 0)
	        	{
    	        	cursor.moveToFirst();
    	        	long[] newIds = new long[cursor.getCount()];
    	        	for(int i=0; i<adapter.getCount(); i++){
    	        		newIds[i] = ((long)adapter.getItemId(i))+1;
    	        		Log.i("nlubej","item id: " + (long)adapter.getItemId(i));
    	        		Log.i("nlubej","default id: " + idss[i]);
    	        		cursor.moveToNext();        	        		
    	        	}
    	        	
    	        	dbHelper.open();
    	        	dbHelper.updateExerciseOrder(newIds, idss);
    	        	dbHelper.close();
	        	}
	        	init();
        	}
        	invalidateOptionsMenu();
        }
        

        
        return super.onOptionsItemSelected(item);
    }
    
    
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
 
        if(canSwitch){
            menu.getItem(1).setIcon(null);
            menu.getItem(1).setTitle("Save");
            canSwitch = false;
        }
        else{
            menu.getItem(1).setIcon(R.drawable.white_arrow);
            menu.getItem(1).setTitle("");
            canSwitch = true;
        }
 
        return super.onPrepareOptionsMenu(menu);
    }
    
    
    private class MAdapter extends SimpleDragSortCursorAdapter {

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols, int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            return v;
        }
    }
    
    
    class ExerciseSingleRow
	{
		String titles;
		
		public ExerciseSingleRow(String titles) {
			this.titles = titles;
		}
	}



	class ExerciseAdapter extends BaseAdapter

	{
		ArrayList<ExerciseSingleRow> list;
		Context c;
		
		String[] titles;
		public ExerciseAdapter(Context c, String[] titles) {
			this.c = c;
			list = new ArrayList<ExerciseSingleRow>();
			this.titles = titles;
			if(names != null) {
				for(int i=0; i<titles.length; i++){
				
					list.add(new ExerciseSingleRow(titles[i])); //v list dodamo nov item
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
		
		class ExerciseHolder 
		{
			TextView title;
			ImageView btn;
			
			public ExerciseHolder(View v)
			{
				title = (TextView) v.findViewById(R.id.show);
				btn = (ImageView) v.findViewById(R.id.edit_btn);
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			ExerciseHolder ExerciseHolder = null;

			if(row == null)
			{
				LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.routine_list_row, parent,false);
				ExerciseHolder = new ExerciseHolder(row);
				row.setTag(ExerciseHolder);
				
			}
			else
			{
				ExerciseHolder = (ExerciseHolder) row.getTag();
			}
			

			ExerciseSingleRow temp = list.get(position);
			
			ExerciseHolder.title.setText(temp.titles);
			ExerciseHolder.btn.setTag(position);
		
			ExerciseHolder.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					PopupMenu popup = new PopupMenu(c, v);
					popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
					popup.show();
					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							
							if(item.getItemId() == R.id.delete) {
								
								
								new AlertDialog.Builder(c)
							    .setTitle("Delete entry")
							    .setMessage("Are you sure you want to delete this entry?")
							    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							        	dbHelper.open();
							        	dbHelper.deleteExercise(idss[position]);
							        	dbHelper.deleteLogByExercise(idss[position]);
							        	dbHelper.close();
							        	init();
							        }
							     })
							    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
							        public void onClick(DialogInterface dialog, int which) { 
							        }
							     })
							     .show();
								
								
							}
							else if(item.getItemId() == R.id.edit) {
								AddExerciseDialog dialog = new AddExerciseDialog();
				        		Bundle arg = new Bundle();
				        		arg.putLong("programID", programID);
				        		arg.putLong("routineID", routineID);
				        		arg.putString("mode", "Edit");
				        		
				        		Log.d("nlubej","type: " + type[position]);
				        		arg.putString("exerciseType", type[position]);
				        		arg.putString("exerciseName", names[position]);
				        		arg.putLong("exerciseId", idss[position]);
				    			
				    	
				        		dialog.setArguments(arg);
				        		dialog.show(getFragmentManager(), "edit exercise");   
								
							}
							return false;
						}
					});
				}
			});

			return row;
		}
	}



	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String action) {
		init();
		if(action.compareTo("Update") == 0) 
			Toast.makeText(this, "Exercise updated", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Exercise added", Toast.LENGTH_SHORT).show();
	}

}



