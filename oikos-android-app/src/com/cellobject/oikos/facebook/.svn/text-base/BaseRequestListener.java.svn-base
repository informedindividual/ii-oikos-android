/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import android.util.Log;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

/**
 * Skeleton base class for RequestListeners, providing default error handling. Applications should handle these error conditions.
 */
public abstract class BaseRequestListener implements RequestListener {
	public void onFacebookError(final FacebookError e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	public void onFileNotFoundException(final FileNotFoundException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	public void onIOException(final IOException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}

	public void onMalformedURLException(final MalformedURLException e, final Object state) {
		Log.e("Facebook", e.getMessage());
		e.printStackTrace();
	}
}
