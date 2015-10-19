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
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nlubej.gains.dialogs.AddRoutineDialog;

public class Routine extends Activity implements OnItemClickListener, AddRoutineDialog.NoticeDialogListener {

    private SimpleDragSortCursorAdapter adapter;
    private RoutineAdapter adapter2;
    MatrixCursor cursor;
    boolean canSwitch;
    DBAdapter dbHelper;
    DragSortListView dslv;
    String[] names = null;
    boolean canContinue = true;
    long routineID = 0;
    long programID = 0;
	long[] idss = null;
	String[] idsss = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	canSwitch = false;
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(null);
        Intent intent = getIntent();
        
        String routineName = intent.getStringExtra("programName");
        setTitle(routineName);
    
        programID = intent.getLongExtra("programID", 1);
        
        dslv = (DragSortListView) findViewById(R.id.list);
        dslv.setOnItemClickListener(this);
        init();
    }
    
    
    
    public void init() 
    {
    	dbHelper = new DBAdapter(this);
		
    	names = null;
		idss = null;
		
		dbHelper.open(); 
		Cursor c = dbHelper.getRoutines(programID);
		if(c.getCount() != 0) {
			names = new String[c.getCount()];
			idss = new long[c.getCount()];
			idsss = new String[c.getCount()];
			
			int i=0;
			while(c.moveToNext()){
				idss[i] = c.getLong(0);
				names[i] = c.getString(1);		
				i++;
			}
		}
		dbHelper.close();
		
		 	String[] cols = {"name"};
	        int[] ids = {R.id.text};
		     adapter = new MAdapter(this, R.layout.exercise_list_item_rearrange_row, null, cols, ids, 0);
		     adapter2 = new RoutineAdapter(this, names);
		       	
		     dslv.setAdapter(adapter2);
	
	
		     cursor = new MatrixCursor(new String[] {"_id","name"});
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
        
        // Inflate the menu; this adds items to the action bar if it is present.
		
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add) {
        	if(canSwitch){
        		AddRoutineDialog addRoutine= new AddRoutineDialog();
                
            	
                Bundle arg = new Bundle();
                arg.putString("add", "New Routine");
                Log.d("nlubej", "id pred po�iljanjem: " + programID);
                arg.putLong("programID", programID);
                arg.putString("mode", "Add");
                addRoutine.setArguments(arg);
                addRoutine.show(getFragmentManager(), "addProgram");
        	
        	
        	}
        	else {

        		//DO NOTHING
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
    	        	dbHelper.updateRoutineOrder(newIds, idss);
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
            canContinue = true;
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
    
    
    class RoutineSingleRow
	{
		String titles;
		String sets;

		
		public RoutineSingleRow(String titles) {
			this.titles = titles;
		}
	}



	class RoutineAdapter extends BaseAdapter

	{
		ArrayList<RoutineSingleRow> list;
		Context c;

		String[] titles;
		public RoutineAdapter(Context c, String[] titles) {
			this.c = c;
			list = new ArrayList<RoutineSingleRow>();
			this.titles = titles;
			//String[] titles = res.getStringArray(R.array.titles);
			
			//String[] descriptions = res.getStringArray(R.array.descriptions);

			if(names != null) {
				for(int i=0; i<titles.length; i++){
				
					list.add(new RoutineSingleRow(titles[i])); //v list dodamo nov item
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
		
		class RoutineHolder 
		{
			TextView title;
			ImageView btn;
			
			public RoutineHolder(View v)
			{
				title = (TextView) v.findViewById(R.id.show);
				btn = (ImageView) v.findViewById(R.id.edit_btn);
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RoutineHolder routineHolder = null;

			if(row == null)
			{
				LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.routine_list_row, parent,false);
				routineHolder = new RoutineHolder(row);
				row.setTag(routineHolder);
				
			}
			else
			{
				routineHolder = (RoutineHolder) row.getTag();
			}
			

			RoutineSingleRow temp = list.get(position);
			
			routineHolder.title.setText(temp.titles);
			routineHolder.btn.setTag(position);
		
			routineHolder.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
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
							        	dbHelper.deleteRoutine(idss[position]);
							        	dbHelper.deleteExercisesByRoutine(idss[position]);
							        	dbHelper.deleteLogByRoutine(idss[position]);
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
								AddRoutineDialog addRoutine= new AddRoutineDialog();
				                
				            	
				                Bundle arg = new Bundle();
				                arg.putString("add", "Update Routine");
				                arg.putLong("programID", programID);
				                arg.putLong("routineId", idss[position]);
				                arg.putString("routineName", names[position]);
				                arg.putString("mode", "Edit");
				                addRoutine.setArguments(arg);
				                addRoutine.show(getFragmentManager(), "addProgram");
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(canSwitch){
			Intent i = new Intent(this, nlubej.gains.Exercise.class);
			i.putExtra("routineName",names[(int)id]);
			i.putExtra("routineID", idss[(int) id]);
			Log.d("nlubej", "dajem: " + idss[(int) id] + " in " + programID);
			i.putExtra("programID", programID);
	        startActivity(i);
		}
		
	}


	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String neki) {
		init();
		if(neki.compareTo("Update") == 0)
		{
			Toast.makeText(this, "Routine updated", Toast.LENGTH_SHORT).show();
		}
		else 
			Toast.makeText(this, "Routine added", Toast.LENGTH_SHORT).show();
		
	}
	

}



