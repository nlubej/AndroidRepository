package nlubej.gains.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nlubej.gains.Adapters.SelectionAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Enums.Constants;
import nlubej.gains.R;
import nlubej.gains.Tools;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 5. 06. 2016.
 */
public class PopupDialog extends DialogFragment implements View.OnClickListener
{
    private AlertDialog alertDialog;
    Context ctx;
    private ExerciseLogger parent;
    private QueryFactory db;
    private  ExerciseDto exerciseDto;
    private  ArrayList<ExerciseDto> exerciseCollection;


    public void SetData(ExerciseLogger context, QueryFactory database, ArrayList<ExerciseDto> exerciseCollection, int currentExerciseLogNumber)
    {
        parent = context;
        ctx = context;
        db = database;
        exerciseDto = exerciseCollection.get(currentExerciseLogNumber);
        this.exerciseCollection = exerciseCollection;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_popup, null);

        TextView previousWorkouts = (TextView) view.findViewById(R.id.previousWorkouts);
        TextView stopwatch = (TextView) view.findViewById(R.id.stopwatch);
        TextView addExercise = (TextView) view.findViewById(R.id.addExercise);

        previousWorkouts.setOnClickListener(this);
        stopwatch.setOnClickListener(this);
        addExercise.setOnClickListener(this);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    @Override
    public void onClick(View v)
    {
        Bundle b = new Bundle();
        switch (v.getId())
        {
            case R.id.previousWorkouts:

                PreviousWorkoutsDialog previousWorkoutsDialog = new PreviousWorkoutsDialog();
                previousWorkoutsDialog.SetData(parent, db);

                b = new Bundle();
                b.putInt("EXERCISE_ID", exerciseDto.Id);
                b.putInt("ROUTINE_EXERCISE_ID", exerciseDto.RoutineExerciseId);
                b.putInt("WORKOUT_NUMBER", exerciseDto.WorkoutNumber);
                previousWorkoutsDialog.setArguments(b);

                previousWorkoutsDialog.show(getFragmentManager(), "");
                break;
            case R.id.stopwatch:

                StopwatchDialog dialog = new StopwatchDialog();
                dialog.SetData(parent);
                dialog.show(getFragmentManager(), "");
                break;
            case R.id.addExercise:

                SearchExerciseDialog addExisting = new SearchExerciseDialog();
                addExisting.SetData(parent, db, GetAllIds());
                b = new Bundle();
                b.putInt("ROUTINE_ID", exerciseDto.RoutineId);
                addExisting.setArguments(b);
                addExisting.show(getFragmentManager(), "");
                break;
        }

        alertDialog.dismiss();
    }

    private ArrayList<Integer> GetAllIds()
    {
        ArrayList<Integer> ids = new ArrayList<>();
            for(ExerciseDto dto : exerciseCollection)
            {
                ids.add(dto.Id);
            }

        return  ids;
    }
}
