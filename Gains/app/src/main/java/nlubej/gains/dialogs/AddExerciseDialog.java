package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseType;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Views.Exercise;
import nlubej.gains.R;
import nlubej.gains.Adapters.ExerciseTypeAdapter;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 24.10.2015.
 */
public class AddExerciseDialog extends DialogFragment implements View.OnClickListener
{
    private QueryFactory db;
    private OnItemChanged parent;
    private Context context;
    private int routineId;
    private MaterialEditText exerciseName;
    private MaterialSpinner exerciseType;
    private int exerciseTypeId;
    private AlertDialog alertDialog;
    private ArrayList<ExerciseType> exerciseTypes;
    private ArrayAdapter exerciseTypeAdapter;

    public void SetData(Exercise exercise)
    {
        parent = exercise;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        context = getActivity();
        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit_exercise, null);
        exerciseName = (MaterialEditText) view.findViewById(R.id.exerciseName);
        exerciseType = (MaterialSpinner) view.findViewById(R.id.exerciseType);
        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);

        Init();
        yes.setOnClickListener(this);
        no.setOnClickListener(this);



        exerciseName.setHint("Squats");
        routineId = getArguments().getInt("ROUTINE_ID");

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }

    private void Init()
    {
        db.Open();
        exerciseTypes = db.SelecExerciseType();
        db.Close();

        exerciseTypeAdapter = new ExerciseTypeAdapter(context, R.layout.row_spinner,exerciseTypes);
        exerciseType.setAdapter(exerciseTypeAdapter);
    }

    @Override
    public void onClick(View v)
    {
        int exerciseId = 0;
        Boolean wantToCloseDialog = true;
        switch (v.getId())
        {

            case R.id.btn_yes:

                if(!Validate())
                {
                    return;
                }

                db.Open();
                exerciseId = db.InsertExercise(exerciseName.getText().toString(), ((ExerciseType)exerciseType.getSelectedItem()).Id, routineId);
                exerciseId = db.InsertRoutineExerciseConnection(routineId, exerciseId);
                db.Close();

                parent.OnAdded(new ExerciseDto(exerciseId, exerciseName.getText().toString(),((ExerciseType)exerciseType.getSelectedItem()).Id));
                alertDialog.dismiss();

                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }

    private boolean Validate()
    {
        if (exerciseName.getText().toString().compareTo("") == 0)
        {
            exerciseName.setError("Write a name");
            return false;
        }

        if (exerciseType.getSelectedItemId() == 0)
        {
            exerciseType.setError("Choose a type");
            return false;
        }

        return true;
    }
}
