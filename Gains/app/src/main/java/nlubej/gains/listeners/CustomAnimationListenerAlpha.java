package nlubej.gains.listeners;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;




public class CustomAnimationListenerAlpha implements AnimationListener {

	private TextView tw;
	private Animation pause;
	public CustomAnimationListenerAlpha(TextView tw, Animation pause){
		this.tw = tw;
		this.pause = pause;
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		tw.startAnimation(pause);
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
		
	}


}
