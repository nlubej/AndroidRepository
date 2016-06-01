package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;

import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.DataTransferObjects.LoggedViewRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.ExerciseLogger;
import nlubej.gains.Views.LogViewer;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 22.10.2015.
 */
public class AddExerciseNoteDialog extends DialogFragment implements View.OnClickListener
{

    private Object fragmentClass;
    private QueryFactory db;
    private OnItemChanged callback;
    private Context context;
    private AlertDialog alertDialog;
    private MaterialEditText noteTxt;
    private int logId;
    boolean isUpdating = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            if(fragmentClass instanceof LogViewer)
            {
                callback = (OnItemChanged<LoggedViewRowDto>) fragmentClass;
            }
            else if (fragmentClass instanceof  ExerciseLogger)
            {
                callback = (OnItemChanged<ExerciseLoggerRow>) fragmentClass;
            }
            else
            {
                throw new ClassCastException("Calling Fragment must implement OnSubmit");
            }
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Calling Fragment must implement OnSubmit");
        }
    }

    public void SetData(Object fragment, QueryFactory database)
    {
        this.fragmentClass = fragment;
        db = database;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        context = getActivity();



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_note, null);
        noteTxt = (MaterialEditText) view.findViewById(R.id.note_txt);
        logId = getArguments().getInt("LOGGED_WORKOUT_ID");

        if(getArguments().containsKey("NOTE"))
        {
            isUpdating = true;
            noteTxt.setText(getArguments().getString("NOTE"));
        }

        Button yes = (Button)view.findViewById(R.id.btn_yes);
        Button no = (Button)view.findViewById(R.id.btn_no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);


        builder.setView(view);
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

                if(!IsValid() && !isUpdating)
                {
                    noteTxt.setError("Write a note");
                    return;
                }

                if (!isUpdating)
                {
                    db.Open();
                    db.InsertIntoTable("WORKOUT_NOTE", new String[] { "LOGGED_WORKOUT_ID", String.valueOf(logId), "NOTE", noteTxt.getText().toString() });
                    db.Close();

                    if(fragmentClass instanceof LogViewer)
                    {
                        LoggedViewRowDto row = new LoggedViewRowDto();
                        row.LoggedWorkoutId = logId;
                        row.Note = noteTxt.getText().toString();

                        callback.OnAdded(row);
                    }
                    else if (fragmentClass instanceof  ExerciseLogger)
                    {
                        ExerciseLoggerRow row = new ExerciseLoggerRow();
                        row.LoggedWorkoutId = logId;
                        row.Note = noteTxt.getText().toString();

                        callback.OnAdded(row);
                    }

                    HideKeyboard();
                    alertDialog.dismiss();
                }
                else
                {
                    if(noteTxt.getText().toString().compareTo("") == 0) //remove note
                    {
                        db.Open();
                        db.DeleteRecord("WORKOUT_NOTE", "LOGGED_WORKOUT_ID = ?", new String[]{String.valueOf(logId)});
                        db.Close();

                        if(fragmentClass instanceof LogViewer)
                        {
                            LoggedViewRowDto row = new LoggedViewRowDto();
                            row.LoggedWorkoutId = logId;

                            callback.OnRemoved(row);
                        }
                        else if (fragmentClass instanceof  ExerciseLogger)
                        {
                            LoggedViewRowDto row = new LoggedViewRowDto();
                            row.LoggedWorkoutId = logId;

                            callback.OnRemoved(row);
                        }

                        HideKeyboard();
                        alertDialog.dismiss();
                    }
                    else //update
                    {
                        db.Open();
                        db.UpdateTable("WORKOUT_NOTE", String.format("LOGGED_WORKOUT_ID = %d", logId), new String[]{"NOTE", noteTxt.getText().toString()});
                        db.Close();

                        if(fragmentClass instanceof LogViewer)
                        {
                            LoggedViewRowDto row = new LoggedViewRowDto();
                            row.LoggedWorkoutId = logId;
                            row.Note = noteTxt.getText().toString();

                            callback.OnUpdated(row);
                        }
                        else if (fragmentClass instanceof  ExerciseLogger)
                        {
                            ExerciseLoggerRow row = new ExerciseLoggerRow();
                            row.LoggedWorkoutId = logId;
                            row.Note = noteTxt.getText().toString();

                            callback.OnUpdated(row);
                        }

                        HideKeyboard();
                        alertDialog.dismiss();
                    }
                }

                break;
            case R.id.btn_no:
                HideKeyboard();
                alertDialog.dismiss();
                break;
        }
    }

    private void HideKeyboard()
    {
        noteTxt.clearFocus();
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(noteTxt.getWindowToken(), 0);

    }

    private boolean IsValid()
    {
        if (noteTxt.getText().toString().compareTo("") != 0)
        {
            return true;
        }

        return false;
    }
}
