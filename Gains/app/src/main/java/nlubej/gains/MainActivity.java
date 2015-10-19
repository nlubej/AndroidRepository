package nlubej.gains;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.navigationliveo.NavigationLiveo;
import nlubej.gains.Fragments.StartWorkout;
import nlubej.gains.Fragments.WorkoutLog;


public class MainActivity extends NavigationLiveo implements OnItemClickListener {


private HelpLiveo mHelpLiveo;

	@Override
	public void onInt(Bundle savedInstanceState) {
		// User Information
		this.userName.setText("Current program");
		this.userEmail.setText("I will make lots of gains");
		this.userBackground.setImageResource(R.drawable.gaains);

		// Creating items navigation
		mHelpLiveo = new HelpLiveo();
		mHelpLiveo.add("Start workout", R.mipmap.ic_launcher);
		mHelpLiveo.add("Logged workouts", R.mipmap.ic_launcher);
		mHelpLiveo.add("Graphs and statistics", R.mipmap.ic_launcher);

		mHelpLiveo.addSubHeader("Tools");
		mHelpLiveo.add("Wilks calculator", R.mipmap.ic_launcher);
		mHelpLiveo.add("Max rep calculator", R.mipmap.ic_launcher);

		mHelpLiveo.addSubHeader("Info");
		mHelpLiveo.add("About", R.mipmap.ic_launcher);

		//with(this, Navigation.THEME_DARK). add theme dark
		//with(this, Navigation.THEME_LIGHT). add theme light

		with(this) // default theme is dark
				.startingPosition(2) //Starting position in the list
				.addAllHelpItem(mHelpLiveo.getHelp()).setOnPrepareOptionsMenu(onPrepare)
				.setOnClickFooter(onClickFooter)
				.build();
	}


	@Override //The "R.id.container" should be used in "beginTransaction (). Replace"
	public void onItemClick(int position) {
		Fragment mFragment;
		FragmentManager mFragmentManager = getSupportFragmentManager();
		Log.i("nlubej",position+"");
		switch (position){
			case 0:
				mFragment = new StartWorkout();
				break;
			case 1:
				mFragment = new WorkoutLog();
				break;
			default:
				mFragment = StartWorkout.newInstance(mHelpLiveo.get(position).getName());
				break;
		}

		if (mFragment != null){
			mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
		}

		setElevationToolBar(position != 2 ? 15 : 0);
	}

private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
	@Override
	public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
	}
};


private View.OnClickListener onClickFooter = new View.OnClickListener() {
	@Override
	public void onClick(View v) {
		closeDrawer();
	}
};

}