package nlubej.gains.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 5. 06. 2016.
 */
public class ConfirmationDialog extends DialogFragment implements View.OnClickListener
{
    private AlertDialog alertDialog;
    Context ctx;
    private ExerciseLogger parent;
    private QueryFactory db;
    private  ExerciseDto exerciseDto;

    public void SetData(ExerciseLogger context)
    {
        parent = context;
        ctx = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.multi_purpose_dialog, null);

        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);
        TextView header = (TextView)view.findViewById(R.id.header);
        TextView text = (TextView)view.findViewById(R.id.text);

        header.setText("Finish Workout");
        text.setText("Finish current workout?");
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        yes.setText("FINISH");
        no.setText("CANCEL");

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

                alertDialog.dismiss();
                FinishWorkout();
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }

    private void FinishWorkout()
    {
        WorkoutSummaryDialog dialog = new WorkoutSummaryDialog();
        dialog.SetData(parent, db);
        dialog.show(getFragmentManager(), "");
    }
}
