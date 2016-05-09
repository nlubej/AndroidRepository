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
		mHelpLiveo.add("Start workout", R.mipmap.ic_launcher);
		mHelpLiveo.add("Programs", R.mipmap.ic_launcher);
		mHelpLiveo.add("Workout logs", R.mipmap.ic_launcher);
		mHelpLiveo.add("Statistics", R.mipmap.ic_launcher);

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
				mFragment = new Program();
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

    @Override
    public void onPrepareOptionsMenu(Menu menu, int position, boolean visible)
    {
        this.userEmail.setText("test");
    }
}