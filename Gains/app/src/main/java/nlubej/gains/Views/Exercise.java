package nlubej.gains.Views;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.Adapters.ExerciseAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseDialog;
import nlubej.gains.Dialogs.EditExerciseDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.R;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.interfaces.OnItemChanged;


public class Exercise extends AppCompatActivity implements OnItemChanged<ExerciseDto>,  OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private Context context;
    private DragSortListView dslv;
    private SwipeMenuListView swipeListView;
    private ExerciseAdapter exerciseAdapter;
    private SimpleDragSortCursorAdapter dragSortAdapter;
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

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("ROUTINE_NAME");
        routineID = intent.getIntExtra("ROUTINE_ID", 1);

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
        dslv = (DragSortListView) findViewById(R.id.dragListView);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        swipeListView.setOnMenuItemClickListener(this);
        addButton.setOnClickListener(this);
        addButton.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.zmdi_plus)
                        .colorRes(R.color.DarkColor)
                        .actionBarSize());


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem editProgram = new SwipeMenuItem(getApplicationContext());
                editProgram.setWidth(100);
                editProgram.setIcon(new IconDrawable(getApplicationContext(), Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.gray).actionBarSize());
                menu.addMenuItem(editProgram);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setWidth(90);
                deleteItem.setIcon(new IconDrawable(getApplicationContext(), Iconify.IconValue.zmdi_delete).colorRes(R.color.red).actionBarSize());
                menu.addMenuItem(deleteItem);
            }
        };

        swipeListView.setMenuCreator(creator);
        dragSortAdapter = new SimpleDragSortCursorAdapter(getApplicationContext(), R.layout.row_rearrange_item, null, new String[]{"Name", "ExerciseTypeDescription"}, new int[] {R.id.name, R.id.subName}, 0);
        exerciseAdapter = new ExerciseAdapter(this, db);

        dslv.setAdapter(dragSortAdapter);
        swipeListView.setAdapter(exerciseAdapter);

        swipeListView.setVisibility(View.VISIBLE);
        dslv.setVisibility(View.INVISIBLE);
    }

    public void SetData()
    {
        db.Open();
        exerciseDto = db.SelectExercises(routineID);
        db.Close();

        exerciseAdapter.AddAll(exerciseDto);
        exerciseAdapter.notifyDataSetChanged();

        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "Type", "RoutineId", "ExerciseTypeDescription"});
        if (exerciseDto != null)
        {
            for (int i = 0; i < exerciseDto.size(); i++)
            {
                cursor.addRow(new Object[] {exerciseDto.get(i).Id, exerciseDto.get(i).Name, exerciseDto.get(i).Position, exerciseDto.get(i).Type.Id, exerciseDto.get(i).RoutineId, exerciseDto.get(i).Type.Description });
            }
        }

        dragSortAdapter.changeCursor(cursor);
    }

    private void UpdateCursor()
    {
        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "Type", "RoutineId", "ExerciseTypeDescription"});
        if (exerciseAdapter.getCount() != 0)
        {
            for (int i = 0; i < exerciseAdapter.getCount(); i++)
            {
                ExerciseDto exerciseDto = exerciseAdapter.getItem(i);
                cursor.addRow(new Object[] {exerciseDto.Id, exerciseDto.Name, exerciseDto.Position, exerciseDto.Type.Id, exerciseDto.RoutineId, exerciseDto.Type.Description });
            }
        }

        dragSortAdapter.changeCursor(cursor);
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
                dslv.setVisibility(View.VISIBLE);
                swipeListView.setVisibility(View.INVISIBLE);
            }
            else
            {
                if (dragSortAdapter.getCount() != 0)
                {
                    ArrayList<ExerciseDto> dto = new ArrayList<>();
                    cursor.moveToFirst();
                    int[] newIds = new int[cursor.getCount()];
                    for (int i = 0; i < dragSortAdapter.getCount(); i++)
                    {
                        newIds[i] = ((int) dragSortAdapter.getItemId(i));
                        dto.add(ExerciseDto.ToDto(cursor));
                        cursor.moveToNext();
                    }

                    db.Open();
                    db.UpdateExerciseOrder(newIds, dto);
                    db.Close();

                    exerciseAdapter.AddAll(dto);
                    exerciseAdapter.notifyDataSetChanged();
                }

                dslv.setVisibility(View.INVISIBLE);
                swipeListView.setVisibility(View.VISIBLE);
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
    public void onClick(View v)
    {
        AddExerciseDialog addDialog = new AddExerciseDialog();
        addDialog.SetData(Exercise.this);
        Bundle b = new Bundle();
        b.putInt("ROUTINE_ID", routineID);
        addDialog.setArguments(b);
        addDialog.show(Exercise.this. getFragmentManager(), "");
    }

    @Override
    public void OnAdded(ExerciseDto row)
    {
        exerciseAdapter.Add(row);
        exerciseAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUpdated(ExerciseDto row)
    {
        exerciseAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        ExerciseDto item = exerciseAdapter.getItem(position);
        switch (index) {
            case 0: //edit
                EditExerciseDialog editExercise = new EditExerciseDialog();
                editExercise.SetData(Exercise.this, db);
                Bundle b = new Bundle();
                b.putInt("EXERCISE_ID", routineID);
                b.putInt("EXERCISE_NAME", routineID);
                b.putInt("EXERCISE_TYPE", routineID);
                editExercise.setArguments(b);

                editExercise.show(Exercise.this.getFragmentManager(), "");
                break;
            case 1: //delete

                db.Open();
                db.DeleteExercise(item.Id);
                db.Close();

                exerciseAdapter.Remove(item);
                dragSortAdapter.remove(position);

                exerciseAdapter.notifyDataSetChanged();
                dragSortAdapter.notifyDataSetChanged();
                UpdateCursor();
                swipeListView.setAdapter(exerciseAdapter);
                break;
        }
        return false;
    }
}