package nlubej.gains.Activities;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.malinskiy.materialicons.widget.IconTextView;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddDialog;
import nlubej.gains.Dialogs.EditDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.R;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.interfaces.onActionSubmit;

public class Routine extends AppCompatActivity implements OnItemClickListener, onActionSubmit, OnClickListener
{
    private QueryFactory db;
    private ArrayList<RoutineDto> routineDto;
    private Context context;
    private DragSortListView dslv;
    private SimpleDragSortCursorAdapter sortAdapter;
    private RoutineAdapter routineAdapter;
    private FloatingActionButton addButton;
    private MatrixCursor cursor;
    private boolean canSwitch = false;
    private int programId = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

       // ActionBar actionBar = getActionBar();
       //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setLogo(null);

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("PROGRAM_NAME");
        programId = intent.getIntExtra("PROGRAM_ID", 1);

        setTitle(routineName);
        InitComponents();
        SetData();
    }

    private void InitComponents()
    {
        context = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QueryFactory(context);
        dslv = (DragSortListView) findViewById(R.id.list);
        dslv.setOnItemClickListener(this);
        sortAdapter = new SortAdapter(this, R.layout.row_rearrange_item);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        addButton.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.zmdi_plus)
                .colorRes(R.color.DarkColor)
                .actionBarSize());
    }

    public void SetData()
    {
        db.Open();
        routineDto = db.SelectRoutines(programId);
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
        menu.findItem(R.id.edit).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_swap_vertical).colorRes(R.color.white).actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

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
            menu.findItem(R.id.edit).setIcon(null);
            menu.findItem(R.id.edit).setTitle("Save");
            canSwitch = false;
        }
        else
        {
            menu.findItem(R.id.edit).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_swap_vertical).colorRes(R.color.white).actionBarSize());
            menu.findItem(R.id.edit).setTitle("");
            canSwitch = true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (canSwitch)
        {
            Intent i = new Intent(this, Exercise.class);
            i.putExtra("ROUTINE_NAME", routineDto.get((int) id).Name);
            i.putExtra("ROUTINE_ID", routineDto.get((int) id).Id);

            startActivity(i);
        }
    }

    @Override
    public void OnSubmit(String action)
    {
        SetData();
    }

    @Override
    public void onClick(View v)
    {
        AddDialog addDialog = new AddDialog();
        addDialog.SetData(Routine.this, AddDialogType.Routine);
        Bundle b = new Bundle();
        b.putInt("PROGRAM_ID", programId);
        addDialog.setArguments(b);
        addDialog.show(Routine.this.getFragmentManager(), "");
    }

    private class SortAdapter extends SimpleDragSortCursorAdapter
    {
        public SortAdapter(Context ctxt, int rmid)
        {
            super(ctxt, rmid, null, new String[]{"name"}, new int[] {R.id.text}, 0);
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
            IconTextView btn;

            public RoutineHolder(View v)
            {
                routineName = (TextView) v.findViewById(R.id.show);
                btn = (IconTextView) v.findViewById(R.id.edit_btn);
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
                row = inflater.inflate(R.layout.row_program, parent, false);
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
}



