package nlubej.gains.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nlubej.gains.R;

/**
 * Created by nlubej on 19.10.2015.
 */
public class WorkoutLog extends Fragment{

    public static StartScreen newInstance(String text){
        StartScreen mFragment = new StartScreen();
        Bundle mBundle = new Bundle();
        mBundle.putString("lubej", text);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_start_workout, container, false);

       // TextView mTxtTitle = (TextView) rootView.findViewById(R.id.teeest);
       // mTxtTitle.setText("lubej");
        return rootView;
    }
}
