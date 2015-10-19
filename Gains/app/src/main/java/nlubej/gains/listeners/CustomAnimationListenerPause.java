package nlubej.gains.listeners;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;




public class CustomAnimationListenerPause implements AnimationListener {

	private TextView tw;
	private Animation alpha2;
	public CustomAnimationListenerPause(TextView tw, Animation alpha2){
		this.tw = tw;
		this.alpha2 = alpha2;
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		tw.startAnimation(alpha2);
		tw.getAnimation().setFillAfter(true);
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
		
	}


}
