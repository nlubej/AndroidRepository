package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.Adapters.ExerciseTypeAdapter;
import nlubej.gains.DataTransferObjects.ExerciseType;
import nlubej.gains.DataTransferObjects.LoggedRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.LogViewer;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 2.6.2016.
 */
public class UpdateLoggedWorkoutDialog extends DialogFragment implements View.OnClickListener
{
    private QueryFactory db;
    private OnItemChanged<LoggedRowDto> parent;
    private Context context;

    private int loggedWorkoutId;
    private AlertDialog alertDialog;
    private ArrayList<ExerciseType> exerciseTypes;
    private ExerciseTypeAdapter exerciseTypeAdapter;
    private EditText editReps;
    private EditText editWeight;
    private Button addNew;

    public void SetData(LogViewer exercise, QueryFactory database)
    {
        parent = exercise;
        context = exercise.getActivity();
        db = database;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_logger_row, null);

        ImageView repsPlus = (ImageView) view.findViewById(R.id.repsPlus);
        ImageView repsMinus = (ImageView) view.findViewById(R.id.repsMinus);
        ImageView weightPlus = (ImageView) view.findViewById(R.id.weightPlus);
        ImageView weightMinus = (ImageView) view.findViewById(R.id.weightMinus);
        TextView exerciseName = (TextView) view.findViewById(R.id.exerciseName);
        editReps = (EditText) view.findViewById(R.id.editReps);
        editWeight = (EditText) view.findViewById(R.id.editWeight);
        addNew = (Button) view.findViewById(R.id.addNew);
        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);
        LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.header2);
        headerLayout.setVisibility(View.GONE);
        addNew.setVisibility(View.GONE);

        exerciseName.setText(getArguments().getString("EXERCISE_NAME"));
        loggedWorkoutId = getArguments().getInt("LOGGED_WORKOUT_ID");
        String rep = getArguments().getString("REP");
        String weight = getArguments().getString("WEIGHT");

        editWeight.setText(weight);
        editReps.setText(rep);

        yes.setOnClickListener(this);
        yes.setText("Update");

        no.setOnClickListener(this);

        repsPlus.setOnClickListener(this);
        repsMinus.setOnClickListener(this);
        weightPlus.setOnClickListener(this);
        weightMinus.setOnClickListener(this);

        repsPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        repsMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));

        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }


    @Override
    public void onClick(View v)
    {
        int val;
        double dval;
        switch (v.getId())
        {

            case R.id.btn_yes:
                db.Open();
                db.UpdateTable("LOGGED_WORKOUT", String.format("LOGGED_WORKOUT_ID = %s", loggedWorkoutId), new String[]{"LOGGED_WEIGHT", editWeight.getText().toString(), "LOGGED_REP", editReps.getText().toString()});
                db.Close();

                LoggedRowDto row = new LoggedRowDto();
                row.Rep = editReps.getText().toString();
                row.LoggedWorkoutId = loggedWorkoutId;
                row.Weight = editWeight.getText().toString();

                parent.OnUpdated(row);
                alertDialog.dismiss();

                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;

            case R.id.repsPlus:
                val = ToInt(editReps.getText().toString()) + 1;
                editReps.setText(String.valueOf(val));
                break;
            case R.id.repsMinus:
                val = ToInt(editReps.getText().toString()) - 1;

                if(val >= 0)
                    editReps.setText(String.valueOf(val));

                break;
            case R.id.weightPlus:
                dval = ToDouble(editWeight.getText().toString()) + 1;
                editWeight.setText(String.valueOf(dval));

                break;
            case R.id.weightMinus:
                dval = ToDouble(editWeight.getText().toString()) - 1;

                if(dval >= 0)
                    editWeight.setText(String.valueOf(dval));
                break;
        }
    }

    private double ToDouble(String value)
    {
        return Double.parseDouble(value);
    }

    private int ToInt(String value)
    {
        return Integer.parseInt(value);
    }
}