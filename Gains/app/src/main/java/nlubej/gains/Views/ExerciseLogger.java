package nlubej.gains.Views;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import nlubej.gains.Adapters.LoggerAdapter;
import nlubej.gains.DataTransferObjects.LoggerRowDto;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;

public class ExerciseLogger extends AppCompatActivity implements SwipeMenuListView.OnMenuItemClickListener, OnClickListener
{
    private Context context;
    private QueryFactory db;
    LoggerAdapter loggerAdapter;
    SwipeMenuListView swipeListView;
    private SharedPreferences prefs;
    LinearLayout headerLayout;
    private View fragment;
    ArrayList<ArrayList<LoggerRowDto>> logs;
    int currentLog = 0;
    private ImageView previousExercise;
    private ImageView nextExercise;
    private EditText editReps;
    private EditText editWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_logger);

        LayoutInflater inflater = getLayoutInflater();

        logs = new ArrayList<>();
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());
        logs.add(new ArrayList<LoggerRowDto>());

        fragment = inflater.inflate(R.layout.view_logger_header_row, null, false);
        headerLayout = (LinearLayout) fragment.findViewById(R.id.header2);

        InitComponents();
        SetData();
    }

    private void InitComponents()
    {
        context = getApplication();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        swipeListView = (SwipeMenuListView) findViewById(R.id.swipeListView);

        loggerAdapter = new LoggerAdapter(this, db);
        db = new QueryFactory(context);

        ImageView repsPlus = (ImageView) fragment.findViewById(R.id.repsPlus);
        ImageView repsMinus = (ImageView) fragment.findViewById(R.id.repsMinus);
        ImageView weightPlus = (ImageView) fragment.findViewById(R.id.weightPlus);
        ImageView weightMinus = (ImageView) fragment.findViewById(R.id.weightMinus);
        editReps = (EditText) fragment.findViewById(R.id.editReps);
        editWeight = (EditText) fragment.findViewById(R.id.editWeight);
        Button addNew = (Button) fragment.findViewById(R.id.addNew);
        previousExercise = (ImageView) findViewById(R.id.previousExercise);
        nextExercise = (ImageView)findViewById(R.id.nextExercise);
        headerLayout.setVisibility(View.INVISIBLE);

        if(currentLog == 0)
        {
            previousExercise.setEnabled(false);
        }


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

        swipeListView.addHeaderView(fragment);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu)
            {
                SwipeMenuItem defaultProgram = new SwipeMenuItem(context);
                defaultProgram.setWidth(120);
                defaultProgram.setIcon(new IconDrawable(context, Iconify.IconValue.zmdi_star).actionBarSize().colorRes(R.color.PrimaryColor).sizeDp(30));
                menu.addMenuItem(defaultProgram);

                SwipeMenuItem editProgram = new SwipeMenuItem(context);
                editProgram.setWidth(120);
                editProgram.setIcon(new IconDrawable(context, Iconify.IconValue.zmdi_edit).actionBarSize().colorRes(R.color.gray).sizeDp(30));
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
        ArrayList<ProgramDto> programDto = db.SelectPrograms();
        db.Close();

       // programAdapter.AddAll(programDto);
        loggerAdapter.notifyDataSetChanged();
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
        int val;
        double dval;
        switch(v.getId())
        {
            case R.id.addNew:
                headerLayout.setVisibility(View.VISIBLE);

                LoggerRowDto dto = new LoggerRowDto(loggerAdapter.getCount()+1 , editReps.getText().toString(), editWeight.getText().toString());
                logs.get(currentLog).add(dto);

                loggerAdapter.Add(dto);
                loggerAdapter.notifyDataSetChanged();
                break;

            case R.id.nextExercise:
                currentLog++;

                loggerAdapter.AddAll(logs.get(currentLog));
                loggerAdapter.notifyDataSetChanged();
                previousExercise.setEnabled(true);

                if(currentLog == 3)
                    nextExercise.setEnabled(false);


                if(loggerAdapter.getCount() == 0)
                    headerLayout.setVisibility(View.INVISIBLE);
                else
                    headerLayout.setVisibility(View.VISIBLE);

                break;

            case R.id.previousExercise:
                currentLog--;

                loggerAdapter.AddAll(logs.get(currentLog));
                loggerAdapter.notifyDataSetChanged();

                nextExercise.setEnabled(true);

                if(currentLog == 0)
                    previousExercise.setEnabled(false);


                if(loggerAdapter.getCount() == 0)
                    headerLayout.setVisibility(View.INVISIBLE);
                else
                    headerLayout.setVisibility(View.VISIBLE);

                break;

            case R.id.repsPlus:
                val = ToInt(editReps.getText().toString()) + 1;
                editReps.setText(String.valueOf(val));
               break;
            case R.id.repsMinus:
                val = ToInt(editReps.getText().toString()) - 1;
                editReps.setText(String.valueOf(val));
                break;
            case R.id.weightPlus:
                dval = ToDouble(editWeight.getText().toString()) + 1;
                editWeight.setText(String.valueOf(dval));
                break;
            case R.id.weightMinus:
                dval = ToDouble(editWeight.getText().toString()) - 1;
                editWeight.setText(String.valueOf(dval));
                break;
        }
    }

    private double ToDouble(String value)
    {
        return Double.parseDouble(value);
    }

    private int ToInt(String value)
    {
        return Integer.parseInt(value);
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
}