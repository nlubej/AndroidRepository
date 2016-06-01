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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import nlubej.gains.Adapters.RoutineAdapter;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddRoutineDialog;
import nlubej.gains.Dialogs.EditRoutineDialog;
import nlubej.gains.Dialogs.SearchExerciseDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.R;
import nlubej.gains.interfaces.OnItemChanged;

public class Routine extends AppCompatActivity implements OnItemClickListener, OnItemChanged<RoutineDto>, OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private QueryFactory db;
    private DragSortListView dragSortListView;
    private SwipeMenuListView swipeListView;
    private SimpleDragSortCursorAdapter dragSortAdapter;
    private RoutineAdapter routineAdapter;
    private MatrixCursor cursor;
    private boolean canSwitch = false;
    private int programId = 0;

    FloatingActionButton addExistingButton;
    FloatingActionButton addNewButton;
    private FloatingActionMenu menuRed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_routine_exercise);

        Intent intent = getIntent();
        String routineName = intent.getStringExtra("PROGRAM_NAME");
        programId = intent.getIntExtra("PROGRAM_ID", 1);

        setTitle(routineName);
        InitComponents();
        SetData();
    }

    private void InitComponents()
    {
        Context context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QueryFactory(context);
        dragSortListView = (DragSortListView) findViewById(R.id.dragListView);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);

        menuRed = (FloatingActionMenu) findViewById(R.id.addButton);
        addExistingButton = (FloatingActionButton) findViewById(R.id.addExistingButton);
        addNewButton = (FloatingActionButton) findViewById(R.id.addNewButton);

        menuRed.setClosedOnTouchOutside(true);
        addExistingButton.setVisibility(View.GONE);
        addNewButton.setVisibility(View.GONE);

        dragSortListView.setOnItemClickListener(this);
        swipeListView.setOnMenuItemClickListener(this);
        swipeListView.setOnItemClickListener(this);
        menuRed.setOnMenuButtonClickListener(this);

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

        dragSortAdapter = new SortAdapter(getApplicationContext(), R.layout.row_rearrange_item);
        routineAdapter = new RoutineAdapter(this, db);

        dragSortListView.setAdapter(dragSortAdapter);
        swipeListView.setAdapter(routineAdapter);

        swipeListView.setVisibility(View.VISIBLE);
        dragSortListView.setVisibility(View.INVISIBLE);
    }

    public void SetData()
    {
        db.Open();
        ArrayList<RoutineDto> routineDto = db.SelectRoutines(programId);
        db.Close();

        routineAdapter.AddAll(routineDto);
        routineAdapter.notifyDataSetChanged();

        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "ProgramId", "ExerciseCount", "ExerciseCountDescription"});
        if (routineDto != null)
        {
            for (int i = 0; i < routineDto.size(); i++)
            {
                cursor.addRow(new Object[] {routineDto.get(i).Id, routineDto.get(i).Name, routineDto.get(i).Position, routineDto.get(i).ProgramId, routineDto.get(i).ExerciseCount, String.format("%d %s", routineDto.get(i).ExerciseCount, routineDto.get(i).ExerciseCount == 1 ? "exercise" : "exercises")});

            }
        }

        dragSortAdapter.changeCursor(cursor);
    }

    private void UpdateCursor()
    {
        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "ProgramId", "ExerciseCount", "ExerciseCountDescription"});
        if (routineAdapter.getCount() != 0)
        {
            for (int i = 0; i < routineAdapter.getCount(); i++)
            {
                RoutineDto routineDto = routineAdapter.getItem(i);
                cursor.addRow(new Object[] {routineDto.Id, routineDto.Name,routineDto.Position, routineDto.ProgramId, routineDto.ExerciseCount, String.format("%d exercises",routineDto.ExerciseCount)});
            }
        }

        dragSortAdapter.changeCursor(cursor);
        swipeListView.setAdapter(routineAdapter);
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
                    ArrayList<RoutineDto> dto = new ArrayList<>();
                    cursor.moveToFirst();
                    int[] newIds = new int[cursor.getCount()];
                    for (int i = 0; i < dragSortAdapter.getCount(); i++)
                    {
                        newIds[i] = ((int) dragSortAdapter.getItemId(i));
                        dto.add(RoutineDto.ToDto(cursor));
                        cursor.moveToNext();
                    }

                    db.Open();
                    db.UpdateRoutineOrder(newIds, dto);
                    db.Close();

                    routineAdapter.AddAll(dto);
                    routineAdapter.notifyDataSetChanged();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (canSwitch)
        {
            Intent i = new Intent(this, Exercise.class);
            i.putExtra("ROUTINE_NAME", routineAdapter.getItem((int) id).Name);
            i.putExtra("ROUTINE_ID", routineAdapter.getItem((int) id).Id);
            startActivityForResult(i, Program.UPDATE_ACTIVITY_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Program.UPDATE_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == Program.RESULT_OK) {

                int id = data.getIntExtra("ROUTINE_ID",0);
                int count = data.getIntExtra("EXERCISE_COUNT",0);

                routineAdapter.UpdateExerciseCount(id, count);
                routineAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v)
    {

        AddRoutineDialog addDialog = new AddRoutineDialog();
        addDialog.SetData(Routine.this, db);

        Bundle b = new Bundle();
        b.putInt("PROGRAM_ID", programId);
        addDialog.setArguments(b);
        addDialog.show(getFragmentManager(), "");

        menuRed.close(false);
    }

    @Override
    public void OnAdded(RoutineDto row)
    {
        routineAdapter.Add(row);
        routineAdapter.notifyDataSetChanged();

        UpdateCursor();
        SetResult();
    }

    @Override
    public void OnUpdated(RoutineDto row)
    {
        routineAdapter.Update(row);
        routineAdapter.notifyDataSetChanged();
        UpdateCursor();
    }

    @Override
    public void OnRemoved(RoutineDto row)
    {
    }

    private void SetResult()
    {
        Intent intent = new Intent();
        intent.putExtra("PROGRAM_ID", programId);
        intent.putExtra("ROUTINE_COUNT", routineAdapter.getCount());
        setResult(Program.RESULT_OK, intent);
    }


    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        RoutineDto item = routineAdapter.getItem(position);
        switch (index) {
            case 0: //edit
                EditRoutineDialog editExercise = new EditRoutineDialog();
                editExercise.SetData(Routine.this, db);
                Bundle b = new Bundle();
                b.putInt("ROUTINE_ID", item.Id);
                b.putInt("PROGRAM_ID", item.ProgramId);
                b.putString("ROUTINE_NAME", item.Name);
                editExercise.setArguments(b);

                editExercise.show(getFragmentManager(), "");
                break;
            case 1: //delete
                db.Open();
                db.DeleteRoutine(item.Id);
                db.Close();

                routineAdapter.Remove(item);
                routineAdapter.notifyDataSetChanged();
                UpdateCursor();

                SetResult();

                if(routineAdapter.getCount() > 0)
                {
                    swipeListView.setSelection(routineAdapter.getCount() - 1);
                }
                break;
        }

        return false;
    }

    private class SortAdapter extends SimpleDragSortCursorAdapter
    {
        public SortAdapter(Context ctx, int rid)
        {
            super(ctx, rid, null, new String[]{"Name", "ExerciseCountDescription"}, new int[] {R.id.name, R.id.subName}, 0);
            mContext = ctx;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return super.getView(position, convertView, parent);
        }
    }
}



