package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.Program;

/**
 * Created by nlubej on 28.4.2016.
 */
public class ProgramAdapter extends BaseAdapter
{
    private final QueryFactory db;
    private ArrayList<ProgramDto> programDto;
    Context ctx;
    public static long DefaultProgram = -1; //used in other classes

    private SharedPreferences prefs;

    public ProgramAdapter(Program parent, QueryFactory database)
    {
        this.ctx = parent.getActivity().getApplicationContext();
        programDto = new ArrayList<>();
        db = database;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public int getCount()
    {
        return programDto.size();
    }


    public void AddAll(ArrayList<ProgramDto> programDto)
    {
        this.programDto.clear();
        this.programDto.addAll(programDto);
    }

    public void Add(ProgramDto row)
    {
        row.RoutineCount = 0;
        programDto.add(row);
    }


    public void Remove(ProgramDto item)
    {
        programDto.remove(item);
    }

    @Override
    public ProgramDto getItem(int position)
    {
        return programDto.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void UpdateRoutineCount(int programId, int routineCount)
    {
        for(ProgramDto dto : programDto)
        {
            if(dto.Id == programId)
            {
                dto.RoutineCount = routineCount;
                return;
            }
        }
    }

    public void Update(ProgramDto row)
    {
        for(ProgramDto dto : programDto)
        {
            if(dto.Id == row.Id)
            {
                dto.Name = row.Name;
                return;
            }
        }
    }

    class ProgramViewHolder
    {
        private TextView name;
        private TextView subName;

        public ProgramViewHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.name);
            subName = (TextView) v.findViewById(R.id.subName);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        ProgramViewHolder holder;

        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_program, parent, false);
            holder = new ProgramViewHolder(row);
            row.setTag(holder);

            if ((programDto.get(position).Id) == (prefs.getInt("DEFAULT_PROGRAM", programDto.get(0).Id)))
            {
                holder.name.setTextColor(ContextCompat.getColor(ctx, R.color.PrimaryColor));
                holder.subName.setTextColor(ContextCompat.getColor(ctx, R.color.PrimaryColor));
            }
        }
        else
        {
            holder = (ProgramViewHolder) row.getTag();
        }


        final ProgramDto temp = programDto.get(position);

        holder.name.setText(temp.Name);
        holder.subName.setText(String.format("%d %s",temp.RoutineCount, (temp.RoutineCount == 1) ? "routine" : "routines"));

            /*holder.btn.setOnClickListener(new OnClickListener()
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

                                ChangeDefaultProgram(position);
                            }
                            else if (item.getItemId() == R.id.edit)
                            {
                                EditDialog editDialog = new EditDialog();
                                editDialog.SetData(Program.this, AddDialogType.Program);
                                editDialog.show(Program.this.getActivity().getFragmentManager(), null);

                            }
                            else if (item.getItemId() == R.id.setDefault)
                            {
                                DefaultProgram = programDto.get(position).Id;
                                prefs.edit().putString("DEFAULT_PROGRAM", programDto.get(position).Id + "").apply();
                                SetData();
                            }
                            return false;
                        }
                    });
                }
            });*/

        return row;
    }

    /*private void ChangeDefaultProgram(int position)
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
    }*/
}