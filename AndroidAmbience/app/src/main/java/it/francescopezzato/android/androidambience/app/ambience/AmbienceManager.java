package it.francescopezzato.android.androidambience.app.ambience;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

import it.francescopezzato.android.androidambience.app.R;

/**
 * Created by francescopezzato on 29/03/2014.
 */
public class AmbienceManager {

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

			ViewGroup containerView = (ViewGroup) (activity.findViewById(android.R.id.content)) ;
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

			((FrameLayout.LayoutParams) ambienceViewparams).topMargin=containerViewHeight-ambienceViewparams.height;

			containerView.addView(ambienceView, containerView.getChildCount(), ambienceViewparams);
		}
	}

	private void hideAmbience(View ambience){
		ViewGroup parent = (ViewGroup)ambience.getParent();
		parent.removeView(ambience);

	}

}
