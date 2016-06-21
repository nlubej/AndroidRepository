package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.Adapters.PreviousWorkoutsAdapter;
import nlubej.gains.DataTransferObjects.CompleteWorkoutDto;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.DataTransferObjects.LoggedRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 8.5.2016.
 */
public class PreviousWorkoutsDialog  extends DialogFragment implements View.OnClickListener
{
    private QueryFactory db;
    private AlertDialog alertDialog;
    private ListView listView;
    Context ctx;
    private ExerciseLogger parent;
    private int exerciseId;
    private ArrayList<CompleteWorkoutDto> completeWorkoutDtos;
    private PreviousWorkoutsAdapter loggedWorkoutAdapter;
    int currentWorkoutNumber;
    private ImageView nextExercise;
    private ImageView previousExercise;
    private TextView header;
    private TextView copyBtn;
    private int workoutNumber;
    private int routineExerciseId;

    public void SetData(ExerciseLogger context, QueryFactory database)
    {
        parent = context;
        ctx = context;
        db = database;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_previous_workouts, null);

        listView = (ListView) view.findViewById(R.id.listView);
        nextExercise = (ImageView) view.findViewById(R.id.nextExercise);
        previousExercise = (ImageView) view.findViewById(R.id.previousExercise);
        header = (TextView) view.findViewById(R.id.header);
        copyBtn = (TextView) view.findViewById(R.id.copyBtn);

        nextExercise.setOnClickListener(this);
        previousExercise.setOnClickListener(this);
        copyBtn.setOnClickListener(this);

        previousExercise.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_chevron_left).colorRes(R.color.white).sizeDp(50));
        nextExercise.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_chevron_right).colorRes(R.color.white).sizeDp(50));

        loggedWorkoutAdapter = new PreviousWorkoutsAdapter(parent);

        exerciseId = getArguments().getInt("EXERCISE_ID");
        routineExerciseId = getArguments().getInt("ROUTINE_EXERCISE_ID");
        workoutNumber = getArguments().getInt("WORKOUT_NUMBER");
        InitData();

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    public void InitData()
    {
        db.Open();
        completeWorkoutDtos = db.SelectLoggedWorkoutsGroupedByWorkoutNumber(exerciseId, workoutNumber);
        db.Close();

        if(completeWorkoutDtos == null || completeWorkoutDtos.size() == 0)
        {
            nextExercise.setEnabled(false);
            previousExercise.setEnabled(false);
            listView.setVisibility(View.GONE);
            listView.setMinimumHeight(10);
            header.setText("No Gains");
            copyBtn.setEnabled(false);
            return;
        }

        currentWorkoutNumber = 0;

        if(completeWorkoutDtos.size() == 1)
        {
            previousExercise.setEnabled(false);
        }
        nextExercise.setEnabled(false);

        listView.setAdapter(loggedWorkoutAdapter);
        loggedWorkoutAdapter.AddAll(completeWorkoutDtos.get(currentWorkoutNumber).LoggedRows);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.previousExercise:
                currentWorkoutNumber++;
                RefreshAdapter();

                nextExercise.setEnabled(true);

                if (currentWorkoutNumber == completeWorkoutDtos.size() - 1)
                    previousExercise.setEnabled(false);
                break;
            case R.id.nextExercise:
                currentWorkoutNumber--;
                RefreshAdapter();

                previousExercise.setEnabled(true);

                if (currentWorkoutNumber == 0)
                    nextExercise.setEnabled(false);

                break;
            case R.id.copyBtn:
                db.Open();
                ArrayList<ExerciseLoggerRow>  loggerRows = new ArrayList<>();
                for (LoggedRowDto dto : completeWorkoutDtos.get(currentWorkoutNumber).LoggedRows)
                {
                    if(dto.IsSummary)
                        continue;

                    int id = db.InsertExerciseLog(routineExerciseId, workoutNumber, dto.Set, dto.Rep, dto.Weight);

                    ExerciseLoggerRow row = new ExerciseLoggerRow();
                    row.LoggedWorkoutId = id;
                    row.Set = dto.Set;
                    row.Rep = dto.Rep;
                    row.Weight = dto.Weight;

                    loggerRows.add(row);
                }

                parent.OnCopy(loggerRows);

                db.Close();
                alertDialog.dismiss();
                break;
        }
    }

    private void RefreshAdapter()
    {
        loggedWorkoutAdapter.AddAll(completeWorkoutDtos.get(currentWorkoutNumber).LoggedRows);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }
}
