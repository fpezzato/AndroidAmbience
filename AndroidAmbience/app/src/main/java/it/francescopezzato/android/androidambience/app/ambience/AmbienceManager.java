package it.francescopezzato.android.androidambience.app.ambience;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import it.francescopezzato.android.androidambience.app.R;

/**
 * Created by francescopezzato on 29/03/2014.
 */
public class AmbienceManager extends Handler {

	private static final class Messages {
		private Messages() { /* no-op */ }

		public static final int REMOVE_CURRENT_AMBIENCE = -10;
	}

	private static AmbienceManager INSTANCE;

	private AmbienceManager() {

	}

	/**
	 * @return The currently used instance of the {@link AmbienceManager}.
	 */
	public static synchronized AmbienceManager geAmbienceManager() {
		if (null == INSTANCE) {
			INSTANCE = new AmbienceManager();
		}

		return INSTANCE;
	}

	WeakReference<Activity> mActivity;

	public void setCurrentActivity(Activity activity) {
		mActivity = new WeakReference<Activity>(activity);
	}

	private View getAmbienceView(ViewGroup viewGroup) {
		View result = null;
		if (mActivity != null) {
			result = mActivity.get().getLayoutInflater().inflate(R.layout.ambience_layout, viewGroup, false);
		}

		return result;
	}

	public void showAmbience() {
		Activity activity = mActivity.get();

		if (activity != null) {

			ViewGroup containerView = (ViewGroup) (activity.findViewById(android.R.id.content));
			int containerViewHeight = containerView.getMeasuredHeight();

			final View ambienceView = getAmbienceView(containerView);

			ambienceView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hideAmbience(ambienceView);
				}
			});

			ViewGroup.LayoutParams ambienceViewparams = ambienceView.getLayoutParams();

			if (ambienceViewparams == null) {
				ambienceViewparams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}

			((FrameLayout.LayoutParams) ambienceViewparams).topMargin = containerViewHeight - ambienceViewparams.height;


			ambienceView.startAnimation(getInAnimation(ambienceView));
			containerView.addView(ambienceView, containerView.getChildCount(), ambienceViewparams);

			/*ambienceView.requestLayout(); // This is needed so the animation can use the measured with/height

			ambienceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						ambienceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						ambienceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}

					ambienceView.startAnimation(getInAnimation(ambienceView));
				}
			});*/


			//auto hide
			Message message = obtainMessage(Messages.REMOVE_CURRENT_AMBIENCE);
			message.obj = ambienceView;

			sendMessageDelayed(message, 1000);
		}
	}

	private void hideAmbience(final View ambience) {
		final ViewGroup parent = (ViewGroup) ambience.getParent();
		Animation animation = getOutAnimation(ambience);

		ambience.startAnimation(getOutAnimation(ambience));

		new Handler().postDelayed(new Runnable() {
			public void run() { 
				parent.removeView(ambience);
			}
		}, animation.getDuration());

	}

	@Override
	public void handleMessage(Message message) {
		final View ambience = (View) message.obj;
		if (ambience != null) {

			switch (message.what) {
				case Messages.REMOVE_CURRENT_AMBIENCE: {
					hideAmbience(ambience);
					break;
				}
			}
		}

	}

	private Animation getInAnimation(View ambience){
		ambience.measure(View.MeasureSpec.UNSPECIFIED,   View.MeasureSpec.UNSPECIFIED);
		Animation translateIn = new TranslateAnimation(
				//X stuff (from,to)
				0,
				0,
				//Y stuff
				ambience.getMeasuredHeight(),
				0
		);
		translateIn.setDuration(500);
		return  translateIn;
	}

	private Animation getOutAnimation(View ambience){
		 Animation translateOut = new TranslateAnimation(
				//X stuff (from,to)
				0,
				0,
				//Y stuff
				0,
				ambience.getMeasuredHeight()
		);
		translateOut.setDuration(500);
		return  translateOut;
	}


}
