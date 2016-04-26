package nlubej.gains.Views;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.malinskiy.materialicons.widget.IconTextView;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseDialog;
import nlubej.gains.Dialogs.EditExerciseDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.R;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.interfaces.onActionSubmit;


public class Exercise extends AppCompatActivity implements onActionSubmit, OnClickListener
{
    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private Context context;
    private DragSortListView dslv;
    private SimpleDragSortCursorAdapter sortAdapter;
    private ExerciseAdapter exerciseAdapter;
    boolean canSwitch = false;
    private int routineID = 0;
    private MatrixCursor cursor;
    private FloatingActionButton addButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setLogo(null);

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("ROUTINE_NAME");
        routineID = intent.getIntExtra("ROUTINE_ID", 1);

        setTitle(routineName);
        InitComponents();
        SetData();

        dslv = (DragSortListView) findViewById(R.id.list);
    }

    private void InitComponents()
    {
        context = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QueryFactory(context);
        dslv = (DragSortListView) findViewById(R.id.list);
        sortAdapter = new SortAdapter(this, R.layout.row_rearrange_item_exercise);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        addButton.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.DarkColor).actionBarSize());

    }

    public void SetData()
    {
        db.Open();
        exerciseDto = db.SelectExercises(routineID);
        db.Close();

        exerciseAdapter = new ExerciseAdapter(this, exerciseDto);
        dslv.setAdapter(exerciseAdapter);

        cursor = new MatrixCursor(new String[]{"_id", "name"});
        if (exerciseDto != null)
        {
            for (int i = 0; i < exerciseDto.size(); i++)
            {
                cursor.addRow(new Object[] {i, exerciseDto.get(i).Name});
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
                        newIds[i] = ((int)sortAdapter.getItemId(i)) + 1;
                        cursor.moveToNext();
                    }

                    db.Open();
                    db.UpdateExerciseOrder(newIds, exerciseDto);
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
    public void OnSubmit(String action)
    {
        SetData();
    }

    @Override
    public void onClick(View v)
    {
        AddExerciseDialog addDialog = new AddExerciseDialog();
        addDialog.SetData(Exercise.this);
        Bundle b = new Bundle();
        b.putInt("ROUTINE_ID", routineID);
        addDialog.setArguments(b);
        addDialog.show(Exercise.this. getFragmentManager(), "");
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


    class ExerciseRow
    {
        String exerciseName;

        public ExerciseRow(String exerciseName)
        {
            this.exerciseName = exerciseName;
        }
    }


    class ExerciseAdapter extends BaseAdapter

    {
        ArrayList<ExerciseRow> exerciseRows;
        Context c;

        public ExerciseAdapter(Context c, ArrayList<ExerciseDto> routineDto)
        {
            this.c = c;
            exerciseRows = new ArrayList<>();

            if (routineDto != null)
            {
                for (ExerciseDto dto : routineDto)
                {
                    exerciseRows.add(new ExerciseRow(dto.Name));
                }
            }
        }


        @Override
        public int getCount()
        {
            return exerciseRows.size();
        }

        @Override
        public Object getItem(int position)
        {
            return exerciseRows.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        class ExerciseHolder
        {
            TextView exerciseName;
            IconTextView btn;

            public ExerciseHolder(View v)
            {
                exerciseName = (TextView) v.findViewById(R.id.show);
                btn = (IconTextView) v.findViewById(R.id.edit_btn);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            ExerciseHolder ExerciseHolder = null;

            if (row == null)
            {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_exercise, parent, false);
                ExerciseHolder = new ExerciseHolder(row);
                row.setTag(ExerciseHolder);

            }
            else
            {
                ExerciseHolder = (ExerciseHolder) row.getTag();
            }


            ExerciseRow temp = exerciseRows.get(position);

            ExerciseHolder.exerciseName.setText(temp.exerciseName);
            ExerciseHolder.btn.setTag(position);

            ExerciseHolder.btn.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(final View v)
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
                                        db.DeleteExercise(exerciseDto.get(position).Id);
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
                                EditExerciseDialog editDialog = new EditExerciseDialog();
                                Bundle arg = new Bundle();
                                arg.putString("EXERCISE_NAME", exerciseDto.get(position).Name);
                                arg.putInt("EXERCISE_ID", exerciseDto.get(position).Id);
                                editDialog.setArguments(arg);
                                editDialog.SetData(Exercise.this);
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



