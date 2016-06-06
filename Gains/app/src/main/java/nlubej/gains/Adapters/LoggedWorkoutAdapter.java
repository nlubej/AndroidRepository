package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.DataTransferObjects.LoggedViewRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseNoteDialog;
import nlubej.gains.R;
import nlubej.gains.Views.LogViewer;

/**
 * Created by nlubej on 28.4.2016.
 */
public class LoggedWorkoutAdapter extends BaseAdapter implements View.OnClickListener
{
    private final QueryFactory db;
    private final LogViewer parentClass;
    private ArrayList<LoggedViewRowDto> loggerRowDto;
    Context ctx;

    private SharedPreferences prefs;

    public LoggedWorkoutAdapter(LogViewer parent, QueryFactory database)
    {
        this.parentClass = parent;
        this.ctx = parent.getActivity();
        loggerRowDto = new ArrayList<>();
        db = database;
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public int getCount()
    {
        return loggerRowDto.size();
    }


    public void AddAll(ArrayList<LoggedViewRowDto> programDto)
    {
        this.loggerRowDto.clear();
        this.loggerRowDto.addAll(programDto);
    }

    public void Add(LoggedViewRowDto row)
    {
        loggerRowDto.add(row);
    }

    public ArrayList<Integer> GetIdsInAscOrder(int workoutNumber)
    {
        ArrayList<Integer> ids = new ArrayList<>();
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.WorkoutNumber == workoutNumber && dto.LoggedWorkoutId != 0)
                ids.add(dto.LoggedWorkoutId);
        }

        return ids;
    }

    public void UpdateWorkoutSetNumbers(int workoutNumber)
    {
        int i = 0;
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.WorkoutNumber == workoutNumber && dto.LoggedWorkoutId != 0)
                dto.Set = ++i;
        }
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        if(loggerRowDto.get(position).IsSummary)
            return 0;

        return 1;
    }

    public void Remove(LoggedViewRowDto item)
    {
        loggerRowDto.remove(item);
    }

    @Override
    public LoggedViewRowDto getItem(int position)
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
        if(v.getId() == R.id.log_note)
        {
            Toast.makeText(ctx, "nlubej", Toast.LENGTH_SHORT).show();
        }
        Log.i("nlubej", "it clicked");

    }

    public void UpdateNote(LoggedViewRowDto row)
    {
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.LoggedWorkoutId == row.LoggedWorkoutId)
            {
                if(row.IsUpdatingNote)
                {
                    dto.Note = row.Note;
                    dto.HasNote = true;

                    if(dto.Note == null || dto.Note.compareTo("") == 0)
                        dto.HasNote = false;
                }
                else
                {
                    dto.Weight = row.Weight;
                    dto.Rep = row.Rep;
                }
            }
        }
    }

    public void RemoveNote(LoggedViewRowDto row)
    {
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.LoggedWorkoutId == row.LoggedWorkoutId)
            {
                dto.Note = null;
                dto.HasNote = false;
            }
        }
    }

    public boolean CanRemoveSummary(int workoutNumber)
    {
        int logsLeft = 0;
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.WorkoutNumber ==  workoutNumber)
            {
                logsLeft++;
            }
        }

        return logsLeft <= 1;
    }

    public void UpdateFollowingWorkoutNumber(int workoutNumber)
    {
        for(LoggedViewRowDto dto : loggerRowDto)
        {
            if(dto.WorkoutNumber > workoutNumber)
            {
                dto.WorkoutNumber--;
            }
        }
    }


    class ProgramViewHolder
    {
        private LinearLayout workingRow;
        private LinearLayout exerciseSummary;
        private View divider;

        private ImageView note;
        private TextView personalBest;
        private TextView set;
        private TextView rep;
        private TextView weight;
        private ImageView icon;
        private TextView workoutNumber;
        private TextView date;

        public ProgramViewHolder(View v)
        {
            workingRow = (LinearLayout)v.findViewById(R.id.logged_sets);
            exerciseSummary = (LinearLayout)v.findViewById(R.id.logged_summary);
            divider = (View)v.findViewById(R.id.divider);

            /*note = (ImageView) v.findViewById(R.id.log_note);
            personalBest = (TextView) v.findViewById(R.id.personal_record);
            set = (TextView) v.findViewById(R.id.set);
            rep = (TextView) v.findViewById(R.id.rep);
            weight = (TextView) v.findViewById(R.id.weight);
            icon = (ImageView) v.findViewById(R.id.workout_info);
            workoutNumber = (TextView) v.findViewById(R.id.workout_number);
            date = (TextView) v.findViewById(R.id.workout_date);*/
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
            row = inflater.inflate(R.layout.row_logged_row_base, parent, false);
            holder = new ProgramViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (ProgramViewHolder) row.getTag();
        }

        final LoggedViewRowDto temp = loggerRowDto.get(position);

        if(temp.IsSummary)
        {
            holder.workingRow.setVisibility(View.GONE);
            holder.exerciseSummary.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);

            TextView workoutNumber = (TextView) holder.exerciseSummary.findViewById(R.id.workout_number);
            TextView workoutDate = (TextView) holder.exerciseSummary.findViewById(R.id.workout_date);
            ImageView icon = (ImageView) holder.exerciseSummary.findViewById(R.id.workout_info);

            icon.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_info_outline).actionBarSize().colorRes(R.color.PrimaryColor).actionBarSize());
            icon.setVisibility(View.INVISIBLE);
            workoutNumber.setText("# " + String.valueOf(temp.WorkoutNumber));
        }
        else
        {
            holder.exerciseSummary.setVisibility(View.GONE);
            holder.workingRow.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.GONE);

            ImageView note = (ImageView)  holder.workingRow.findViewById(R.id.log_note);
            note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.silver).sizeDp(30));

            note.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        AddExerciseNoteDialog dialog = new AddExerciseNoteDialog();
                        dialog.SetData(parentClass, db);
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

            if(!temp.HasNote)
            {
                note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.silver).sizeDp(30));
            }
            else
            {
                note.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_comment_text_alt).colorRes(R.color.colorAccent).sizeDp(30));
            }

            TextView personalBest = (TextView) holder.workingRow.findViewById(R.id.personal_record);
            TextView set = (TextView)  holder.workingRow.findViewById(R.id.set);
            TextView rep = (TextView)  holder.workingRow.findViewById(R.id.rep);
            TextView weight = (TextView)  holder.workingRow.findViewById(R.id.weight);

            set.setText(String.valueOf(temp.Set));
            rep.setText(temp.Rep);
            weight.setText(temp.Weight);
            personalBest.setVisibility(View.INVISIBLE);
        }

        return row;
    }

}