package nlubej.gains.Views;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;

import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.navigationliveo.NavigationLiveo;
import nlubej.gains.R;


public class MainActivity extends NavigationLiveo implements OnItemClickListener, OnPrepareOptionsMenuLiveo
{


private HelpLiveo mHelpLiveo;

	@Override
	public void onInt(Bundle savedInstanceState) {
		// User Information
		this.userName.setText("Current program");
		this.userEmail.setText("I will make lots of gains");
		this.userBackground.setImageResource(R.drawable.gaains);

        this.setOnPrepareOptionsMenu(this);
		// Creating items navigation
		mHelpLiveo = new HelpLiveo();
		mHelpLiveo.add("Start workout", R.drawable.ic_play_arrow_black_36dp);
		mHelpLiveo.add("Programs", R.drawable.ic_fitness_center_black_36dp);
		mHelpLiveo.add("Workout logs", R.drawable.ic_content_paste_black_36dp);

		mHelpLiveo.addSubHeader("Tools");
		mHelpLiveo.add("Wilks calculator", R.drawable.ic_fiber_manual_record_black_36dp);
		mHelpLiveo.add("Max rep calculator", R.drawable.ic_fiber_manual_record_black_36dp);

		mHelpLiveo.addSubHeader("Info");
		mHelpLiveo.add("About", R.drawable.ic_info_outline_black_36dp);

		//with(this, Navigation.THEME_DARK). add theme dark
		//with(this, Navigation.THEME_LIGHT). add theme light

		with(this) // default theme is dark
				.startingPosition(1) //Starting position in the list
				.addAllHelpItem(mHelpLiveo.getHelp()).setOnPrepareOptionsMenu(onPrepare)
				.removeHeader()
				//.header
				.build();
	}


    @Override //The "R.id.container" should be used in "beginTransaction (). Replace"
	public void onItemClick(int position) {
		Fragment mFragment;
		FragmentManager mFragmentManager = getFragmentManager();
		Log.i("nlubej",position+"");
		switch (position){
			case 0:
				mFragment = new NewWorkout();
				break;
			case 1:
				mFragment = new Program();
				this.userEmail.setText("program");
				break;
			case 2:
				mFragment = new NewWorkout();
				this.userEmail.setText("new workout");
				break;
			case 3:
				mFragment = new OneRepMaxCalculator();
				break;
			case 4:
				mFragment = new ExerciseLogger();
				break;
			case 5:
				mFragment = new ExerciseLogger();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu, int position, boolean visible)
    {
        this.userEmail.setText("test");
    }
}