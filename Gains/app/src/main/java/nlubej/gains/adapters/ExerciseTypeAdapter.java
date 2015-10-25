package nlubej.gains.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nlubej.gains.R;

/**
 * Created by nlubej on 25.10.2015.
 */
public class ExerciseTypeAdapter extends ArrayAdapter<String>
{
    private Context c;
    private String[] exerciseTypes;

    public ExerciseTypeAdapter(Context ctx, int txtViewResourceId, String[] exerciseTypes) {
        super(ctx, txtViewResourceId, exerciseTypes);

        this.exerciseTypes = exerciseTypes;
        this.c = ctx;
    }


    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        View mySpinner = inflater.inflate(R.layout.row_spinner, parent, false);
        TextView main_text = (TextView) mySpinner .findViewById(R.id.spinnerValue);
        main_text.setText(exerciseTypes[position]);

        return mySpinner;
    }
}
