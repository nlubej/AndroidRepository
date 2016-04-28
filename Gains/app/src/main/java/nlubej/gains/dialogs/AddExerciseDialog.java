package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import fr.ganfra.materialspinner.MaterialSpinner;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Views.Exercise;
import nlubej.gains.R;
import nlubej.gains.Adapters.ExerciseTypeAdapter;
import nlubej.gains.interfaces.OnItemAdded;
import nlubej.gains.interfaces.onActionSubmit;

/**
 * Created by nlubej on 24.10.2015.
 */
public class AddExerciseDialog extends DialogFragment implements View.OnClickListener
{
    private Exercise fragmentClass;
    private QueryFactory db;
    private OnItemAdded callback;
    private Context context;
    private int routineId;
    private MaterialEditText exerciseName;
    private MaterialSpinner exerciseType;
    private int exerciseTypeId;
    private AlertDialog alertDialog;
    private String[] exerciseTypes;
    private ArrayAdapter exerciseTypeAdapter;

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            callback = (OnItemAdded<ExerciseDto>) fragmentClass;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement OnSubmit");
        }
    }

    public void SetData(Exercise exercise)
    {
        fragmentClass = exercise;
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
                if (exerciseName.getText().toString().compareTo("") != 0)
                {
                    db.Open();
                    exerciseId = db.InsertExercise(exerciseName.getText().toString(), exerciseType.getSelectedItemPosition()+1, routineId);
                    db.Close();
                }
                else
                {
                    wantToCloseDialog = false;
                }


                if (wantToCloseDialog)
                {
                    callback.OnAdded(new ExerciseDto(exerciseId, exerciseName.getText().toString(),(int)exerciseType.getSelectedItemId(),routineId));
                    alertDialog.dismiss();
                }
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }
}
