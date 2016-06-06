package nlubej.gains.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;

import nlubej.gains.Enums.Constants;
import nlubej.gains.R;
import nlubej.gains.Tools;
import nlubej.gains.Views.ExerciseLogger;

/**
 * Created by nlubej on 28.4.2016.
 */
public class SelectionAdapter extends BaseAdapter
{
    ExerciseLogger parent;
    Context ctx;
    private String[] items;
    private ArrayList<SelectionRow> selectionRows;
    private double weightIcrement;

    private SharedPreferences prefs;

    public SelectionAdapter(ExerciseLogger parent, String[] items)
    {
        this.ctx = parent;
        this.parent = parent;
        this.items = items;
        this.selectionRows = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        weightIcrement = Tools.ToDouble(prefs.getString(Constants.WEIGHT_INCREMENT_KEY, "1"), 2);

        for (String item: items)
        {
            if(Tools.ToDouble(item, 2) == weightIcrement)
                selectionRows.add(new SelectionRow(item, true));
            else
                selectionRows.add(new SelectionRow(item));
        }

    }

    @Override
    public int getCount()
    {
        return selectionRows.size();
    }

    @Override
    public SelectionRow getItem(int position)
    {
        return selectionRows.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public SelectionRow GetSelectedItem()
    {
        for (SelectionRow row : selectionRows)
        {
            if(row.IsSelected)
                return row;
        }

        return null;
    }

    public void SetSelectedRow(int id)
    {
        for (SelectionRow row : selectionRows)
        {
            row.IsSelected = false;
        }

        SelectionRow selectedRow = selectionRows.get(id);
        selectedRow.IsSelected = true;
    }

    class SelectionViewHolder
    {
        TextView name;
        //TextView subName;
        ImageView btnSelected;

        public SelectionViewHolder(View v)
        {
            name = (TextView) v.findViewById(R.id.name);
            //subName = (TextView) v.findViewById(R.id.subName);
            btnSelected = (ImageView) v.findViewById(R.id.btnSelected);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        SelectionViewHolder holder;

        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_selection, parent, false);
            holder = new SelectionViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (SelectionViewHolder) row.getTag();
        }


        final SelectionRow temp = selectionRows.get(position);

        holder.name.setText(temp.Item);

        holder.btnSelected.setVisibility(View.GONE);
        if(temp.IsSelected)
        {
            holder.btnSelected.setVisibility(View.VISIBLE);
            holder.btnSelected.setImageDrawable(new IconDrawable(ctx, Iconify.IconValue.zmdi_check).colorRes(R.color.colorAccent).sizeDp(40));
        }

        return row;
    }

    public class SelectionRow
    {
        public String Item;
        public boolean IsSelected;

        public SelectionRow(String item)
        {
            Item = item;
            IsSelected = false;
        }

        public SelectionRow(String item, boolean isSelected)
        {
            Item = item;
            IsSelected = isSelected;
        }
    }
}