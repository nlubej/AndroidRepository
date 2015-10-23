package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Enums.AddDialogType;
import nlubej.gains.R;
import nlubej.gains.interfaces.onActionSubmit;

/**
 * Created by nlubej on 22.10.2015.
 */
public class AddDialog extends DialogFragment
{

    private Object fragmentClass;
    private AddDialogType dialogType;
    private QueryFactory db;
    private onActionSubmit callback;
    private Context context;

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            callback = (onActionSubmit) fragmentClass;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement OnSubmit");
        }
    }

    public void SetData (Object fragment, AddDialogType type)
    {
        this.fragmentClass = fragment;
        this.dialogType = type;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        context = getActivity().getApplicationContext();

        db = new QueryFactory(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_edit, null);
        final EditText program = (EditText) view.findViewById(R.id.programName);

        builder.setView(view);
        builder.setTitle("ADD");

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick (DialogInterface dialog, int which)
            {

            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick (DialogInterface dialog, int which)
            {
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
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
                        db.InsertRoutine(program.getText().toString());
                    }
                    db.Close();
                }
                else
                {
                    wantToCloseDialog = false;
                }


                if (wantToCloseDialog)
                {
                    callback.OnSubmit("Add");
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }
}
