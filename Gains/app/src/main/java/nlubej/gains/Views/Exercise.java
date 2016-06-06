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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import nlubej.gains.Adapters.ExerciseAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseDialog;
import nlubej.gains.Dialogs.AddRoutineDialog;
import nlubej.gains.Dialogs.EditExerciseDialog;
import nlubej.gains.Dialogs.SearchExerciseDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.R;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.interfaces.OnItemChanged;


public class Exercise extends AppCompatActivity implements OnItemChanged<ExerciseDto>,  OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private Context context;
    private DragSortListView dragSortListView;
    private SwipeMenuListView swipeListView;
    private ExerciseAdapter exerciseAdapter;
    private SimpleDragSortCursorAdapter dragSortAdapter;
    boolean canSwitch = false;
    private int routineID = 0;
    private MatrixCursor cursor;
    private FloatingActionButton addButton;
    private Toolbar toolbar;

    FloatingActionButton addExistingButton;
    FloatingActionButton addNewButton;
    private FloatingActionMenu menuRed;

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
        dragSortListView = (DragSortListView) findViewById(R.id.dragListView);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);

        menuRed = (FloatingActionMenu) findViewById(R.id.addButton);
        addExistingButton = (FloatingActionButton) findViewById(R.id.addExistingButton);
        addNewButton = (FloatingActionButton) findViewById(R.id.addNewButton);

        addExistingButton.setImageResource(R.drawable.ic_search_white_24dp);
        addNewButton.setImageResource(R.drawable.fab_add);
        menuRed.setClosedOnTouchOutside(true);
        //addButton = (FloatingActionButton) findViewById(R.id.addButton);

        //addButton.attachToListView(swipeListView);
        //addButton.attachToListView(dragSortListView);

        swipeListView.setOnMenuItemClickListener(this);
        addExistingButton.setOnClickListener(this);
        addNewButton.setOnClickListener(this);

      /*  addButton.setOnClickListener(this);
        addButton.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.zmdi_plus)
                        .colorRes(R.color.DarkColor)
                        .actionBarSize());*/


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
        exerciseAdapter = new ExerciseAdapter(this);

        dragSortListView.setAdapter(dragSortAdapter);
        swipeListView.setAdapter(exerciseAdapter);

        swipeListView.setVisibility(View.VISIBLE);
        dragSortListView.setVisibility(View.INVISIBLE);
    }

    public void SetData()
    {
        db.Open();
        exerciseDto = db.SelectExercises(routineID);
        db.Close();

        exerciseAdapter.AddAll(exerciseDto);
        exerciseAdapter.notifyDataSetChanged();

        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "Type", "ExerciseTypeDescription"});
        if (exerciseDto != null)
        {
            for (int i = 0; i < exerciseDto.size(); i++)
            {
                cursor.addRow(new Object[] {exerciseDto.get(i).Id, exerciseDto.get(i).Name, exerciseDto.get(i).Position, exerciseDto.get(i).Type.Id, exerciseDto.get(i).Type.Description });
            }
        }

        dragSortAdapter.changeCursor(cursor);
    }

    private void UpdateCursor()
    {
        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "Type", "ExerciseTypeDescription"});
        if (exerciseAdapter.getCount() != 0)
        {
            for (int i = 0; i < exerciseAdapter.getCount(); i++)
            {
                ExerciseDto exerciseDto = exerciseAdapter.getItem(i);
                cursor.addRow(new Object[] {exerciseDto.Id, exerciseDto.Name, exerciseDto.Position, exerciseDto.Type.Id, exerciseDto.Type.Description });
            }
        }

        dragSortAdapter.changeCursor(cursor);
        swipeListView.setAdapter(exerciseAdapter);
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
                dragSortListView.setVisibility(View.VISIBLE);
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
                    db.UpdateExerciseOrder(newIds, dto, routineID);
                    db.Close();

                    exerciseAdapter.AddAll(dto);
                    exerciseAdapter.notifyDataSetChanged();
                }

                dragSortListView.setVisibility(View.INVISIBLE);
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
            menu.findItem(R.id.edit).setIcon(R.drawable.ic_done_white_24dp);
            canSwitch = false;
        }
        else
        {
            menu.findItem(R.id.edit).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_swap_vertical).colorRes(R.color.white).actionBarSize());
            canSwitch = true;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.addExistingButton:
                SearchExerciseDialog addExisting = new SearchExerciseDialog();
                addExisting.SetData(this, db, exerciseAdapter.GetAllIds());
                Bundle b = new Bundle();
                b.putInt("ROUTINE_ID", routineID);
                addExisting.setArguments(b);
                addExisting.show(getFragmentManager(), "");

                break;
            case R.id.addNewButton:

                AddExerciseDialog addDialog = new AddExerciseDialog();
                addDialog.SetData(Exercise.this);
                Bundle bu = new Bundle();
                bu.putInt("ROUTINE_ID", routineID);
                addDialog.setArguments(bu);
                addDialog.show(Exercise.this. getFragmentManager(), "");


                break;
        }

        menuRed.close(false);
    }

    @Override
    public void OnAdded(ExerciseDto row)
    {
        exerciseAdapter.Add(row);
        exerciseAdapter.notifyDataSetChanged();
        UpdateCursor();

        SetResult();
    }

    @Override
    public void OnUpdated(ExerciseDto row)
    {
        exerciseAdapter.Update(row);
        exerciseAdapter.notifyDataSetChanged();

        UpdateCursor();
    }

    @Override
    public void OnRemoved(ExerciseDto row)
    {
    }

    private void SetResult()
    {
        Intent intent = new Intent();
        intent.putExtra("ROUTINE_ID", routineID);
        intent.putExtra("EXERCISE_COUNT", exerciseAdapter.getCount());
        setResult(Program.RESULT_OK, intent);
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
                b.putInt("EXERCISE_ID", item.Id);
                b.putString("EXERCISE_NAME", item.Name);
                b.putInt("EXERCISE_TYPE", item.Type.Id);
                editExercise.setArguments(b);

                editExercise.show(Exercise.this.getFragmentManager(), "");
                break;
            case 1: //delete

                db.Open();
                db.DeleteExercise(String.valueOf(item.Id), String.valueOf(routineID));
                db.Close();

                exerciseAdapter.Remove(item);
                exerciseAdapter.notifyDataSetChanged();
                UpdateCursor();

                SetResult();

                if(exerciseAdapter.getCount() > 0)
                {
                    swipeListView.setSelection(position - 1);
                }

                //TODO DELTE FROM WOKROUT NOTE PRODUCED ERROR
                break;
        }
        return false;
    }
}