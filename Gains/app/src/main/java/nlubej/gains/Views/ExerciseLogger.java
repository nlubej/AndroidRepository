package nlubej.gains.Views;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.melnykov.fab.FloatingActionButton;

import nlubej.gains.Adapters.LoggerAdapter;
import nlubej.gains.Adapters.OnSwipeTouchListener;
import nlubej.gains.Adapters.ProgramAdapter;
import nlubej.gains.Adapters.RoutineAdapter;
import nlubej.gains.DataTransferObjects.LoggerRowDto;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddProgramDialog;
import nlubej.gains.Dialogs.EditProgramDialog;
import nlubej.gains.Dialogs.EditRoutineDialog;
import nlubej.gains.R;
import nlubej.gains.interfaces.*;

public class ExerciseLogger extends Fragment implements OnItemClickListener, OnItemChanged<LoggerRowDto>, OnClickListener, SwipeMenuListView.OnMenuItemClickListener
{
    private Context context;
    private QueryFactory db;
    LoggerAdapter loggerAdapter;
    SwipeMenuListView swipeListView;
    private SharedPreferences prefs;
    RelativeLayout mainLayout;
    static final int UPDATE_ACTIVITY_RESULT = 1;
    static final int RESULT_OK = 1;
    private View fragment2  ;
    ArrayList<ArrayList<LoggerRowDto>> logs;
    int currentLog = 0;
    private GestureDetector gesture;
    private ImageView previousExercise;
    private ImageView nextExercise;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        logs = new ArrayList<>();
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());

        View fragment = inflater.inflate(R.layout.view_logger, container, false);
        fragment2 = inflater.inflate(R.layout.fragment_main_test, null, false);
        mainLayout = (RelativeLayout)fragment2.findViewById(R.id.rlayout);

        InitComponents(fragment);
        SetData();

        this.setHasOptionsMenu(true);
        return fragment;
    }

    private void InitComponents(final View fragment)
    {
        context = fragment.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        swipeListView = (SwipeMenuListView) fragment.findViewById(R.id.swipeListView);

        loggerAdapter = new LoggerAdapter(this, db);
        db = new QueryFactory(context);

        ImageView repsPlus = (ImageView)fragment2.findViewById(R.id.repsPlus);
        ImageView repsMinus = (ImageView)fragment2.findViewById(R.id.repsMinus);
        ImageView weightPlus = (ImageView)fragment2.findViewById(R.id.weightPlus);
        ImageView weightMinus = (ImageView)fragment2.findViewById(R.id.weightMinus);
        Button addNew = (Button)fragment2.findViewById(R.id.addNew);
        previousExercise = (ImageView) fragment.findViewById(R.id.previousExercise);
        nextExercise = (ImageView)fragment.findViewById(R.id.nextExercise);


        if(currentLog == 0)
        {
            previousExercise.setEnabled(false);
        }


        addNew.setOnClickListener(this);
         gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        Log.i("nlubej", "onFling has been called!");
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                            {
                                currentLog++;
                                Toast.makeText(context,"Next Exercise  " + currentLog, Toast.LENGTH_SHORT).show();
                                loggerAdapter.AddAll(logs.get(currentLog));
                                loggerAdapter.notifyDataSetChanged();
                            }
                            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                            {
                                currentLog--;
                                Toast.makeText(context, "Previous Exercise " + currentLog, Toast.LENGTH_SHORT).show();
                                loggerAdapter.AddAll(logs.get(currentLog));
                                loggerAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        mainLayout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gesture.onTouchEvent(event);
            }
        });

        fragment2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gesture.onTouchEvent(event);
            }
        });


        repsPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        repsMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));
        previousExercise.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_chevron_left).colorRes(R.color.white).sizeDp(50));
        nextExercise.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_chevron_right).colorRes(R.color.white).sizeDp(50));


        swipeListView.setOnItemClickListener(this);
        swipeListView.setOnMenuItemClickListener(this);
        previousExercise.setOnClickListener(this);
        nextExercise.setOnClickListener(this);

        swipeListView.addHeaderView(fragment2);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem defaultProgram = new SwipeMenuItem(fragment.getContext());
                defaultProgram.setWidth(100);
                defaultProgram.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_star).actionBarSize().colorRes(R.color.PrimaryColor).actionBarSize());
                menu.addMenuItem(defaultProgram);

                SwipeMenuItem editProgram = new SwipeMenuItem(fragment.getContext());
                editProgram.setWidth(100);
                editProgram.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.gray).actionBarSize());
                menu.addMenuItem(editProgram);

                SwipeMenuItem deleteItem = new SwipeMenuItem(fragment.getContext());
                deleteItem.setWidth(100);
                deleteItem.setIcon(new IconDrawable(fragment.getContext(), Iconify.IconValue.zmdi_delete).actionBarSize().colorRes(R.color.red).actionBarSize());
                menu.addMenuItem(deleteItem);
            }
        };

        swipeListView.setAdapter(loggerAdapter);
        swipeListView.setMenuCreator(creator);
    }

    public void SetData()
    {
        db.Open();
        ArrayList<ProgramDto> programDto = db.SelectPrograms();
        db.Close();

       // programAdapter.AddAll(programDto);
        loggerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(context, Routine.class);
        i.putExtra("PROGRAM_NAME", loggerAdapter.getItem((int) id).Name);
        i.putExtra("PROGRAM_ID", loggerAdapter.getItem((int) id).Id);

        startActivityForResult(i, UPDATE_ACTIVITY_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATE_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                int id = data.getIntExtra("PROGRAM_ID",0);
                int count = data.getIntExtra("ROUTINE_COUNT",0);

                loggerAdapter.UpdateRoutineCount(id, count);
                loggerAdapter.notifyDataSetChanged();
            }
        }
    }

