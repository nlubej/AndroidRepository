package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.LoggerRowDto;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 28.4.2016.
 */
public class LoggerAdapter extends BaseAdapter implements View.OnClickListener
{
    private final QueryFactory db;
    private ArrayList<LoggerRowDto> loggerRowDto;
    Context ctx;

    private SharedPreferences prefs;

    public LoggerAdapter(ExerciseLogger parent, QueryFactory database)
    {
        this.ctx = parent.getApplication();
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
        loggerRowDto.add(row);
    }


    public void Remove(LoggerRowDto item)
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

    @Override
    public void onClick(View v)
    {
        Log.i("nlubej", "it clicked");

    }

    public void Update(LoggerRowDto row)
    {
        for(LoggerRowDto dto : loggerRowDto)
        {
            if(row.LogId == dto.LogId)
            {
                dto.Rep = row.Rep;
                dto.Weight = row.Weight;
            }
        }
    }

    public void MarkForEdit(int logId, boolean flag)
    {
        for(LoggerRowDto dto : loggerRowDto)
        {
            if(dto.LogId == logId)
            {
                dto.MarkForEdit = flag;
            }
            else
            {
                dto.MarkForEdit = false;
            }
        }
    }

    class ProgramViewHolder
    {
        private ImageView note;
        private TextView personalBest;
        private TextView set;
        private TextView rep;
        private TextView weight;

        public ProgramViewHolder(View v)
        {
            note = (ImageView) v.findViewById(R.id.log_note);
            personalBest = (TextView) v.findViewById(R.id.personal_record);
            set = (TextView) v.findViewById(R.id.set);
            rep = (TextView) v.findViewById(R.id.rep);
            weight = (TextView) v.findViewById(R.id.weight);
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
            row = inflater.inflate(R.layout.row_workout_set, parent, false);
            holder = new ProgramViewHolder(row);
            row.setTag(holder);

        }
        else
        {
            holder = (ProgramViewHolder) row.getTag();
        }

        final LoggerRowDto temp = loggerRowDto.get(position);

        holder.note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.silver).sizeDp(30));
        holder.personalBest.setVisibility(View.INVISIBLE);

        holder.set.setText(String.valueOf(temp.Set));
        holder.rep.setText(temp.Rep);
        holder.weight.setText(temp.Weight);

        if(temp.MarkForEdit)
        {
            row.setBackgroundColor(ctx.getResources().getColor(R.color.colorAccentPressed));
        }
        else
        {
            row.setBackgroundColor(ctx.getResources().getColor(android.R.color.transparent));
        }
        //holder.name.setText(temp.Name);
        //holder.subName.setText(String.format("%d %s",temp.RoutineCount, (temp.RoutineCount == 1) ? "routine" : "routines"));


        return row;
    }

}