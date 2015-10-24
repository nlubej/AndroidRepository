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
public class DeleteDialog extends DialogFragment
{
    private Object fragmentClass;
    private AddDialogType dialogType;
    private QueryFactory db;
    private onActionSubmit callback;
    private Context context;
    private int programId;
    private int routineId;

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
        View view = inflater.inflate(
                //R.layout.dialog_delete
                R.layout.dialog_add_edit
                , null);

        if(dialogType == AddDialogType.Program)
            programId = getArguments().getInt("PROGRAM_ID");

        if(dialogType == AddDialogType.Program)
            routineId = getArguments().getInt("ROUTINE_ID");


        builder.setView(view);
        builder.setTitle("Delete");

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
                db.Open();
                if (dialogType == AddDialogType.Program)
                {
                    db.DeleteProgram(programId);
                }
                else
                {
                    db.DeleteRoutine(routineId);
                }
                db.Close();

                callback.OnSubmit("");
                alertDialog.dismiss();

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
