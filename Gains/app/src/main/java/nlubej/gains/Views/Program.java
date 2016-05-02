package nlubej.gains.Views;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.Adapters.ProgramAdapter;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddProgramDialog;
import nlubej.gains.R;
import nlubej.gains.interfaces.*;

public class Program extends Fragment implements OnItemClickListener, OnItemChanged<ProgramDto>, OnClickListener
{
    private Context context;
    private QueryFactory db;
    private ArrayList<ProgramDto> programDto;
    FloatingActionButton addButton;
    ProgramAdapter programAdapter;
    SwipeMenuListView swipeListView;

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
        swipeListView = (SwipeMenuListView) fragment.findViewById(R.id.swipeListView);
        addButton = (FloatingActionButton)fragment.findViewById(R.id.addButton);
        programAdapter = new ProgramAdapter(this, db);
        db = new QueryFactory(context);

        addButton.setOnClickListener(this);
        swipeListView.setOnItemClickListener(this);
        addButton.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.zmdi_plus)
                        .colorRes(R.color.DarkColor)
                        .actionBarSize());

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
        programDto = db.SelectPrograms();
        db.Close();

        programAdapter.AddAll(programDto);
        programAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(context, Routine.class);
        i.putExtra("PROGRAM_NAME", programDto.get((int) id).Name);
        i.putExtra("PROGRAM_ID", programDto.get((int) id).Id);

        startActivity(i);
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
        programAdapter.notifyDataSetChanged();
    }
}