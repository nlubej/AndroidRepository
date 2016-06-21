package nlubej.gains.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import nlubej.gains.Adapters.SelectionAdapter;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Enums.Constants;
import nlubej.gains.R;
import nlubej.gains.Tools;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 5. 06. 2016.
 */
public class IncrementChooserDialog extends DialogFragment implements AdapterView.OnItemClickListener
{
    private AlertDialog alertDialog;
    private ListView listView;
    Context ctx;
    private ExerciseLogger parent;
    private SelectionAdapter selectionAdapter;
    private SharedPreferences prefs;

    String[] incrementValues = new String[]
            {
                    "0.15",
                    "0.25",
                    "0.50",
                    "1.00",
                    "1.50",
                    "2.00"
            };

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
        View view = inflater.inflate(R.layout.dialog_increment_chooser, null);
        selectionAdapter = new SelectionAdapter(parent, incrementValues);
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        listView = (ListView) view.findViewById(R.id.listView);

        listView.setOnItemClickListener(this);
        listView.setAdapter(selectionAdapter);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    @Override
    public void onItemClick(AdapterView<?> p, View view, int position, long id)
    {
        selectionAdapter.SetSelectedRow((int)id);
        selectionAdapter.notifyDataSetChanged();

        SelectionAdapter.SelectionRow row = selectionAdapter.GetSelectedItem();

        parent.OnSelection(Double.parseDouble(row.Item));
        prefs.edit().putString(Constants.WEIGHT_INCREMENT_KEY, row.Item).apply();
        alertDialog.dismiss();
    }
}
