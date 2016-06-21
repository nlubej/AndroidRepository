package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.CompleteWorkoutDto;
import nlubej.gains.DataTransferObjects.LoggedRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddExerciseNoteDialog;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;
import nlubej.gains.Views.LogViewer;

/**
 * Created by nlubej on 28.4.2016.
 */
public class PreviousWorkoutsAdapter extends BaseAdapter implements View.OnClickListener
{
    private final ExerciseLogger parentClass;
    private CompleteWorkoutDto completeWorkoutDto;
    Context ctx;

    public PreviousWorkoutsAdapter(ExerciseLogger parent)
    {
        this.parentClass = parent;
        this.ctx = parent.getApplicationContext();
        completeWorkoutDto = new CompleteWorkoutDto();
        completeWorkoutDto.LoggedRows = new ArrayList<>();
    }

    @Override
    public int getCount()
    {
        return completeWorkoutDto.LoggedRows.size();
    }


    public void AddAll(ArrayList<LoggedRowDto> completeDtos)
    {
        this.completeWorkoutDto.LoggedRows.clear();
        this.completeWorkoutDto.LoggedRows.addAll(completeDtos);
    }

    public void Add(LoggedRowDto row)
    {
        completeWorkoutDto.LoggedRows.add(row);
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        if(completeWorkoutDto.LoggedRows.get(position).IsSummary)
            return 0;

        return 1;
    }

    public void Remove(LoggedRowDto item)
    {
        completeWorkoutDto.LoggedRows.remove(item);
    }

    @Override
    public LoggedRowDto getItem(int position)
    {
        return completeWorkoutDto.LoggedRows.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public void onClick(View v)
    {
    }


    class ProgramViewHolder
    {
        private LinearLayout workingRow;
        private LinearLayout exerciseSummary;
        private View divider;

        public ProgramViewHolder(View v)
        {
            workingRow = (LinearLayout)v.findViewById(R.id.logged_sets);
            exerciseSummary = (LinearLayout)v.findViewById(R.id.logged_summary);
            divider = (View)v.findViewById(R.id.divider);
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

        final LoggedRowDto temp = completeWorkoutDto.LoggedRows.get(position);

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
                    dialog.SetData(parentClass, null, true);
                    Bundle b = new Bundle();
                    if (temp.Note != null)
                        b.putString("NOTE", temp.Note);
                    else
                        b.putString("NOTE", "");

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