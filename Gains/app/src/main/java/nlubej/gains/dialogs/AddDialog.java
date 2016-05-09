package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.R;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 22.10.2015.
 */
public class AddDialog extends DialogFragment implements View.OnClickListener
{

    private Object fragmentClass;
    private AddDialogType dialogType;
    private QueryFactory db;
    private OnItemChanged callback;
    private Context context;
    private int programId;
    private MaterialEditText program;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            callback = (OnItemChanged<RoutineDto>) fragmentClass;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement OnSubmit");
        }
    }

    public void SetData(Object fragment, AddDialogType type)
    {
        this.fragmentClass = fragment;
        this.dialogType = type;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        context = getActivity();

        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit, null);

        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        if (dialogType == AddDialogType.Routine)
        {
            programId = getArguments().getInt("PROGRAM_ID");
        }
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();


       /* alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = true;

                if (program.getText().toString().compareTo("") != 0)
                {
                    db.Open();
                    if (dialogType == AddDialogType.Program)
                    {
                        db.InsertProgram(program.getText().toString());
                    }
                    else
                    {
                        db.InsertRoutine(program.getText().toString(), programId);
                    }
                    db.Close();
                }
                else
                {
                    wantToCloseDialog = false;
                }


                if (wantToCloseDialog)
                {
                    callback.OnAdded("Add");
                    alertDialog.dismiss();
                }
            }
        });*/

       /* alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });
*/
        return alertDialog;
    }

    @Override
    public void onClick(View v)
    {
        Boolean wantToCloseDialog = true;
        switch (v.getId())
        {

            case R.id.btn_yes:
                if (program.getText().toString().compareTo("") != 0)
                {
                    db.Open();
                    if (dialogType == AddDialogType.Program)
                    {
                        Log.d("nlubej", "INSERTED: " + db.InsertProgram(program.getText().toString()));
                    }
                    else
                    {
                        Log.d("nlubej", "INSERTED: " + db.InsertRoutine(program.getText().toString(), programId));
                    }
                    db.Close();
                }
                else
                {
                    program.setError("Name must not be empty.");
                    wantToCloseDialog = false;
                }

                if (wantToCloseDialog)
                {
                    //callback.OnAdded(new RoutineDto(program.getText().toString(),programId));
                    alertDialog.dismiss();
                }
                break;
            case R.id.btn_no:
                alertDialog.dismiss();
            break;
        }
    }
}
