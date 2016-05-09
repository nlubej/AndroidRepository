package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.malinskiy.materialicons.widget.IconTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;
import nlubej.gains.Adapters.ExerciseTypeAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseType;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Views.Exercise;
import nlubej.gains.R;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 24.10.2015.
 */
public class EditExerciseDialog extends DialogFragment implements View.OnClickListener
{
    private QueryFactory db;
    private OnItemChanged<ExerciseDto> parent;
    private Context context;
    private MaterialEditText exerciseName;
    private MaterialSpinner exerciseType;
    private int exerciseId;
    private AlertDialog alertDialog;
    private ArrayList<ExerciseType> exerciseTypes;
    private ExerciseTypeAdapter exerciseTypeAdapter;

    public void SetData(Exercise exercise, QueryFactory database)
    {
        parent = exercise;
        context = exercise;
        db = database;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit_exercise, null);

        exerciseName = (MaterialEditText) view.findViewById(R.id.exerciseName);
        exerciseType = (MaterialSpinner) view.findViewById(R.id.exerciseType);
        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);

        exerciseName.setText(getArguments().getString("EXERCISE_NAME"));
        exerciseType.setSelection(getArguments().getInt("EXERCISE_TYPE"));
        exerciseId = getArguments().getInt("EXERCISE_ID");

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        Init();

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

        exerciseTypeAdapter = new ExerciseTypeAdapter(context, R.layout.row_spinner, exerciseTypes);
        exerciseType.setAdapter(exerciseTypeAdapter);
    }

    @Override
    public void onClick(View v)
    {
        Boolean wantToCloseDialog = true;
        switch (v.getId())
        {

            case R.id.btn_yes:
                if (exerciseName.getText().toString().compareTo("") != 0)
                {
                    db.Open();
                    db.UpdateExercise(exerciseName.getText().toString(), ((ExerciseType)exerciseType.getSelectedItem()).Id, exerciseId);
                    db.Close();
                }
                else
                {
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog)
                {
                    parent.OnUpdated(new ExerciseDto(exerciseId,exerciseName.getText().toString(),((ExerciseType)exerciseType.getSelectedItem()).Id));
                    alertDialog.dismiss();
                }
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }
}
