package nlubej.gains;


import java.util.ArrayList;
import java.util.Set;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseDialog;
import nlubej.gains.Dialogs.EditDialog;
import nlubej.gains.Dialogs.EditExerciseDialog;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.interfaces.onActionSubmit;


public class Exercise extends Activity implements onActionSubmit
{

    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private Context context;
    private DragSortListView dslv;
    private SimpleDragSortCursorAdapter sortAdapter;
    private ExerciseAdapter routineAdapter;

    private SimpleDragSortCursorAdapter adapter;
    private ExerciseAdapter adapter2;
    boolean canSwitch = false;
    DBAdapter dbHelper;
    long routineID = 0;
    long programID = 0;
    String[] names = null;
    long[] idss = null;
    String[] type = null;
    MatrixCursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(null);

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("ROUTINE_NAME");
        setTitle(routineName);
        routineID = intent.getLongExtra("ROUTINE_ID", 1);

        InitComponents();
        SetData();

        dslv = (DragSortListView) findViewById(R.id.list);
    }

    private void InitComponents()
    {
        context = getApplicationContext();
        db = new QueryFactory(context);
        dslv = (DragSortListView) findViewById(R.id.list);
        sortAdapter = new SortAdapter(this, R.layout.exercise_list_item_rearrange_row);
    }

    public void SetData()
    {
        db.Open();
        exerciseDto = db.SelectExercises();
        db.Close();

        routineAdapter = new ExerciseAdapter(this, exerciseDto);
        dslv.setAdapter(routineAdapter);

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
                AddExerciseDialog dialog = new AddExerciseDialog();
                Bundle arg = new Bundle();
                arg.putLong("ROUTINE_ID", routineID);
                dialog.setArguments(arg);
                dialog.show(getFragmentManager(), "");
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
                dslv.setAdapter(adapter);
            }
            else
            {
                if (adapter.getCount() != 0)
                {
                    cursor.moveToFirst();
                    int[] newIds = new int[cursor.getCount()];
                    for (int i = 0; i < adapter.getCount(); i++)
                    {
                        newIds[i] = ((int)adapter.getItemId(i)) + 1;
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
            menu.getItem(1).setIcon(null);
            menu.getItem(1).setTitle("Save");
            canSwitch = false;
        }
        else
        {
            menu.getItem(1).setIcon(R.drawable.white_arrow);
            menu.getItem(1).setTitle("");
            canSwitch = true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void OnSubmit(String action)
    {
        SetData();
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
            ImageView btn;

            public ExerciseHolder(View v)
            {
                exerciseName = (TextView) v.findViewById(R.id.show);
                btn = (ImageView) v.findViewById(R.id.edit_btn);
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
                row = inflater.inflate(R.layout.row_routine, parent, false);
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



