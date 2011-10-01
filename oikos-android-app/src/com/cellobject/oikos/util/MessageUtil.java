package com.cellobject.oikos.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class MessageUtil {
	private final Context context;

	private final int DURATION_SHORT = Toast.LENGTH_SHORT;

	private final int DURATION_MEDIUM = Toast.LENGTH_LONG;

	public MessageUtil(final Context context) {
		this.context = context;
	}

	public void textToastShort(final String textToDisplay) {
		final CharSequence text = textToDisplay;
		final Toast toast = Toast.makeText(context, text, DURATION_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	public void textToastMedium(final String textToDisplay) {
		final CharSequence text = textToDisplay;
		final Toast toast = Toast.makeText(context, text, DURATION_MEDIUM);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
}
