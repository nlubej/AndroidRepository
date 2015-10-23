package nlubej.gains.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.navigationliveo.NavigationLiveo;
import nlubej.gains.Activities.StartScreen;
import nlubej.gains.Fragments.OneRepMaxCalculator;
import nlubej.gains.Fragments.Program;
import nlubej.gains.Fragments.WilksCalculator;
import nlubej.gains.Fragments.WorkoutLog;
import nlubej.gains.R;


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
				.startingPosition(0) //Starting position in the list
				.addAllHelpItem(mHelpLiveo.getHelp()).setOnPrepareOptionsMenu(onPrepare)
				.build();
	}


	@Override //The "R.id.container" should be used in "beginTransaction (). Replace"
	public void onItemClick(int position) {
		Fragment mFragment;
		FragmentManager mFragmentManager = getSupportFragmentManager();
		Log.i("nlubej",position+"");
		switch (position){
			case 0:
				mFragment = new StartScreen();
				break;
			case 1:
				mFragment = new Program();
				break;
			case 2:
				mFragment = new WorkoutLog();
				break;
			case 3:
				mFragment = new OneRepMaxCalculator();
				break;
			case 4:
				mFragment = new WilksCalculator();
				break;
			case 5:
				mFragment = new WilksCalculator();
				break;
			default:
				mFragment = StartScreen.newInstance(mHelpLiveo.get(position).getName());
				break;
		}

		if (mFragment != null){
			mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
		}

	}

private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
	@Override
	public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
	}
};

}