/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_program, menu);
    }
*/
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == android.R.id.home)
        {
        }
        return true;
    }*/


    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.addNew:
                LoggerRowDto dto = new LoggerRowDto(1,"12", 2);
                logs.get(currentLog).add(dto);
                loggerAdapter.Add(dto);
                loggerAdapter.notifyDataSetChanged();
                break;

            case R.id.nextExercise:
                currentLog++;
                Log.i("nlubej", "Next Exercise  " + currentLog);
                loggerAdapter.AddAll(logs.get(currentLog));
                loggerAdapter.notifyDataSetChanged();

                previousExercise.setEnabled(true);
                if(currentLog == 3)
                {
                    nextExercise.setEnabled(false);
                }
                break;

            case R.id.previousExercise:
                currentLog--;
                Log.i("nlubej", "Previous Exercise  " + currentLog);
                loggerAdapter.AddAll(logs.get(currentLog));
                loggerAdapter.notifyDataSetChanged();

                nextExercise.setEnabled(true);
                if(currentLog == 0)
                {
                    previousExercise.setEnabled(false);
                }

                break;
        }

        /*AddProgramDialog addDialog = new AddProgramDialog();
        addDialog.SetData(ExerciseLogger.this, db);
        addDialog.show(ExerciseLogger.this.getActivity().getFragmentManager(), "");*/
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {/*
        ProgramDto item = programAdapter.getItem(position);
        switch (index) {
            case 0: //mark
                ProgramAdapter.DefaultProgram = item.Id;
                prefs.edit().putInt("DEFAULT_PROGRAM", item.Id).apply();
                programAdapter.notifyDataSetChanged();
                swipeListView.setAdapter(programAdapter);
                break;
            case 1: //edit
                EditProgramDialog editExercise = new EditProgramDialog();
                editExercise.SetData(Program.this, db);
                Bundle b = new Bundle();
                b.putInt("PROGRAM_ID", item.Id);
                b.putString("PROGRAM_NAME", item.Name);
                editExercise.setArguments(b);

                editExercise.show(getFragmentManager(), "");
                break;
            case 2: //delete
                db.Open();
                db.DeleteProgram(item.Id);
                db.Close();

                programAdapter.Remove(item);
                programAdapter.notifyDataSetChanged();
                swipeListView.setAdapter(programAdapter);

                break;
        }
*/
        return false;
    }

    @Override
    public void OnAdded(LoggerRowDto row)
    {
        loggerAdapter.Add(row);
        loggerAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUpdated(LoggerRowDto row)
    {
        loggerAdapter.Update(row);
        loggerAdapter.notifyDataSetChanged();
    }
}