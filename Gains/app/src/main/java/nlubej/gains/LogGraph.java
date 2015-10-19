package nlubej.gains;
/*
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;


public class LogGraph extends Activity implements OnClickListener {
	
	
	
	private GraphicalView mChartView;
	DBAdapter dbHelper;
	LinearLayout layout;
	SharedPreferences prefs;
	
	XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	  
	float textSize;
	int start,end, id, prevId;
	int min,max;
	int selectedPoint;
	VerticalTextView unitSystem;
	Button next;
	Button prev;
	
	int[] workoutNums = null;
	double[] weights = null;
	long exerciseId= 0, routineId = 0;
	String durationEnd = "";
	String exercise  = "";
	String exerciseType = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.graphs);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	Intent intent = getIntent();
        exerciseId = intent.getLongExtra("exerciseID", exerciseId);
        routineId = intent.getLongExtra("routineID", routineId);
        exercise = intent.getStringExtra("exerciseName");
       // String exerciseName = intent.getStringExtra("exerciseName");
        unitSystem = (VerticalTextView)findViewById(R.id.unitSystem);
        textSize = unitSystem.getTextSize();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(exercise);
        if(prefs.getString("unitSystem","").compareTo("metric") == 0) {
        	unitSystem.setText("KG");
        }
        else
        	unitSystem.setText("LBS");
        
        Log.d("nlubej","unit system: " +  prefs.getString("unitSystem",""));

        layout = (LinearLayout) findViewById(R.id.chart);
       

        XYSeries series = new XYSeries("title");
        mDataset.addSeries(series);
        mCurrentSeries = series;
      
        
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setFillPoints(true);
        renderer.setColor(getResources().getColor(R.color.custom_black));
        renderer.setLineWidth(3f);
        
        renderer.setChartValuesSpacing(20);
    
        renderer.setDisplayChartValues(true);
        renderer.setChartValuesTextSize(textSize);
        
        

      // mRenderer.setZoomEnabled(true, true);
        mRenderer.addSeriesRenderer(renderer);
        mRenderer.setZoomRate(0.005f);
        mRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        mRenderer.setShowGridX(true);
        //mRenderer.setMargins(new int[] { 30, 40, 15, 0 });
        mRenderer.setLabelsTextSize(textSize);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.TRANSPARENT);
        mRenderer.setAxesColor(Color.TRANSPARENT);
        mRenderer.setGridColor(getResources().getColor(R.color.custom_black));
        mRenderer.setXLabelsColor(getResources().getColor(R.color.custom_black));
        mRenderer.setYLabelsColor(0,getResources().getColor(R.color.custom_black));
        mRenderer.setClickEnabled(true);
        mRenderer.setSelectableBuffer(30);
        mRenderer.setPointSize(9f);
        mRenderer.setAxesColor(Color.TRANSPARENT);
        mRenderer.setShowAxes(true);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowLegend(false);
       
        
        mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
        mChartView.setOnClickListener(this);

        init();
    }
    
    public void init() {
    	 
    	  dbHelper = new DBAdapter(getApplicationContext());
    	  int num = 0;
			try {
				dbHelper.open(); 
					num = dbHelper.getLastWorkoutNum(exerciseId);
					Log.d("nlubej","last workout num: " + num);
					
					Cursor c = dbHelper.getLog(exerciseId);
					if(c.getCount() != 0) {
						Log.d("nlubej","log count: " + c.getCount());
						weights = new double[num];
						workoutNums = new int[num];
						//duration = new double[c.getCount()+num]; 
						int i=0;
						int workoutNum = 1;
						double prevWeight = -1;
						
						while(c.moveToNext())
						{
							if(c.getInt(6) != workoutNum)
							{

									i++;
									workoutNum++;
									weights[i] = c.getDouble(2);
									workoutNums[i] = workoutNum;
									prevWeight = 0;
								
							}
							else
							{
								if(prevWeight < c.getDouble(2))
								{
									weights[i] = c.getDouble(2);
									workoutNums[i] = workoutNum;
									prevWeight = weights[i];
								}
							}
							
						}

						while(c.moveToNext()){
							if(c.getInt(6) != workoutNum){
								workoutNum = c.getInt(6);
								weights[i] = 0;
								//duration[i] = 0;
								workoutNums[i] = workoutNum;
								i++;
								
							}
							if(c.getString(0).compareTo("Strength") == 0) {
								exerciseType = "Strength";
								weights[i] = c.getDouble(2);
								workoutNums[i] = c.getInt(6);
								
								i++;
								
							}
						}
					}
					c.close();
				}
				catch(NullPointerException e)
				{
					
				}
				finally {
					dbHelper.close();
					
				}
			
			
            
			if(weights != null) 
			{
				id = 1;
				prevId = R.id.show;
				 
				min = getMin(weights);
			    max = getMax(weights);
			        
			    mRenderer.setYAxisMin(min-5);
			    mRenderer.setYAxisMax(max+5);
				//fill data
				for(int i=0; i< weights.length; i++) {
					mCurrentSeries.add(workoutNums[i], weights[i]); 
				}
			}
			else //min and max so the graph is pretier
			{
				mCurrentSeries.add(1, 0);
				mCurrentSeries.add(2, 0); 
				mCurrentSeries.add(3, 0); 
				mCurrentSeries.add(4, 0); 
				mRenderer.setPointSize(0);
				mRenderer.setYAxisMin(50-5);
			    mRenderer.setYAxisMax(50+5);
			}
			
	        layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
	                LayoutParams.MATCH_PARENT));
    }
    
    
    //gets the minimum value that will be showen in graph
    public double getGraphMin(int min) {
    	
    	if(min<4)
    		return 0;
    	else
    		return min-5;
    }
    
    public double getGraphMax(int max) {
    	return max+5;
    }
    
    
    public int getMin( double[] weights){
    	double min = 10000;
    	for(int i=0; i<weights.length; i++) {
    		if(weights[i] < min) 
    			min = weights[i];
    	}
    	
    	if(min <=4)
    		return 0;
    	else
    		return (int) (min-4);
    	
    }
    
    public int getMax( double[] weights) {
    	double max = 0;
    	for(int i=0; i<weights.length; i++) {
    		if(weights[i] > max) 
    			max = weights[i];
    	}
    	
    	return (int) (max+4);
    	
    }




	@Override
	public void onClick(View v) 
	{
		if(v == mChartView) 
		{

			SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
	        if (seriesSelection != null) 
	        {
	        	GraphPointDialog dialog = new GraphPointDialog();
	        	Bundle arg = new Bundle();
	        	
	    		arg.putLong("exerciseId", exerciseId);
	    		arg.putDouble("weight", seriesSelection.getValue());
	    		arg.putInt("workoutNum", (int)seriesSelection.getXValue());
	    		dialog.setArguments(arg);
	    		
	        	dialog.show(getFragmentManager(), "log point");

	        }
		}
	}
	
	
	
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        

      //  getMenuInflater().inflate(R.menu.menu_exercise, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        
        if(id == android.R.id.home){
        	onBackPressed();
        }

        

        
        return super.onOptionsItemSelected(item);
    }


}*/