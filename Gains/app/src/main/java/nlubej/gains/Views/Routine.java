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
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.Adapters.RoutineAdapter;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddRoutineDialog;
import nlubej.gains.Dialogs.EditExerciseDialog;
import nlubej.gains.ExternalFiles.DragSortListView;
import nlubej.gains.R;
import nlubej.gains.ExternalFiles.SimpleDragSortCursorAdapter;
import nlubej.gains.interfaces.OnItemChanged;

public class Routine extends AppCompatActivity implements OnItemClickListener, OnItemChanged<RoutineDto>, OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private QueryFactory db;
    private ArrayList<RoutineDto> routineDto;
    private Context context;
    private DragSortListView dslv;
    private SwipeMenuListView swipeListView;
    private SimpleDragSortCursorAdapter dragSortAdapter;
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
        dslv = (DragSortListView) findViewById(R.id.dragListView);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        dslv.setOnItemClickListener(this);
        swipeListView.setOnMenuItemClickListener(this);
        swipeListView.setOnItemClickListener(this);
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

        //dragSortAdapter = new SimpleDragSortCursorAdapter(getApplicationContext(), R.layout.row_rearrange_item, null, new String[]{"Name", "ExerciseCountDescription"}, new int[] {R.id.name, R.id.subName}, 0);
        dragSortAdapter = new SortAdapter(getApplicationContext(), R.layout.row_rearrange_item);
        routineAdapter = new RoutineAdapter(this, db);

        dslv.setAdapter(dragSortAdapter);
        swipeListView.setAdapter(routineAdapter);

        swipeListView.setVisibility(View.VISIBLE);
        dslv.setVisibility(View.INVISIBLE);
    }

    public void SetData()
    {
        db.Open();
        routineDto = db.SelectRoutines(programId);
        db.Close();

        routineAdapter.AddAll(routineDto);
        routineAdapter.notifyDataSetChanged();

        cursor = new MatrixCursor(new String[]{"_id", "Name", "Position", "ProgramId", "ExerciseCount", "ExerciseCountDescription"});
        if (routineDto != null)
        {
            for (int i = 0; i < routineDto.size(); i++)
            {
                cursor.addRow(new Object[] {routineDto.get(i).Id, routineDto.get(i).Name,routineDto.get(i).Position, routineDto.get(i).ProgramId, routineDto.get(i).ExerciseCount, String.format("%d exercises",routineDto.get(i).ExerciseCount)});

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (canSwitch)
        {
            Intent i = new Intent(this, Exercise.class);
            i.putExtra("ROUTINE_NAME", routineAdapter.getItem((int) id).Name);
            i.putExtra("ROUTINE_ID", routineAdapter.getItem((int) id).Id);
            startActivity(i);
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
        addDialog.show(Routine.this.getFragmentManager(), "");
    }

    @Override
    public void OnAdded(RoutineDto row)
    {
        routineAdapter.Add(row);
        routineAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUpdated(RoutineDto row)
    {
        routineAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        RoutineDto item = routineAdapter.getItem(position);
        switch (index) {
            case 0: //edit
          //      EditExerciseDialog addDialog = new EditExerciseDialog();
               // addDialog.SetData(Program.this, db);
              //  addDialog.show(Program.this.getActivity().getFragmentManager(), "");
                break;
            case 1: //delete
                routineAdapter.Remove(item);
                break;
        }

        swipeListView.invalidate();
        routineAdapter.notifyDataSetChanged();
        swipeListView.setAdapter(routineAdapter);

        return false;
    }

    private class SortAdapter extends SimpleDragSortCursorAdapter
    {
        public SortAdapter(Context ctxt, int rmid)
        {
            super(ctxt, rmid, null, new String[]{"Name", "ExerciseCountDescription"}, new int[] {R.id.name, R.id.subName}, 0);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            return v;
        }
    }
}



