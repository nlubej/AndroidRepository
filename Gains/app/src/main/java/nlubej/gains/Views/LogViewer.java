package nlubej.gains.Views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

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
import nlubej.gains.DataTransferObjects.LoggerViewRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Enums.Constants;
import nlubej.gains.R;

/**
 * Created by nlubej on 19.10.2015.
 */
public class LogViewer extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener, SearchView.OnCloseListener
{
    private QueryFactory db;
    private ArrayList<ExerciseDto> exerciseDto;
    private ArrayList<ExerciseDto> filteredExerciseDto;
    private ExerciseAdapter exerciseAdapter;
    private SharedPreferences prefs;
    Context context;
    private ListView listView;
    SwipeMenuListView swipeListView;
    ArrayList<LoggerViewRowDto> loggedRows;
    private SearchView searchView;
    LoggedWorkoutAdapter loggedWorkoutAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.view_loger_viewer, container, false);
        context = getActivity();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        db = new QueryFactory(context);
        exerciseAdapter = new ExerciseAdapter(getActivity());
        loggedWorkoutAdapter = new LoggedWorkoutAdapter(this, db);

        InitData();

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem defaultProgram = new SwipeMenuItem(view.getContext());
                defaultProgram.setWidth(100);
                defaultProgram.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_star).actionBarSize().colorRes(R.color.PrimaryColor).actionBarSize());
                menu.addMenuItem(defaultProgram);

                SwipeMenuItem editProgram = new SwipeMenuItem(view.getContext());
                editProgram.setWidth(100);
                editProgram.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.gray).actionBarSize());
                menu.addMenuItem(editProgram);

                SwipeMenuItem deleteItem = new SwipeMenuItem(view.getContext());
                deleteItem.setWidth(100);
                deleteItem.setIcon(new IconDrawable(view.getContext(), Iconify.IconValue.zmdi_delete).actionBarSize().colorRes(R.color.red).actionBarSize());
                menu.addMenuItem(deleteItem);
            }
        };

        swipeListView.setAdapter(loggedWorkoutAdapter);
        swipeListView.setMenuCreator(creator);

        return view;
    }

    public void InitData()
    {
        filteredExerciseDto = new ArrayList<>();

        db.Open();
        exerciseDto = db.SelectExercisesByProgram(prefs.getInt(Constants.DEFAULT_PROGRAM_KEY, -1));
        db.Close();

        if(exerciseDto == null)
            return;

        filteredExerciseDto.addAll(exerciseDto);
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();
        listView.setAdapter(exerciseAdapter);
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if(newText == "")
            return true;

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
        searchView.setQuery(filteredExerciseDto.get((int) id).Name, true);
        filteredExerciseDto.clear();
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();

        SetUpLogData(filteredExerciseDto.get((int) id).Id);
    }

    private void SetUpLogData(int exerciseId)
    {
        db.Open();
        ArrayList<LoggerViewRowDto> loggerRows = db.SelectLoggedWorkouts(exerciseId);
        db.Close();

        loggedWorkoutAdapter.AddAll(loggerRows);
        loggedWorkoutAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onClose()
    {
        filteredExerciseDto.addAll(exerciseDto);
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();
        return false;
    }
}
