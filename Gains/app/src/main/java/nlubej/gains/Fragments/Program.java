package nlubej.gains.Fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddDialog;
import nlubej.gains.Dialogs.DeleteDialog;
import nlubej.gains.Dialogs.EditDialog;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.R;
import nlubej.gains.interfaces.*;

public class Program extends Fragment implements OnItemClickListener, onActionSubmit
{
    private View fragment;
    private Context context;
    private ListView list;
    private QueryFactory db;
    private SharedPreferences prefs;
    private ArrayList<ProgramDto> programDto;
    private static long defaultProgram = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        fragment = inflater.inflate(R.layout.view_program, container, false);

        InitComponents(fragment);
        SetData();

        list.setOnItemClickListener(this);
        this.setHasOptionsMenu(true);

        return fragment;
    }

    private void InitComponents(View fragment)
    {
        context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        list = (ListView) fragment.findViewById(R.id.listView);
        db = new QueryFactory(context);
    }

    public void SetData()
    {
        db.Open();
        programDto = db.SelectPrograms();
        db.Close();

        list.setAdapter(new ProgramAdapter(context, programDto));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(context, nlubej.gains.Routine.class);
        i.putExtra("PROGRAM_NAME", programDto.get((int) id).Name);
        i.putExtra("PROGRAM_ID", programDto.get((int) id).Id);

        startActivity(i);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_program, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
        }
        return true;
    }

    @Override
    public void OnSubmit(String action)
    {
        SetData();
    }

    class ProgramRow
    {
        private String programName;

        public ProgramRow(String programName)
        {
            this.programName = programName;
        }
    }

    class ProgramAdapter extends BaseAdapter
    {
        private ArrayList<ProgramRow> programRows;
        Context context;

        public ProgramAdapter(Context context, ArrayList<ProgramDto> programDto)
        {
            this.context = context;
            programRows = new ArrayList();

            if (programDto != null)
            {
                for (ProgramDto dto : programDto)
                {
                    programRows.add(new ProgramRow(dto.Name));
                }
            }
        }


        @Override
        public int getCount()
        {
            return programRows.size();
        }

        public int GetIndex(String id)
        {
            for (int i = 0; i < programDto.size(); i++)
            {
                if (id.compareTo(programDto.get(i).Id + "") == 0)
                {
                    return i;
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position)
        {
            return programRows.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        class ProgramViewHolder
        {
            private TextView title;
            private ImageView btn;
            private ImageView defaultProgram;

            public ProgramViewHolder(View v)
            {
                title = (TextView) v.findViewById(R.id.show);
                btn = (ImageView) v.findViewById(R.id.edit_btn);
                //	defaultProgram = (ImageView) v.findViewById(R.id.defaultProgram);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            ProgramViewHolder holder = null;

            if (row == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.row_program, parent, false);
                holder = new ProgramViewHolder(row);
                row.setTag(holder);
                try
                {
                    if (defaultProgram == programDto.get(position).Id)
                    {
                        holder.title.setTextColor(getResources().getColor(R.color.menu));
                    }
                    else if ((programDto.get(position).Id) == Integer.parseInt(prefs.getString("DEFAULT_PROGRAM", "")))
                    {
                        holder.title.setTextColor(getResources().getColor(R.color.menu));
                    }
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    holder.title.setTextColor(ContextCompat.getColor(context, R.color.menu));
                    prefs.edit().putString("DEFAULT_PROGRAM", programDto.get(0).Id + "").apply();
                }

            }
            else
            {
                holder = (ProgramViewHolder) row.getTag();
            }


            final ProgramRow temp = programRows.get(position);

            holder.title.setText(temp.programName);

            holder.btn.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.popup_program, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            if (item.getItemId() == R.id.delete)
                            {
                                DeleteDialog deleteDialog = new DeleteDialog();
                                deleteDialog.SetData(Program.this, AddDialogType.Program);
                                deleteDialog.show(Program.this.getActivity().getFragmentManager(), null);
                            }
                            else if (item.getItemId() == R.id.edit)
                            {
                                EditDialog editDialog = new EditDialog();
                                editDialog.SetData(Program.this, AddDialogType.Program);
                                editDialog.show(Program.this.getActivity().getFragmentManager(), null);

                                AddDialog addDialog = new AddDialog();
                                addDialog.SetData(Program.this, AddDialogType.Program);

                                // AddProgramDialog addProgram = new AddProgramDialog();
                                // addProgram.SetData(Program.this, 0);

                              /*  Bundle arg = new Bundle();
                                arg.putString("add", "Update Program");
                                arg.putLong("programId", programDto.get(position).Id);
                                arg.putString("programName", programDto.get(position).Name);
                                arg.putString("mode", "Edit");
                                Log.d("nlubej", "programName: " + programDto.get(position).Name);
                                addProgram.setArguments(arg);*/
                                addDialog.show(Program.this.getActivity().getFragmentManager(), "updateProgram");

                            }
                            else if (item.getItemId() == R.id.setDefault)
                            {
                                defaultProgram = programDto.get(position).Id;
                                prefs.edit().putString("defaultProgram", programDto.get(position).Id + "").apply();
                                SetData();
                            }
                            return false;
                        }
                    });
                }
            });

            return row;
        }

        private void ChangeDefaultProgram(int position)
        {
            if (programDto.size() == 1)
            {
                prefs.edit().putString("DEFAULT_PROGRAM", null).apply();
            }
            else if (position + 1 == Integer.parseInt(prefs.getString("DEFAULT_PROGRAM", "")) && programDto.size() > 1)
            {
                prefs.edit().putString("DEFAULT_PROGRAM", programDto.get(0).Id + "").apply();
            }
            else if (programDto.size() == 2)
            {
                prefs.edit().putString("DEFAULT_PROGRAM", programDto.get(1).Id + "").apply();
            }
            else if (position + 1 < Integer.parseInt(prefs.getString("DEFAULT_PROGRAM", "")))
            {
                int prefIndeks = GetIndex(prefs.getString("DEFAULT_PROGRAM", ""));
                prefs.edit().putString("DEFAULT_PROGRAM", programDto.get(prefIndeks - 1).Id + "").apply();
            }
        }
    }
}