package nlubej.gains;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class Achievements extends Activity {

	GridView gridView;

	static final String[] MOBILE_OS = new String[] { 
		"Android", "iOS","Windows", "Blackberry" };

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.achievement);

		gridView = (GridView) findViewById(R.id.achievementGrid);

		gridView.setAdapter(new GridViewAdapter(this));

		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	        
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				

			}
		});

	}
	
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case android.R.id.home:
		    	onBackPressed();
		    	return true;
		    }
		    return super.onOptionsItemSelected(item);
		}
	
	
	
	
	
	
	public class GridViewAdapter extends BaseAdapter {
		private Context context;
		private ArrayList<Achievement> list;
	 
		public GridViewAdapter(Context context) {
			this.context = context;
			list = new ArrayList<Achievement>();
			Resources res = context.getResources();
			String[] tempAchievementName = res.getStringArray(R.array.achievements);
			int[] achievementImages = {R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, 
					R.drawable.achievement, R.drawable.achievement,R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, 
					R.drawable.achievement, R.drawable.achievement,R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, 
					R.drawable.achievement, R.drawable.achievement,R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, R.drawable.achievement, 
					R.drawable.achievement, R.drawable.achievement};
			for(int i=0; i<tempAchievementName.length; i++) 
			{
				Achievement tempAchievement = new Achievement(achievementImages[i],tempAchievementName[i]);
				list.add(tempAchievement);
			}
		}
	 
		public View getView(int position, View convertView, ViewGroup parent) {
	 
			View row = convertView;
			ViewHolder holder = null;
			if(row == null) 
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.achievement_item, parent, false);
				holder = new ViewHolder(row);
				row.setTag(holder);
			}
			else 
			{
				holder = (ViewHolder) row.getTag();
			}
			
			Achievement temp = list.get(position);
			holder.achievementImage.setImageResource(temp.imageId);
			
			return row;
		}
	 
		@Override
		public int getCount() {
			return list.size();
		}
	 
		@Override
		public Object getItem(int position) {
			return list.get(position);
		}
	 
		@Override
		public long getItemId(int position) {
			return position;
		}
	 
		class ViewHolder
		{
			ImageView achievementImage;
			ViewHolder(View v)
			{
				achievementImage = (ImageView)v.findViewById(R.id.grid_item_image);
			}
		}
		
		
		class Achievement
		{
			int imageId;
			String achievementName;
			
			Achievement(int imageId, String achievementName) {
				this.imageId = imageId;
				this.achievementName = achievementName;
			}
		}
	}

}