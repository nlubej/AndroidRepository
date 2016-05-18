package nlubej.gains.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialCommunityIcons;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;

import java.util.ArrayList;
import nlubej.gains.Adapters.RoutineAdapter;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;

/**
 * Created by nlubej on 19.10.2015.
 */
public class NewWorkout extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnTouchListener
{

    private ListView ListView;
    ImageView btnStartWorkout;
    private RoutineAdapter routineAdapter;
    private QueryFactory db;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Iconify.with(new MaterialCommunityModule());

        View rootView = inflater.inflate(R.layout.view_start_workout, container, false);

        db = new QueryFactory(getActivity());
        ListView = (ListView) rootView.findViewById(R.id.listView);
        btnStartWorkout = (ImageView) rootView.findViewById(R.id.btn_start_workout);
        routineAdapter = new RoutineAdapter(getActivity(), db);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ListView.setAdapter(routineAdapter);
        btnStartWorkout.setOnTouchListener(this);
        btnStartWorkout.setImageDrawable(new IconDrawable(getActivity(), MaterialCommunityIcons.mdi_run).colorRes(R.color.white).sizeDp(110));
        btnStartWorkout.setOnClickListener(this);
        ListView.setOnItemClickListener(this);
        SetData();

        return rootView;
    }


    public void SetData()
    {
        db.Open();
        ArrayList<RoutineDto> routineDto = db.SelectRoutines(prefs.getInt("DEFAULT_PROGRAM", -1));
        db.Close();

        if(routineDto.size() == 0)
        {
            routineDto.add(new RoutineDto(-1,"No routines...", -1));
        }

        routineAdapter.SetSelected(0);
        routineAdapter.AddAll(routineDto);
        routineAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(!routineAdapter.HasValidItems())
            return;

        routineAdapter.SetSelected((int)id);
        routineAdapter.notifyDataSetChanged();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  int id = item.getItemId();

        if (id == R.id.add) {
            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            return true;
        }
        if (id == R.id.add) {
            mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {

    }

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                btnStartWorkout.animate().scaleX(1.7f).scaleY(1.7f).setDuration(450).setInterpolator(DECCELERATE_INTERPOLATOR);
                v.setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < v.getWidth() && y > 0 && y < v.getHeight());
                if (v.isPressed() != isInside) {
                    v.setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                btnStartWorkout.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (v.isPressed()) {
                    v.performClick();
                    v.setPressed(false);
                    StartWorkout();
                }
                break;
        }
        return true;
    }

    private void StartWorkout()
    {
        RoutineDto selectedItem = routineAdapter.GetSelectedItem();
        if(selectedItem == null)
            return;

        Intent i = new Intent(getActivity(), ExerciseLogger.class);
        i.putExtra("ROUTINE_ID", selectedItem.Id);
        i.putExtra("ROUTINE_NAME", selectedItem.Name);

        startActivity(i);
    }
}
