package nlubej.gains;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
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

import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddDialog;
import nlubej.gains.Dialogs.EditDialog;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.interfaces.onActionSubmit;

public class Routine extends Activity implements OnItemClickListener, onActionSubmit
{
    private QueryFactory db;
    private ArrayList<RoutineDto> routineDto;
    private Context context;
    private DragSortListView dslv;
    private SimpleDragSortCursorAdapter sortAdapter;
    private RoutineAdapter routineAdapter;

    MatrixCursor cursor;
    boolean canSwitch = false;

    boolean canContinue = true;
    long programID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(null);

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("PROGRAM_NAME");
        setTitle(routineName);
        programID = intent.getIntExtra("PROGRAM_ID", 1);

        InitComponents();
        SetData();
    }

    private void InitComponents()
    {
        context = getApplicationContext();
        db = new QueryFactory(context);
        dslv = (DragSortListView) findViewById(R.id.list);
        dslv.setOnItemClickListener(this);
        sortAdapter = new SortAdapter(this, R.layout.exercise_list_item_rearrange_row);
    }

    public void SetData()
    {
        db.Open();
        routineDto = db.SelectRoutines();
        db.Close();

        routineAdapter = new RoutineAdapter(this, routineDto);
        dslv.setAdapter(routineAdapter);

        cursor = new MatrixCursor(new String[]{"_id", "name"});
        if (routineDto != null)
        {
            for (int i = 0; i < routineDto.size(); i++)
            {
                cursor.addRow(new Object[] {i, routineDto.get(i).Name});
            }
        }

        sortAdapter.changeCursor(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.add)
        {
            if (canSwitch)
            {
                AddDialog addDialog = new AddDialog();
                addDialog.SetData(Routine.this, AddDialogType.Routine);
                addDialog.show(getFragmentManager(), "");
            }
        }
        if (id == android.R.id.home)
        {
            onBackPressed();
        }
        if (id == R.id.edit)
        {

            if (canSwitch)
            {
                dslv.setAdapter(sortAdapter);
            }
            else
            {
                if (sortAdapter.getCount() != 0)
                {
                    cursor.moveToFirst();
                    int[] newIds = new int[cursor.getCount()];
                    for (int i = 0; i < sortAdapter.getCount(); i++)
                    {
                        newIds[i] = ((int) sortAdapter.getItemId(i)) + 1;
                     //   Log.i("nlubej", "item id: " + (long) sortAdapter.getItemId(i));
                       // Log.i("nlubej", "default id: " + idss[i]);
                        cursor.moveToNext();
                    }

                    db.Open();
                    db.UpdateRoutineOrder(newIds, routineDto);
                    db.Close();
                }

                SetData();
            }
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (canSwitch)
        {
            menu.getItem(1).setIcon(null);
            menu.getItem(1).setTitle("Save");
            canSwitch = false;
        }
        else
        {
            menu.getItem(1).setIcon(R.drawable.white_arrow);
            menu.getItem(1).setTitle("");
            canContinue = true;
            canSwitch = true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private class SortAdapter extends SimpleDragSortCursorAdapter
    {
        public SortAdapter(Context ctxt, int rmid)
        {
            super(ctxt, rmid, null, new String[]{"NAME"}, new int[] {1}, 0);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            return v;
        }
    }

    class RoutineRow
    {
        public String routineName;

        public RoutineRow(String routineName)
        {
            this.routineName = routineName;
        }
    }

    class RoutineAdapter extends BaseAdapter
    {
        ArrayList<RoutineRow> routineRows;
        Context c;


        public RoutineAdapter(Context c, ArrayList<RoutineDto> routineDto)
        {
            this.c = c;
            routineRows = new ArrayList<>();

            if (routineDto != null)
            {
                for (RoutineDto dto : routineDto)
                {
                    routineRows.add(new RoutineRow(dto.Name));
                }
            }
        }


        @Override
        public int getCount()
        {
            return routineRows.size();
        }

        @Override
        public Object getItem(int position)
        {
            return routineRows.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        class RoutineHolder
        {
            TextView routineName;
            ImageView btn;

            public RoutineHolder(View v)
            {
                routineName = (TextView) v.findViewById(R.id.show);
                btn = (ImageView) v.findViewById(R.id.edit_btn);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            RoutineHolder routineHolder = null;

            if (row == null)
            {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_routine, parent, false);
                routineHolder = new RoutineHolder(row);
                row.setTag(routineHolder);

            }
            else
            {
                routineHolder = (RoutineHolder) row.getTag();
            }


            RoutineRow temp = routineRows.get(position);

            routineHolder.routineName.setText(temp.routineName);
            routineHolder.btn.setTag(position);

            routineHolder.btn.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(c, v);
                    popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            if (item.getItemId() == R.id.delete)
                            {
                                new AlertDialog.Builder(c).setTitle("Delete entry").setMessage("Are you sure you want to delete this entry?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        db.Open();
                                        db.DeleteRoutine(routineDto.get(position).Id);
                                        db.Close();

                                        SetData();
                                    }
                                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                    }
                                }).show();
                            }
                            else if (item.getItemId() == R.id.edit)
                            {
                                EditDialog editDialog = new EditDialog();
                                Bundle arg = new Bundle();
                                arg.putString("ROUTINE_NAME", routineDto.get(position).Name);
                                arg.putInt("ROUTINE_ID", routineDto.get(position).Id);
                                editDialog.setArguments(arg);
                                editDialog.SetData(Routine.this, AddDialogType.Routine);
                                editDialog.show(getFragmentManager(), "");

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
    public void OnSubmit(String action)
    {
        SetData();
    }
}



