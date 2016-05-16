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

import nlubej.gains.DataTransferObjects.LoggerRowDto;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 28.4.2016.
 */
public class LoggerAdapter extends BaseAdapter
{
    private final QueryFactory db;
    private ArrayList<LoggerRowDto> loggerRowDto;
    Context ctx;
    public static long DefaultProgram = -1; //used in other classes

    private SharedPreferences prefs;

    public LoggerAdapter(ExerciseLogger parent, QueryFactory database)
    {
        this.ctx = parent.getActivity().getApplicationContext();
        loggerRowDto = new ArrayList<>();
        db = database;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public int getCount()
    {
        return loggerRowDto.size();
    }


    public void AddAll(ArrayList<LoggerRowDto> programDto)
    {
        this.loggerRowDto.clear();
        this.loggerRowDto.addAll(programDto);
    }

    public void Add(LoggerRowDto row)
    {
        row.RoutineCount = 0;
        loggerRowDto.add(row);
    }


    public void Remove(ProgramDto item)
    {
        loggerRowDto.remove(item);
    }

    @Override
    public LoggerRowDto getItem(int position)
    {
        return loggerRowDto.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void UpdateRoutineCount(int programId, int routineCount)
    {
        for(LoggerRowDto dto : loggerRowDto)
        {
            if(dto.Id == programId)
            {
                dto.RoutineCount = routineCount;
                return;
            }
        }
    }

    public void Update(LoggerRowDto row)
    {
        for(LoggerRowDto dto : loggerRowDto)
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

            if ((loggerRowDto.get(position).Id) == (prefs.getInt("DEFAULT_PROGRAM", loggerRowDto.get(0).Id)))
            {
                holder.name.setTextColor(ContextCompat.getColor(ctx, R.color.PrimaryColor));
                holder.subName.setTextColor(ContextCompat.getColor(ctx, R.color.PrimaryColor));
            }
        }
        else
        {
            holder = (ProgramViewHolder) row.getTag();
        }


        final LoggerRowDto temp = loggerRowDto.get(position);

        holder.name.setText(temp.Name);
        holder.subName.setText(String.format("%d %s",temp.RoutineCount, (temp.RoutineCount == 1) ? "routine" : "routines"));

        return row;
    }

}