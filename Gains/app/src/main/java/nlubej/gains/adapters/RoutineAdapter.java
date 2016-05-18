package nlubej.gains.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.QueryFactory;
import nlubej.gains.R;
import nlubej.gains.Views.Routine;

/**
 * Created by nlubej on 28.4.2016.
 */
public class RoutineAdapter extends BaseAdapter
{
    private ArrayList<RoutineDto> routineDto;
    Context ctx;
    private QueryFactory db;

    public RoutineAdapter(Context c, QueryFactory database)
    {
        this.ctx = c;
        db = database;

        routineDto = new ArrayList();
    }


    @Override
    public int getCount()
    {
        return routineDto.size();
    }

    @Override
    public RoutineDto getItem(int position)
    {
        return routineDto.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void AddAll(ArrayList<RoutineDto> routineDto)
    {
        this.routineDto.clear();
        this.routineDto.addAll(routineDto);
    }

    public void Add(RoutineDto row)
    {
        row.Position = routineDto.size()+1;
        row.ExerciseCount = 0;

        routineDto.add(row);
    }

    public void Remove(RoutineDto item)
    {
        routineDto.remove(item);
    }

    public void Update(RoutineDto row)
    {
        for(RoutineDto dto : routineDto)
        {
            if(dto.Id == row.Id)
            {
                dto.Name = row.Name;
                return;
            }
        }
    }

    public void UpdateExerciseCount(int routineId, int exerciseCount)
    {
        for(RoutineDto dto : routineDto)
        {
            if(dto.Id == routineId)
            {
                dto.ExerciseCount = exerciseCount;
                return;
            }
        }
    }

    public void SetSelected(int position)
    {
        for(RoutineDto dto : routineDto)
        {
            dto.IsSelected = false;
        }

        if(routineDto.size() > 0)
        {
            routineDto.get(position).IsSelected = true;
        }
    }

    public boolean HasValidItems()
    {
        for(RoutineDto dto : routineDto)
        {
            if(dto.Id == -1)
                return false;
        }

        return true;
    }

    public RoutineDto GetSelectedItem()
    {
        for(RoutineDto dto : routineDto)
        {
            if(dto.IsSelected)
                return dto;
        }

        return null;
    }

    class RoutineHolder
    {
        TextView name;
        TextView subName;
        ImageView btnSelected;

        public RoutineHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.name);
            subName = (TextView) v.findViewById(R.id.subName);
            btnSelected = (ImageView) v.findViewById(R.id.btnSelected);
        }
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        View row = convertView;
        RoutineHolder routineHolder = null;

        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_program, parent, false);
            routineHolder = new RoutineHolder(row);
            row.setTag(routineHolder);
        }
        else
        {
            routineHolder = (RoutineHolder) row.getTag();
        }


        RoutineDto temp = routineDto.get(position);

        routineHolder.name.setText(temp.Name);
        routineHolder.subName.setText(String.format("%d %s",temp.ExerciseCount, (temp.ExerciseCount == 1) ? "exercise" : "exercises"));
        routineHolder.btnSelected.setVisibility(View.GONE);
        if(temp.IsSelected)
        {
            routineHolder.btnSelected.setVisibility(View.VISIBLE);
            routineHolder.btnSelected.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_check).colorRes(R.color.colorAccent).sizeDp(40));
        }
        //routineHolder.btn.setTag(position);

        /*routineHolder.btn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                PopupMenu popup = new PopupMenu(ctx, v);
                popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        if (item.getItemId() == R.id.delete)
                        {
                            new AlertDialog.Builder(ctx).setTitle("Delete entry").setMessage("Are you sure you want to delete this entry?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    db.Open();
                                    db.DeleteRoutine(routineDto.get(position).Id);
                                    db.Close();
                                }
                            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                }
                            }).show();
                        }
                        else if (item.getItemId() == R.id.edit)
                        {
                            EditDialog editDialog = new EditDialog();
                            Bundle arg = new Bundle();
                            arg.putString("ROUTINE_NAME", routineDto.get(position).Name);
                            arg.putInt("ROUTINE_ID", routineDto.get(position).Id);
                            editDialog.setArguments(arg);
                            editDialog.SetData(parentClass, AddDialogType.Routine);
                            editDialog.show(parentClass.getFragmentManager(), "");

                        }
                        return false;
                    }
                });
            }
        });*/

        return row;
    }
}
