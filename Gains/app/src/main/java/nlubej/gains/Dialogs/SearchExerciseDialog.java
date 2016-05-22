package nlubej.gains.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import nlubej.gains.Adapters.ExerciseAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseType;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.Exercise;
import nlubej.gains.Views.Routine;
import nlubej.gains.interfaces.OnItemChanged;

/**
 * Created by nlubej on 8.5.2016.
 */
public class SearchExerciseDialog  extends DialogFragment implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener
{
    private QueryFactory db;
    private AlertDialog alertDialog;
    private ArrayList<ExerciseDto> exerciseDto;
    private ArrayList<ExerciseDto> filteredExerciseDto;
    private ExerciseAdapter exerciseAdapter;
    private ListView listView;
    Context ctx;
    private Exercise parent;
    private int routineId;
    private ArrayList<Integer> existingExercises;

    public void SetData(Exercise context, QueryFactory database, ArrayList<Integer> existingExer)
    {
        parent = context;
        ctx = context;
        db = database;
        existingExercises = existingExer;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_search_exercise, null);
        SearchView search = (SearchView) view.findViewById(R.id.searchView);
        search.setOnQueryTextListener(this);

        listView = (ListView) view.findViewById(R.id.listView);
        exerciseAdapter = new ExerciseAdapter(getActivity());

        routineId = getArguments().getInt("ROUTINE_ID");
        listView.setOnItemClickListener(this);
        InitData();

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;

    }

    public void InitData()
    {
        filteredExerciseDto = new ArrayList<>();

        db.Open();
        exerciseDto = db.SelectAllExercises(routineId);
        db.Close();

        if(exerciseDto == null)
            return;

        for (int i=0; i<exerciseDto.size(); i++)
        {
            if(existingExercises.contains(exerciseDto.get(i).Id))
            {
                exerciseDto.remove(i);
                i--;
            }
        }

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
            if(dto.Name.toUpperCase().contains(newText.toUpperCase())
                    && !exerciseAdapter.Exists(dto))
            {
                filteredExerciseDto.add(dto);
            }
        }
        exerciseAdapter.AddAll(filteredExerciseDto);
        exerciseAdapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> p, View view, int position, long id)
    {
        ExerciseDto item = exerciseAdapter.getItem((int) id);

        if(!Validate(item))
            return;

        db.Open();
        db.InsertRoutineExerciseConnection(routineId, item.Id);
        db.Close();

        alertDialog.dismiss();
        parent.OnAdded(new ExerciseDto(item.Id, item.Name, item.Type.Id));
    }

    private boolean Validate(ExerciseDto item)
    {
        return true;
    }
}
