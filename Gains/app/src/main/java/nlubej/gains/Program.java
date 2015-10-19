package nlubej.gains;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import nlubej.gains.dialogs.AddProgramDialog;
import nlubej.gains.interfaces.*;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class Program extends Fragment implements OnItemClickListener, onSubmit{

	View fragment;
	ListView list;
	TextView program_added;
	ArrayAdapter<String> adapter;
	DBAdapter dbHelper;
	SharedPreferences prefs;
	String[] names = null;
	long[] ids = null;
	public static long defaultProgram = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
			
			fragment = inflater.inflate(R.layout.fragment2, container, false);
			prefs = PreferenceManager.getDefaultSharedPreferences(fragment.getContext());
			list = (ListView) fragment.findViewById(R.id.listView);
			//program_added = (TextView) fragment.findViewById(R.id.show_routine_added);
		//	program_added.setAlpha(0);
			init();

	        list.setOnItemClickListener(this);
	        this.setHasOptionsMenu(true);

			return fragment;
	}

	
	public void init(){

		names = null;
		ids = null;
		
		dbHelper = new DBAdapter(fragment.getContext());
		//dbHelper.insertProgram("5x5 StrongLifts");
		
		
		dbHelper.open();
		Cursor c = dbHelper.getPrograms();
		if(c.getCount() != 0) {
			names = new String[c.getCount()];
			ids = new long[c.getCount()];
			
			int i=0, j=0;
				while(c.moveToNext()){
				ids[j] = c.getInt(0);
				j++;
				names[i] = c.getString(1);
				i++;
				
			}
		}
		
		c.close();
		dbHelper.close();
	
		list.setAdapter(new ProgramAdapter(fragment.getContext(), names));
		
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		
		Intent i = new Intent(fragment.getContext(), nlubej.gains.Routine.class);
		i.putExtra("programName",names[(int)id]);
		i.putExtra("programID", ids[(int) id]); 
		
        startActivity(i);
        

	}
	
	
	@Override 
	public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    Log.d("nlubej","prsl");
	    menu.clear();
	    inflater.inflate(R.menu.menu_program, menu);
	}
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add) {
        	AddProgramDialog addProgram = new AddProgramDialog();
        	addProgram.setTargetFragment(this, 0);
	        Bundle arg = new Bundle();
	        arg.putString("add", "New Program");
	        arg.putString("mode", "Add");
	        addProgram.setArguments(arg);
	        addProgram.show(this.getActivity().getFragmentManager(), "addProgram");

	       
        }	
        if(id == android.R.id.home){
        	
        	
        	//onBackPressed();
        }
        return true;
    }




//base adapter za ListView
class SingleRow2
{
	String titles;

	
	public SingleRow2(String titles) {
		this.titles = titles;
	}
}



class ProgramAdapter extends BaseAdapter

{
	ArrayList<SingleRow2> list;
	Context c;
	
	public ProgramAdapter(Context c, String[] data) {
		this.c = c;
		list = new ArrayList<SingleRow2>();
		//String[] titles = res.getStringArray(R.array.titles);
		//String[] descriptions = res.getStringArray(R.array.descriptions);
		if(data != null){
			Log.d("Nlubej","ni null");
			for(int i=0; i<data.length; i++){
			
				list.add(new SingleRow2(data[i])); //v list dodamo nov item
			}
		}
	}
	

	
	@Override
	public int getCount() {
		return list.size();
	}
	
	public int getIndeks(String id) {
		for(int i=0; i<ids.length; i++)
		{
			if(id.compareTo(ids[i]+"")== 0) 
				return i;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class MyViewHolder2 
	{
		TextView title;
		ImageView btn;
		ImageView defaultProgram;
		
		public MyViewHolder2(View v)
		{
			title = (TextView) v.findViewById(R.id.show);
			btn = (ImageView) v.findViewById(R.id.edit_btn);
		//	defaultProgram = (ImageView) v.findViewById(R.id.defaultProgram);
		}
	}

	int i = 0;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MyViewHolder2 holder = null;

		if(row == null)
		{
				LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.fragment2_list_row, parent,false);
				holder = new MyViewHolder2(row);
				row.setTag(holder);	
				Log.d("nlubej", "position: " + position+1);
				try {
					if(defaultProgram  == ids[position])
					{
						holder.title.setTextColor(getResources().getColor(R.color.menu));
					}
					else if((ids[position]) == Integer.parseInt(prefs.getString("defaultProgram", "")))
						holder.title.setTextColor(getResources().getColor(R.color.menu));
				} catch(NumberFormatException e)
				{
					e.printStackTrace();
					holder.title.setTextColor(getResources().getColor(R.color.menu));
				    prefs.edit().putString("defaultProgram", ids[0]+"").apply();
				}
				
		}
		else
		{
			holder = (MyViewHolder2) row.getTag();
		}
		

		final SingleRow2 temp = list.get(position);
		
		holder.title.setText(temp.titles);
		
		i++;
		
		holder.btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(c, v);
				popup.getMenuInflater().inflate(R.menu.popup_program, popup.getMenu());
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
						        	
						        	if(names.length == 1)
						        	{
						        		prefs.edit().putString("defaultProgram", null).apply();
						        	}
						        	else if(position+1 == Integer.parseInt(prefs.getString("defaultProgram", "")) && names.length > 1)
						        	{
						        		prefs.edit().putString("defaultProgram", ids[0]+"").apply();
						        	}
						        	else if(names.length == 2) 
						        	{
						        		prefs.edit().putString("defaultProgram", ids[1]+"").apply();
						        	}
						        	else if(position+1 < Integer.parseInt(prefs.getString("defaultProgram", "")))
						        	{
						        		int prefIndeks = getIndeks(prefs.getString("defaultProgram", ""));
						        		prefs.edit().putString("defaultProgram", ids[prefIndeks-1] +"").apply();
						        	}
						        	dbHelper.open();
						        	dbHelper.deleteProgram(ids[position]);
						        	dbHelper.deleteRoutineByProgram(ids[position]);
						        	dbHelper.deleteExercisesByProgram(ids[position]);
						        	Cursor c = dbHelper.getRoutines(ids[position]);
						        	if(c.getCount() != 0) 
						        	{
						        		while(c.moveToNext())
						        		{
						        			dbHelper.deleteLogByRoutine(c.getLong(0));
						        		}
						        	}
						        	dbHelper.close();
						        	init();
						        }
						     })
						    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int which) { 
						        }
						     }).show();
							
							
							
						}
						else if(item.getItemId() == R.id.edit)
						{
							AddProgramDialog addProgram = new AddProgramDialog();
				        	addProgram.setTargetFragment(Program.this, 0);
					        Bundle arg = new Bundle();
					        arg.putString("add", "Update Program"); 
							arg.putLong("programId", ids[position]);
			                arg.putString("programName", names[position]);
			                arg.putString("mode", "Edit");
			                Log.d("nlubej","programName: " + names[position]);
			                addProgram.setArguments(arg);
					        addProgram.show(Program.this.getActivity().getFragmentManager(), "updateProgram");
						
						}
						else if(item.getItemId() == R.id.setDefault)
						{
							defaultProgram = ids[position];
							prefs.edit().putString("defaultProgram", ids[position] +"").apply();
							init();
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
public void onSumbitSubmit(String action) {
	init();
	if(action.compareTo("Update")==0)
	{
		Toast.makeText(fragment.getContext(), "Program updated", Toast.LENGTH_SHORT).show();
	}
	else
		Toast.makeText(fragment.getContext(), "Program added", Toast.LENGTH_SHORT).show();
	
}


}