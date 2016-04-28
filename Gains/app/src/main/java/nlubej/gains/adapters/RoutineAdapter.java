package nlubej.gains.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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
    Routine parentClass;
    private QueryFactory db;

    public RoutineAdapter(Routine parent, QueryFactory database)
    {
        this.ctx = parent.getApplicationContext();
        this.parentClass = parent;
        db = database;

        routineDto = new ArrayList();
    }


    @Override
    public int getCount()
    {
        return routineDto.size();
    }

    @Override
    public Object getItem(int position)
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

    class RoutineHolder
    {
        TextView name;
        TextView subName;

        public RoutineHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.name);
            subName = (TextView) v.findViewById(R.id.subName);
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
        routineHolder.subName.setText(String.format("%d exercises",temp.ExerciseCount));
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
