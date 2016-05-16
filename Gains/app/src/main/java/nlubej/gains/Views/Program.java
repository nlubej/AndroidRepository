package nlubej.gains.Views;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.clans.fab.FloatingActionMenu;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.Adapters.ProgramAdapter;
import nlubej.gains.Adapters.RoutineAdapter;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddProgramDialog;
import nlubej.gains.Dialogs.EditProgramDialog;
import nlubej.gains.Dialogs.EditRoutineDialog;
import nlubej.gains.R;
import nlubej.gains.interfaces.*;

public class Program extends Fragment implements OnItemClickListener, OnItemChanged<ProgramDto>, OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private Context context;
    private QueryFactory db;
    ProgramAdapter programAdapter;
    SwipeMenuListView swipeListView;
    private SharedPreferences prefs;

    static final int UPDATE_ACTIVITY_RESULT = 1;
    static final int RESULT_OK = 1;
    private FloatingActionMenu menuRed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragment = inflater.inflate(R.layout.view_program, container, false);

        InitComponents(fragment);
        SetData();

        this.setHasOptionsMenu(true);

        return fragment;
    }

    private void InitComponents(final View fragment)
    {
        context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        swipeListView = (SwipeMenuListView) fragment.findViewById(R.id.swipeListView);
        menuRed = (FloatingActionMenu) fragment.findViewById(R.id.addButton);
        programAdapter = new ProgramAdapter(this, db);
        db = new QueryFactory(context);


        menuRed.setClosedOnTouchOutside(true);
        menuRed.setOnMenuButtonClickListener(this);

        swipeListView.setOnItemClickListener(this);
        swipeListView.setOnMenuItemClickListener(this);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem defaultProgram = new SwipeMenuItem(fragment.getContext());
                defaultProgram.setWidth(100);
                defaultProgram.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_star).actionBarSize().colorRes(R.color.PrimaryColor).actionBarSize());
                menu.addMenuItem(defaultProgram);

                SwipeMenuItem editProgram = new SwipeMenuItem(fragment.getContext());
                editProgram.setWidth(100);
                editProgram.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.gray).actionBarSize());
                menu.addMenuItem(editProgram);

                SwipeMenuItem deleteItem = new SwipeMenuItem(fragment.getContext());
                deleteItem.setWidth(100);
                deleteItem.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_delete).actionBarSize().colorRes(R.color.red).actionBarSize());
                menu.addMenuItem(deleteItem);
            }
        };

        swipeListView.setAdapter(programAdapter);
        swipeListView.setMenuCreator(creator);
    }

    public void SetData()
    {
        db.Open();
        ArrayList<ProgramDto> programDto = db.SelectPrograms();
        db.Close();

        programAdapter.AddAll(programDto);
        programAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(context, Routine.class);
        i.putExtra("PROGRAM_NAME", programAdapter.getItem((int) id).Name);
        i.putExtra("PROGRAM_ID", programAdapter.getItem((int) id).Id);

        startActivityForResult(i, UPDATE_ACTIVITY_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATE_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                int id = data.getIntExtra("PROGRAM_ID",0);
                int count = data.getIntExtra("ROUTINE_COUNT",0);

                programAdapter.UpdateRoutineCount(id, count);
                programAdapter.notifyDataSetChanged();
            }
        }
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_program, menu);
    }
*/
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
        }
        return true;
    }*/


    @Override
    public void onClick(View v)
    {
        AddProgramDialog addDialog = new AddProgramDialog();
        addDialog.SetData(Program.this, db);
        addDialog.show(Program.this.getActivity().getFragmentManager(), "");

        menuRed.close(false);
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        ProgramDto item = programAdapter.getItem(position);
        switch (index) {
            case 0: //mark
                ProgramAdapter.DefaultProgram = item.Id;
                prefs.edit().putInt("DEFAULT_PROGRAM", item.Id).apply();
                programAdapter.notifyDataSetChanged();
                swipeListView.setAdapter(programAdapter);
                break;
            case 1: //edit
                EditProgramDialog editExercise = new EditProgramDialog();
                editExercise.SetData(Program.this, db);
                Bundle b = new Bundle();
                b.putInt("PROGRAM_ID", item.Id);
                b.putString("PROGRAM_NAME", item.Name);
                editExercise.setArguments(b);

                editExercise.show(getFragmentManager(), "");
                break;
            case 2: //delete
                db.Open();
                db.DeleteProgram(item.Id);
                db.Close();

                programAdapter.Remove(item);
                programAdapter.notifyDataSetChanged();
                swipeListView.setAdapter(programAdapter);

                break;
        }

        return false;
    }

    @Override
    public void OnAdded(ProgramDto row)
    {
        programAdapter.Add(row);
        programAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUpdated(ProgramDto row)
    {
        programAdapter.Update(row);
        programAdapter.notifyDataSetChanged();
    }
}