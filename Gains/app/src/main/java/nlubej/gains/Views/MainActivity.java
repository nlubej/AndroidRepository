package nlubej.gains.Views;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.Menu;

import br.liveo.Model.HelpLiveo;
import br.liveo.interfaces.OnItemClickListener;
import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.navigationliveo.NavigationLiveo;
import nlubej.gains.Database.AndroidDatabaseManager;
import nlubej.gains.R;
import nlubej.gains.interfaces.OnBackPressedListener;


public class MainActivity extends NavigationLiveo implements OnItemClickListener, OnPrepareOptionsMenuLiveo
{
	protected OnBackPressedListener onBackPressedListener;
	private HelpLiveo mHelpLiveo;

	@Override
	public void onInt(Bundle savedInstanceState) {
		// User Information
		//this.userName.setText("Current program");
	//	this.userEmail.setText("I will make lots of gains");
		//this.userBackground.setImageResource(R.drawable.gaains);

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
				.startingPosition(0) //Starting position in the list
				.addAllHelpItem(mHelpLiveo.getHelp()).setOnPrepareOptionsMenu(onPrepare)
				.colorItemSelected(R.color.PrimaryColor)
				.colorNameSubHeader(R.color.PrimaryColor)

				.removeHeader()
				//.header
				.build();
	}


	@Override
	public void onBackPressed() {
		if (onBackPressedListener != null)
			onBackPressedListener.doBack();
		else
			super.onBackPressed();
	}

	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.onBackPressedListener = onBackPressedListener;
	}

	@Override //The "R.id.container" should be used in "beginTransaction (). Replace"
	public void onItemClick(int position) {
		Fragment mFragment;
		FragmentManager mFragmentManager = getFragmentManager();
		switch (position){
			case 0:
				mFragment = new NewWorkout();
				break;
			case 1:
				mFragment = new Program();
				break;
			case 2:
				mFragment = new LogViewer();
				break;
			case 3:




				mFragment = new OneRepMaxCalculator();
				break;
			case 4:
				Intent dbmanager = new Intent(getApplicationContext(),AndroidDatabaseManager.class);
				startActivity(dbmanager);
				mFragment = new OneRepMaxCalculator();
				break;
			case 5:
				mFragment = new OneRepMaxCalculator();
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


	@Override
	protected void onDestroy() {
		onBackPressedListener = null;
		super.onDestroy();
	}
}