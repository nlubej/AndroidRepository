package nlubej.gains.Views;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import nlubej.gains.Adapters.LoggerAdapter;
import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseLoggerRow;
import nlubej.gains.DataTransferObjects.LoggedRowDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.Dialogs.ConfirmationDialog;
import nlubej.gains.Dialogs.IncrementChooserDialog;
import nlubej.gains.Dialogs.PopupDialog;
import nlubej.gains.Dialogs.PreviousWorkoutsDialog;
import nlubej.gains.Dialogs.StopwatchDialog;
import nlubej.gains.Enums.Constants;
import nlubej.gains.R;
import nlubej.gains.Tools;
import nlubej.gains.interfaces.OnCopyLog;
import nlubej.gains.interfaces.OnItemChanged;
import nlubej.gains.interfaces.OnNoteChanged;
import nlubej.gains.interfaces.OnSelection;

public class ExerciseLogger extends AppCompatActivity implements SwipeMenuListView.OnMenuItemClickListener, OnClickListener, AdapterView.OnItemClickListener, OnNoteChanged<ExerciseLoggerRow>, OnSelection<Double>, OnCopyLog<ArrayList<ExerciseLoggerRow>>, OnItemChanged<ExerciseDto>
{
    private Context context;
    private QueryFactory db;
    LoggerAdapter loggerAdapter;
    SwipeMenuListView swipeListView;
    private SharedPreferences prefs;
    LinearLayout headerLayout;
    private View fragment;
    ArrayList<ArrayList<ExerciseLoggerRow>> exerciseLogs;
    int currentExerciseLogNumber = 0;
    private ImageView previousExercise;
    private ImageView nextExercise;
    private EditText editReps;
    private EditText editWeight;
    private int routineId;
    private ArrayList<ExerciseDto> exerciseCollection;
    private TextView exerciseName;
    private Button addNew;
    private BigDecimal incrementWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_logger);

        Intent intent = getIntent();
        LayoutInflater inflater = getLayoutInflater();

        String routineName = intent.getStringExtra("ROUTINE_NAME");
        routineId = intent.getIntExtra("ROUTINE_ID", -1);

        setTitle(routineName);

        fragment = inflater.inflate(R.layout.view_logger_header_row, null, false);
        headerLayout = (LinearLayout) fragment.findViewById(R.id.header2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitComponents();
        SetData();

        previousExercise.setEnabled(false);
        if(exerciseCollection.size() == 1)
        {
            nextExercise.setEnabled(false);
        }
    }

    private void InitComponents()
    {
        context = getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);

        db = new QueryFactory(context);
        loggerAdapter = new LoggerAdapter(this, db);
        incrementWeight = BigDecimal.valueOf(Tools.ToDouble(prefs.getString(Constants.WEIGHT_INCREMENT_KEY, "1"), 2));
        ImageView repsPlus = (ImageView) fragment.findViewById(R.id.repsPlus);
        ImageView repsMinus = (ImageView) fragment.findViewById(R.id.repsMinus);
        ImageView weightPlus = (ImageView) fragment.findViewById(R.id.weightPlus);
        ImageView weightMinus = (ImageView) fragment.findViewById(R.id.weightMinus);
        exerciseName = (TextView) fragment.findViewById(R.id.exerciseName);
        editReps = (EditText) fragment.findViewById(R.id.editReps);
        editWeight = (EditText) fragment.findViewById(R.id.editWeight);
        addNew = (Button) fragment.findViewById(R.id.addNew);
        previousExercise = (ImageView) findViewById(R.id.previousExercise);
        nextExercise = (ImageView) findViewById(R.id.nextExercise);
        headerLayout.setVisibility(View.INVISIBLE);


        addNew.setOnClickListener(this);
        repsPlus.setOnClickListener(this);
        repsMinus.setOnClickListener(this);
        weightPlus.setOnClickListener(this);
        weightMinus.setOnClickListener(this);

        repsPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        repsMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightPlus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_plus).colorRes(R.color.PrimaryColor).sizeDp(40));
        weightMinus.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_minus).colorRes(R.color.PrimaryColor).sizeDp(40));
        previousExercise.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_chevron_left).colorRes(R.color.white).sizeDp(50));
        nextExercise.setImageDrawable(new IconDrawable(context, Iconify.IconValue.zmdi_chevron_right).colorRes(R.color.white).sizeDp(50));


        swipeListView.setOnMenuItemClickListener(this);
        previousExercise.setOnClickListener(this);
        nextExercise.setOnClickListener(this);
        swipeListView.setOnItemClickListener(this);
        swipeListView.addHeaderView(fragment);
        SwipeMenuCreator creator = new SwipeMenuCreator()
        {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem editProgram = new SwipeMenuItem(context);
                editProgram.setWidth(120);
                editProgram.setIcon(new IconDrawable(context, Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.PrimaryColor).sizeDp(30));
                menu.addMenuItem(editProgram);

                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                deleteItem.setWidth(120);
                deleteItem.setIcon(new IconDrawable(context, Iconify.IconValue.zmdi_delete).actionBarSize().colorRes(R.color.red).sizeDp(30));
                menu.addMenuItem(deleteItem);
            }
        };

        swipeListView.setAdapter(loggerAdapter);
        swipeListView.setMenuCreator(creator);
    }

    public void SetData()
    {
        db.Open();
        exerciseCollection = db.SelectExercises(routineId);


        exerciseLogs = new ArrayList<>();
        for (int i = 0; i < exerciseCollection.size(); i++)
        {
            exerciseLogs.add(new ArrayList<ExerciseLoggerRow>());
            exerciseCollection.get(i).WorkoutNumber = db.GetNextWorkoutNumber(exerciseCollection.get(i).Id);
        }

        db.Close();

        RefreshAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_exercise_logger, menu);
        menu.findItem(R.id.more).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_more_vert).colorRes(R.color.white).actionBarSize());
        menu.findItem(R.id.increment).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_tune).colorRes(R.color.white).actionBarSize());
        //menu.findItem(R.id.finish).setIcon(new IconDrawable(this, Iconify.IconValue.zmdi_check_all).colorRes(R.color.colorAccent).sizeDp(30));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.more:
                ShowPopup();
                break;

            case R.id.increment:
                IncrementChooserDialog dialog = new IncrementChooserDialog();
                dialog.SetData(this);
                dialog.show(getFragmentManager(),"");
                break;

            case R.id.finish:
                ConfirmationDialog confDialog = new ConfirmationDialog();
                confDialog.SetData(this);
                confDialog.show(getFragmentManager(), "");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ShowPopup(){

        PopupDialog dialog = new PopupDialog();
        dialog.SetData(this, db, exerciseCollection, currentExerciseLogNumber);
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onClick(View v)
    {
        if(incrementWeight.intValue() == 0)
        {
            incrementWeight = BigDecimal.valueOf(Tools.ToDouble(prefs.getString(Constants.WEIGHT_INCREMENT_KEY, "1"), 2));
        }

        int val;
        BigDecimal dval;
        switch (v.getId())
        {
            case R.id.addNew:
                if(GetTagValue(addNew) >= 0) // position is in the tag
                {
                    ExerciseLoggerRow row = exerciseLogs.get(currentExerciseLogNumber).get(GetTagValue(addNew));

                    row.Weight = editWeight.getText().toString();
                    row.Rep = editReps.getText().toString();

                    db.Open();
                    db.UpdateTable("LOGGED_WORKOUT", String.format("LOGGED_WORKOUT_ID = %s", row.LoggedWorkoutId), new String[]{"LOGGED_WEIGHT", row.Weight, "LOGGED_REP", row.Rep});
                    db.Close();

                    loggerAdapter.Update(row);

                    loggerAdapter.MarkForEdit(row.LoggedWorkoutId, false);
                    addNew.setTag(-1);
                    addNew.setText("Add");
                }
                else //add new
                {
                    headerLayout.setVisibility(View.VISIBLE);

                    ExerciseLoggerRow dto = new ExerciseLoggerRow(loggerAdapter.getCount() + 1, editReps.getText().toString(), editWeight.getText().toString());
                    int logId = AddExercise(dto);
                    dto.LoggedWorkoutId = logId;

                    if (logId <= 0)
                        return;

                    exerciseLogs.get(currentExerciseLogNumber).add(dto);
                    loggerAdapter.Add(dto);
                }


                loggerAdapter.notifyDataSetChanged();
                break;

            case R.id.nextExercise:
                currentExerciseLogNumber++;
                RefreshAdapter();

                previousExercise.setEnabled(true);

                if (currentExerciseLogNumber == exerciseCollection.size() - 1)
                    nextExercise.setEnabled(false);


                if (loggerAdapter.getCount() == 0)
                    headerLayout.setVisibility(View.INVISIBLE);
                else
                    headerLayout.setVisibility(View.VISIBLE);

                addNew.setTag(-1);
                addNew.setText("Add");
                break;

            case R.id.previousExercise:
                currentExerciseLogNumber--;
                RefreshAdapter();

                nextExercise.setEnabled(true);

                if (currentExerciseLogNumber == 0)
                    previousExercise.setEnabled(false);


                if (loggerAdapter.getCount() == 0)
                    headerLayout.setVisibility(View.INVISIBLE);
                else
                    headerLayout.setVisibility(View.VISIBLE);

                addNew.setTag(-1);
                addNew.setText("Add");
                break;

            case R.id.repsPlus:
                val = Tools.ToInt(editReps.getText().toString()) + 1;
                editReps.setText(String.valueOf(val));
                break;
            case R.id.repsMinus:
                val = Tools.ToInt(editReps.getText().toString()) - 1;
                editReps.setText(String.valueOf(val));
                break;
            case R.id.weightPlus:
                Increment();
                break;
            case R.id.weightMinus:
                Decrement();
                break;
        }
    }

    private int GetTagValue(Button addNew)
    {
        if(addNew.getTag() == null)
            return -1;
        else
            return (int)addNew.getTag();
    }

    private int AddExercise(ExerciseLoggerRow dto)
    {
        db.Open();
        int logId = db.InsertExerciseLog(exerciseCollection.get(currentExerciseLogNumber).RoutineExerciseId, exerciseCollection.get(currentExerciseLogNumber).WorkoutNumber, dto.Set, dto.Rep, dto.Weight);
        db.Close();

        return logId;
    }

    private void RefreshAdapter()
    {
        exerciseName.setText(exerciseCollection.get(currentExerciseLogNumber).Name);

        loggerAdapter.AddAll(exerciseLogs.get(currentExerciseLogNumber));
        loggerAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
    {
        ExerciseLoggerRow row = loggerAdapter.getItem(position);
        switch (index) {
            case 0: //edit
                addNew.setText("Update");

                editWeight.setText(row.Weight);
                editReps.setText(row.Rep);

                loggerAdapter.MarkForEdit(row.LoggedWorkoutId, true);
                loggerAdapter.notifyDataSetChanged();
                swipeListView.setAdapter(loggerAdapter);
                addNew.setTag(position);
                break;
            case 1: //delete

                DeleteRow(String.valueOf(row.LoggedWorkoutId));

                RefreshAfterDeletion(row, position);

                break;
        }

        return false;
    }

    private void RefreshAfterDeletion(ExerciseLoggerRow row, int position)
    {
        exerciseLogs.get(currentExerciseLogNumber).remove(position);
        loggerAdapter.Remove(row);
        loggerAdapter.UpdateWorkoutSetNumbers();

        db.Open();
        db.UpdateWorkoutSetNumbers(loggerAdapter.GetIdsInAscOrder());
        db.Close();

        if(loggerAdapter.getCount() == 0)
        {
            headerLayout.setVisibility(View.INVISIBLE);
        }

        swipeListView.setAdapter(loggerAdapter);
        loggerAdapter.notifyDataSetChanged();

        if(loggerAdapter.getCount() > 0)
        {
            swipeListView.setSelection(position - 1);
        }
    }

    private void DeleteRow(String loggedWorkoutId)
    {
        db.Open();
        db.DeleteRecord("LOGGED_WORKOUT", "LOGGED_WORKOUT_ID = ?", new String[]{loggedWorkoutId});
        db.Close();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public void OnNoteAdded(ExerciseLoggerRow row)
    {
        loggerAdapter.UpdateNote(row);
    }

    @Override
    public void OnNoteUpdated(ExerciseLoggerRow row)
    {
        loggerAdapter.UpdateNote(row);
    }

    @Override
    public void OnNoteRemoved(ExerciseLoggerRow row)
    {
        loggerAdapter.RemoveNote(row);
    }

    @Override
    public void OnSelection(Double newIncrement)
    {
        prefs.edit().putString(Constants.WEIGHT_INCREMENT_KEY, String.valueOf(newIncrement)).apply();
        incrementWeight = BigDecimal.valueOf(newIncrement);
    }

    public void Increment(){
        BigDecimal dval = BigDecimal.valueOf(Tools.ToDouble(editWeight.getText().toString(), 2));
        dval = dval.add(incrementWeight);
        editWeight.setText(String.valueOf(dval));
    }

    public void Decrement(){
        BigDecimal dval = BigDecimal.valueOf(Tools.ToDouble(editWeight.getText().toString(), 2));
        dval = dval.subtract(incrementWeight);
        editWeight.setText(String.valueOf(dval));
    }

    @Override
    public void OnCopy(ArrayList<ExerciseLoggerRow> rows)
    {
        for(int i= 0; i<exerciseLogs.get(currentExerciseLogNumber).size(); i++)
        {
            ExerciseLoggerRow row = exerciseLogs.get(currentExerciseLogNumber).get(i);
            DeleteRow(String.valueOf(row.LoggedWorkoutId));

            RefreshAfterDeletion(row, i);
            i--;  //decrement index because item was deleted
        }

        exerciseLogs.get(currentExerciseLogNumber).addAll(rows);
        loggerAdapter.AddAll(rows);
        loggerAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnAdded(ExerciseDto row)
    {
        nextExercise.setEnabled(true);

        db.Open();
        row.WorkoutNumber = db.GetNextWorkoutNumber(row.Id);
        db.Close();

        exerciseCollection.add(row);
        exerciseLogs.add(new ArrayList<ExerciseLoggerRow>());
    }

    @Override
    public void OnUpdated(ExerciseDto row)
    {
    }

    @Override
    public void OnRemoved(ExerciseDto row)
    {
    }
}