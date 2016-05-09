package nlubej.gains.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.Exercise;

/**
 * Created by nlubej on 28.4.2016.
 */
public class ExerciseAdapter extends BaseAdapter
{
    private ArrayList<ExerciseDto> exerciseDto;
    Context ctx;
    public ExerciseAdapter(Context ctx)
    {
        this.ctx = ctx;

        exerciseDto = new ArrayList();
    }


    @Override
    public int getCount()
    {
        return exerciseDto.size();
    }

    @Override
    public ExerciseDto getItem(int position)
    {
        return exerciseDto.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void AddAll(ArrayList<ExerciseDto> routineDto)
    {
        this.exerciseDto.clear();
        this.exerciseDto.addAll(routineDto);
    }

    public void Add(ExerciseDto row)
    {
        row.Position = exerciseDto.size()+1;

        exerciseDto.add(row);
    }

    public void Remove(ExerciseDto item)
    {
        exerciseDto.remove(item);
    }

    public void Update(ExerciseDto row)
    {
        for(ExerciseDto dto : exerciseDto)
        {
            if(dto.Id == row.Id)
            {
                dto.Type = row.Type;
                dto.Name = row.Name;
                return;
            }
        }
    }

    public boolean Exists(ExerciseDto item)
    {
        for(ExerciseDto dto : exerciseDto)
        {
            if(dto.Id == item.Id)
            {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Integer> GetAllIds()
    {
        ArrayList<Integer> exerciseIds = new ArrayList<>();

        for(ExerciseDto dto : exerciseDto)
        {
            exerciseIds.add(dto.Id);
        }

        return exerciseIds;
    }

    class ExerciseHolder
    {
        TextView name;
        TextView subName;

        public ExerciseHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.name);
            subName = (TextView) v.findViewById(R.id.subName);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        View row = convertView;
        ExerciseHolder routineHolder = null;

        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_program, parent, false);
            routineHolder = new ExerciseHolder(row);
            row.setTag(routineHolder);
        }
        else
        {
            routineHolder = (ExerciseHolder) row.getTag();
        }


        ExerciseDto temp = exerciseDto.get(position);

        routineHolder.name.setText(temp.Name);
        routineHolder.subName.setText(temp.Type.Description);

        return row;
    }
}
