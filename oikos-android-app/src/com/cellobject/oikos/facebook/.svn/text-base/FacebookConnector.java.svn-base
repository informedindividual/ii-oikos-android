/**
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; version 2 of the License. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY. See the GNU General Public License for more details. Copyright (C) 2011 Oikos.no, developed
 * by InformedIndividual.org & CellObject.com
 */
package com.cellobject.oikos.facebook;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.cellobject.oikos.facebook.SessionEvents.AuthListener;
import com.cellobject.oikos.facebook.SessionEvents.LogoutListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class FacebookConnector {
	private static final String TAG = "oikos";

	private Facebook facebook = null;

	private final Context context;

	private final String[] permissions;

	private final Handler mHandler;

	private final Activity activity;

	private final SessionListener mSessionListener = new SessionListener();;

	public FacebookConnector(final String appId, final Activity activity, final Context context, final String[] permissions) {
		this.facebook = new Facebook(appId);
		SessionStore.restore(facebook, context);
		SessionEvents.addAuthListener(mSessionListener);
		SessionEvents.addLogoutListener(mSessionListener);
		this.context = context;
		this.permissions = permissions;
		this.mHandler = new Handler();
		this.activity = activity;
	}

	public void login() {
		if (!facebook.isSessionValid()) {
			facebook.authorize(this.activity, this.permissions, new LoginDialogListener());
		}
	}

	public void logout() {
		SessionEvents.onLogoutBegin();
		final AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(this.facebook);
		asyncRunner.logout(this.context, new LogoutRequestListener());
	}

	public void postMessageOnWall(final String facebookPageId, final String message, final boolean postOnMyWall, final boolean postOnStoreWall) {
		if (facebook.isSessionValid()) {
			final Bundle parameters = new Bundle();
			parameters.putString("message", message);
			try {
				if (postOnMyWall) {
					//Post to my profile wall
					final String myWallResponse = facebook.request("me/feed", parameters, "POST");
					Log.i(TAG, myWallResponse);
				}
				if (postOnStoreWall && !facebookPageId.equalsIgnoreCase("")) {
					//Post to store's profile wall
					final String storeGraph = facebookPageId + "/feed";
					final String storeResponse = facebook.request(storeGraph, parameters, "POST");
					Log.i(TAG, storeResponse);
				}
			} catch (final IOException e) {
				Log.e(TAG, "Error posting request message to facebook", e);
			}
		} else {
			login();
		}
	}

	private final class LoginDialogListener implements DialogListener {
		public void onComplete(final Bundle values) {
			SessionEvents.onLoginSuccess();
		}

		public void onFacebookError(final FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(final DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
	}
	public class LogoutRequestListener extends BaseRequestListener {
		public void onComplete(final String response, final Object state) {
			// callback should be run in the original thread,
			// not the background thread
			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}
	private class SessionListener implements AuthListener, LogoutListener {
		public void onAuthSucceed() {
			SessionStore.save(facebook, context);
		}

		public void onAuthFail(final String error) {
		}

		public void onLogoutBegin() {
		}

		public void onLogoutFinish() {
			SessionStore.clear(context);
		}
	}

	public Facebook getFacebook() {
		return this.facebook;
	}
}
