package nlubej.gains.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 5. 06. 2016.
 */
public class WorkoutSummaryDialog extends DialogFragment implements View.OnClickListener
{
    private AlertDialog alertDialog;
    Context ctx;
    private ExerciseLogger parent;
    private QueryFactory db;
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
        View view = inflater.inflate(R.layout.dialog_workout_summary2, null);

        TextView currSets = (TextView) view.findViewById(R.id.currSets);
        TextView prevSets = (TextView) view.findViewById(R.id.prevSets);
        TextView currReps = (TextView) view.findViewById(R.id.currReps);
        TextView prevReps = (TextView) view.findViewById(R.id.prevReps);
        TextView currWeight = (TextView) view.findViewById(R.id.currWeight);
        TextView prevWeight = (TextView) view.findViewById(R.id.prevWeight);
        TextView currDuration = (TextView) view.findViewById(R.id.currDuration);
        TextView prevDuration = (TextView) view.findViewById(R.id.prevDuration);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_yes:
                FinishWorkout();
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }

    private void FinishWorkout()
    {

    }
}
