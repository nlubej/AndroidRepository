package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.Routine;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 24.10.2015.
 */
public class EditRoutineDialog extends DialogFragment implements View.OnClickListener
{
    private QueryFactory db;
    private OnItemChanged<RoutineDto> parent;
    private Context context;
    private MaterialEditText routineName;
    private int routineId;
    private int programId;
    private AlertDialog alertDialog;

    public void SetData(Routine routine, QueryFactory database)
    {
        parent = routine;
        context = routine;
        db = database;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit, null);

        routineName = (MaterialEditText) view.findViewById(R.id.name);
        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);

        routineName.setText(getArguments().getString("ROUTINE_NAME"));
        routineId = getArguments().getInt("ROUTINE_ID");
        programId = getArguments().getInt("PROGRAM_ID");

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        builder.setView(view);
        builder.setTitle("Edit");

        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }

    @Override
    public void onClick(View v)
    {
        Boolean wantToCloseDialog = true;
        switch (v.getId())
        {

            case R.id.btn_yes:
                if (routineName.getText().toString().compareTo("") != 0)
                {
                    db.Open();
                    db.UpdateRoutine(routineName.getText().toString(), routineId);
                    db.Close();
                }
                else
                {
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog)
                {
                    parent.OnUpdated(new RoutineDto(routineId, routineName.getText().toString(), programId));
                    alertDialog.dismiss();
                }
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
                break;
        }
    }
}
