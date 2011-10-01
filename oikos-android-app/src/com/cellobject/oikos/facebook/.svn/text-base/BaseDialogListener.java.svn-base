/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.facebook;

import com.facebook.android.DialogError;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

/**
 * Skeleton base class for RequestListeners, providing default error handling. Applications should handle these error conditions.
 */
public abstract class BaseDialogListener implements DialogListener {
	public void onFacebookError(final FacebookError e) {
		e.printStackTrace();
	}

	public void onError(final DialogError e) {
		e.printStackTrace();
	}

	public void onCancel() {
	}
}
