package nlubej.gains.Dialogs;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
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
public class StopwatchDialog extends DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener
{
    Chronometer chrono;
    private AlertDialog alertDialog;
    Context ctx;
    private ExerciseLogger parent;
    private SharedPreferences prefs;
    private boolean isChronometerRunning = false;
    long timeWhenStoped;

    String[] incrementValues = new String[]
            {
                    "0.15",
                    "0.25",
                    "0.50",
                    "1",
                    "1.5",
                    "2"
            };
    private Button start;
    private Button stopResume;
    private Button reset;

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
        View view = inflater.inflate(R.layout.dialog_stopwatch, null);
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        chrono = (Chronometer)view.findViewById(R.id.chronometer);

        start = (Button) view.findViewById(R.id.start);
        stopResume = (Button) view.findViewById(R.id.stop_resume);
        reset = (Button) view.findViewById(R.id.reset);

       // Button exit = (Button) view.findViewById(R.id.btn_exit);

        start.setOnClickListener(this);
        stopResume.setOnClickListener(this);
        reset.setOnClickListener(this);
        //exit.setOnClickListener(this);

        start.setVisibility(View.VISIBLE);
        stopResume.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);

        chrono.setText("00:00:00");

        chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
        {
            @Override
            public void onChronometerTick(Chronometer cArg)
            {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                cArg.setText(hh + ":" + mm + ":" + ss);
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    @Override
    public void onItemClick(AdapterView<?> p, View view, int position, long id)
    {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start:
                start.setVisibility(View.INVISIBLE);
                stopResume.setVisibility(View.VISIBLE);
                reset.setVisibility(View.VISIBLE);
                chrono.setBase(SystemClock.elapsedRealtime());
                chrono.start();
                isChronometerRunning = true;
                break;
            case R.id.stop_resume:
                if(isChronometerRunning)
                {
                    chrono.stop();
                    stopResume.setText("RESUME");
                    timeWhenStoped = SystemClock.elapsedRealtime() - chrono.getBase();
                    isChronometerRunning = false;
                    stopResume.setBackgroundResource(R.drawable.button_effect_blue);
                }
                else
                {
                    stopResume.setText("STOP");
                    chrono.setBase(SystemClock.elapsedRealtime() - timeWhenStoped);
                    chrono.start();
                    isChronometerRunning = true;
                    stopResume.setBackgroundResource(R.drawable.button_effect_red);
        }

                break;
            case R.id.reset:
                start.setVisibility(View.VISIBLE);
                stopResume.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
                chrono.stop();
                timeWhenStoped = 0;
                chrono.setText("00:00:00");
                break;

           // case R.id.btn_exit:
           //    alertDialog.dismiss();
           //     break;
        }
    }
}
