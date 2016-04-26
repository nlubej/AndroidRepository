package nlubej.gains;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import nlubej.gains.Views.MainActivity;

public class Splash extends Activity {

	
	@Override
    protected void onCreate(Bundle savedInstanceState) {      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, 1000);
    }
}
