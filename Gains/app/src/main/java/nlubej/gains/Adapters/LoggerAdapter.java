package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseNoteDialog;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 28.4.2016.
 */
public class LoggerAdapter extends BaseAdapter
{
    private final QueryFactory db;
    private final ExerciseLogger parentClass;
    private ArrayList<ExerciseLoggerRow> loggerRowDto;
    Context ctx;

    private SharedPreferences prefs;

    public LoggerAdapter(ExerciseLogger parent, QueryFactory database)
    {
        this.ctx = parent.getApplication();
        this.parentClass = parent;
        loggerRowDto = new ArrayList<>();
        db = database;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public int getCount()
    {
        return loggerRowDto.size();
    }


    public void AddAll(ArrayList<ExerciseLoggerRow> programDto)
    {
        this.loggerRowDto.clear();
        this.loggerRowDto.addAll(programDto);
    }

    public void Add(ExerciseLoggerRow row)
    {
        loggerRowDto.add(row);
    }


    public void Remove(ExerciseLoggerRow item)
    {
        loggerRowDto.remove(item);
    }

    @Override
    public ExerciseLoggerRow getItem(int position)
    {
        return loggerRowDto.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void Update(ExerciseLoggerRow row)
    {
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            if(row.LoggedWorkoutId == dto.LoggedWorkoutId)
            {
                dto.Rep = row.Rep;
                dto.Weight = row.Weight;
            }
        }
    }

    public void MarkForEdit(int logId, boolean flag)
    {
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            if(dto.LoggedWorkoutId == logId)
            {
                dto.MarkForEdit = flag;
            }
            else
            {
                dto.MarkForEdit = false;
            }
        }
    }

    public ArrayList<Integer> GetIdsInAscOrder()
    {
        ArrayList<Integer> ids = new ArrayList<>();
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            ids.add(dto.LoggedWorkoutId);
        }

        return ids;
    }

    public void UpdateWorkoutSetNumbers()
    {
        int i = 0;
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            dto.Set = ++i;
        }
    }

    public void RemoveNote(ExerciseLoggerRow row)
    {
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            if(dto.LoggedWorkoutId == row.LoggedWorkoutId)
            {
                dto.Note = null;
                dto.HasNote = false;
            }
        }
    }

    public void UpdateNote(ExerciseLoggerRow row)
    {
        for(ExerciseLoggerRow dto : loggerRowDto)
        {
            if(dto.LoggedWorkoutId == row.LoggedWorkoutId)
            {
                dto.Note = row.Note;
                dto.HasNote = true;

                if(dto.Note == null || dto.Note.compareTo("") == 0)
                    dto.HasNote = false;
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
    public View getView(final int position, View convertView, final ViewGroup parent)
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

        final ExerciseLoggerRow temp = loggerRowDto.get(position);

        if(!temp.HasNote)
        {
            holder.note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.silver).sizeDp(30));
        }
        else
        {
            holder.note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.PrimaryColor).sizeDp(30));
        }

        holder.note.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AddExerciseNoteDialog dialog = new AddExerciseNoteDialog();
                dialog.SetData(parentClass, db, false);
                Bundle b = new Bundle();
                if (temp.Note != null && temp.Note.compareTo("") != 0)
                {
                    b.putString("NOTE", temp.Note);
                }
                b.putInt("LOGGED_WORKOUT_ID", temp.LoggedWorkoutId);
                dialog.setArguments(b);
                dialog.show(parentClass.getFragmentManager(), "");
            }
        });



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