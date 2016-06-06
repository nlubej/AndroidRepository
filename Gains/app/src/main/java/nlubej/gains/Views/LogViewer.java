package nlubej.gains.Views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.Adapters.ExerciseAdapter;
import nlubej.gains.Adapters.LoggedWorkoutAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.DataTransferObjects.LoggedViewRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.AddRoutineDialog;
import nlubej.gains.Dialogs.UpdateLoggedWorkoutDialog;
import nlubej.gains.Enums.Constants;
import nlubej.gains.R;
import nlubej.gains.interfaces.OnBackPressedListener;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 19.10.2015.
 */
public class LogViewer extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener , OnItemChanged<LoggedViewRowDto>, SwipeMenuListView.OnMenuItemClickListener, View.OnFocusChangeListener, OnBackPressedListener
{
    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private ArrayList<ExerciseDto> filteredExerciseDto;
    private ExerciseAdapter exerciseAdapter;
    private SharedPreferences prefs;
    Context context;
    SwipeMenuListView swipeListView;
    private SearchView searchView;
    LoggedWorkoutAdapter loggedWorkoutAdapter;
    LinearLayout header;
    private ExerciseDto selectedExercise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.view_loger_viewer, container, false);
        context = getActivity();

        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextFocusChangeListener(this);

        swipeListView = (SwipeMenuListView) view.findViewById(R.id.swipeListView);
        swipeListView.setOnItemClickListener(this);

        header = (LinearLayout) view.findViewById(R.id.header2);
        header.setVisibility(View.GONE);

        db = new QueryFactory(context);
        exerciseAdapter = new ExerciseAdapter(getActivity());
        loggedWorkoutAdapter = new LoggedWorkoutAdapter(this, db);

        InitData();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                switch (menu.getViewType())
                {
                    case 0:
                        SwipeMenuItem showDetails = new SwipeMenuItem(view.getContext());
                        showDetails.setWidth(120);
                        showDetails.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_info_outline).actionBarSize().colorRes(R.color.gray).sizeDp(30));
                        menu.addMenuItem(showDetails);

                    case 1:
                        SwipeMenuItem editProgram = new SwipeMenuItem(view.getContext());
                        editProgram.setWidth(120);
                        editProgram.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.PrimaryColor).sizeDp(30));
                        menu.addMenuItem(editProgram);

                        SwipeMenuItem deleteItem = new SwipeMenuItem(view.getContext());
                        deleteItem.setWidth(120);
                        deleteItem.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_delete).actionBarSize().colorRes(R.color.red).sizeDp(30));
                        menu.addMenuItem(deleteItem);

                }
            }
        };

        swipeListView.setMenuCreator(creator);
        swipeListView.setOnMenuItemClickListener(this);

        searchView.clearFocus();
        searchView.setOnFocusChangeListener(this);
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        swipeListView.requestFocus();

        return view;
    }

    public void InitData()
    {
        filteredExerciseDto = new ArrayList<>();

        db.Open();
        exerciseDto = db.SelectExercisesByProgram(prefs.getInt(Constants.DEFAULT_PROGRAM_KEY, -1));
        db.Close();

        if(exerciseDto == null)
        {
            header.setVisibility(View.GONE);
            return;
        }

        swipeListView.setAdapter(exerciseAdapter);
        filteredExerciseDto.addAll(exerciseDto);
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if(newText.compareTo("") == 0)
        {
            exerciseAdapter.AddAll(exerciseDto);
            filteredExerciseDto.addAll(exerciseDto);
            exerciseAdapter.notifyDataSetChanged();
            swipeListView.setAdapter(exerciseAdapter);

            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

            header.setVisibility(View.GONE);
            return true;
        }

        filteredExerciseDto.clear();
        for(ExerciseDto dto : exerciseDto)
        {
            if(dto.Name.toUpperCase().contains(newText.toUpperCase()))
            {
                filteredExerciseDto.add(dto);
            }
        }
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(filteredExerciseDto.size() == 0)
            return;

        selectedExercise = filteredExerciseDto.get((int) id);
        if(filteredExerciseDto.size() <= id)
            return;

        SetUpLogData(selectedExercise.Id);
        searchView.setQuery(selectedExercise.Name, true);

        filteredExerciseDto.clear();
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();
    }

    private void SetUpLogData(int exerciseId)
    {
        db.Open();
        ArrayList<LoggedViewRowDto> loggerRows = db.SelectLoggedWorkouts(exerciseId);
        db.Close();

        loggedWorkoutAdapter.AddAll(loggerRows);
        loggedWorkoutAdapter.notifyDataSetChanged();
        swipeListView.setAdapter(loggedWorkoutAdapter);
        if(loggerRows.size() > 0)
        {
            header.setVisibility(View.VISIBLE);
        }
        else
        {
            header.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        if(menu.getViewType() == 0)
        {

        }
        else
        {
            LoggedViewRowDto row = loggedWorkoutAdapter.getItem(position);
            switch (index)
            {
                case 0: //edit

                    UpdateLoggedWorkoutDialog addDialog = new UpdateLoggedWorkoutDialog();
                    addDialog.SetData(LogViewer.this, db);
                    Bundle b = new Bundle();
                    b.putString("EXERCISE_NAME", selectedExercise.Name);
                    b.putInt("LOGGED_WORKOUT_ID", row.LoggedWorkoutId);
                    b.putString("REP", row.Rep);
                    b.putString("WEIGHT", row.Weight);
                    addDialog.setArguments(b);
                    addDialog.show(getFragmentManager(), "");

                    break;
                case 1: //delete
                    db.Open();
                    boolean isDeleted = db.DeleteRecord("LOGGED_WORKOUT", "LOGGED_WORKOUT_ID = ?", new String[]{String.valueOf(row.LoggedWorkoutId)});
                    db.UpdateWorkoutSetNumbers(loggedWorkoutAdapter.GetIdsInAscOrder(row.WorkoutNumber));
                    db.Close();

                    if (!isDeleted)
                        break;

                    loggedWorkoutAdapter.Remove(row);
                    if (loggedWorkoutAdapter.CanRemoveSummary(row.WorkoutNumber))
                    {
                        //remove summary
                        loggedWorkoutAdapter.Remove(loggedWorkoutAdapter.getItem(position - 1));
                        loggedWorkoutAdapter.UpdateFollowingWorkoutNumber(row.WorkoutNumber);
                        //update workout numbers
                        db.Open();
                        db.UpdateFollowingWorkoutNumber(row.WorkoutNumber);
                        db.Close();

                    }
                    loggedWorkoutAdapter.UpdateWorkoutSetNumbers(row.WorkoutNumber);
                    if (loggedWorkoutAdapter.getCount() == 0)
                    {
                        header.setVisibility(View.INVISIBLE);
                    }

                    swipeListView.setAdapter(loggedWorkoutAdapter);
                    loggedWorkoutAdapter.notifyDataSetChanged();
                    if (loggedWorkoutAdapter.getCount() > 0)
                    {
                        swipeListView.setSelection(position - 1);
                    }

                    break;
            }

        }
        return false;
    }

    @Override
    public void OnAdded(LoggedViewRowDto row)
    {
        loggedWorkoutAdapter.UpdateNote(row);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnUpdated(LoggedViewRowDto row)
    {
        loggedWorkoutAdapter.UpdateNote(row);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnRemoved(LoggedViewRowDto row)
    {
        loggedWorkoutAdapter.RemoveNote(row);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void doBack()
    {
        Toast.makeText(getActivity(), "Click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                if ((getActivity()) != null)
                    ((MainActivity) getActivity()).setOnBackPressedListener(LogViewer.this);
            }
        }, 2000);

        ((MainActivity) getActivity()).setOnBackPressedListener(null);
    }
}
