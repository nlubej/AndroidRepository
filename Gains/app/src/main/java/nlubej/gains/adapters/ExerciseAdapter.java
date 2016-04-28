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
    Exercise parentClass;
    private QueryFactory db;

    public ExerciseAdapter(Exercise parent, QueryFactory database)
    {
        this.ctx = parent.getApplicationContext();
        this.parentClass = parent;
        db = database;

        exerciseDto = new ArrayList();
    }


    @Override
    public int getCount()
    {
        return exerciseDto.size();
    }

    @Override
    public Object getItem(int position)
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
