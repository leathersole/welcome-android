/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feedhenry.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.feedhenry.android.server.FHAgent;
import com.feedhenry.android.utilities.MyToast;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;


public class MyApplication extends Application {

	private static Context context;
	private static boolean isInitialised = false;

	public void onCreate() {
		super.onCreate();
		MyApplication.context = getApplicationContext();
	}

	// Initialize application by connecting to FeedHenry Cloud
	public static boolean initApp(final Activity activity) {
		Log.i("FEEDHENRY", "In initApp");
		if (FHAgent.isOnline()) {
			if (activity == null) {
				initFH(activity);
			} else {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						initFH(activity);
					}
				});
			}
			isInitialised = true;
			return true;
		}
		return false;
	}

	// Use FH Agent to connect
	protected static void initFH(final Activity activity) {
		FHAgent.init(new FHActCallback() {
			@Override
			public void success(FHResponse fhResponse) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.showToast("Connected to FeedHenry");
					}
				});
			}

			@Override
			public void fail(FHResponse fhResponse) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MyToast.showToast("Server connection failed");
					}
				});
			}
		});
	}

	public static boolean initApp() {
		return initApp();
	}

	public static boolean isInitialised() {
		return isInitialised;
	}

	public static Context getAppContext() {
		return MyApplication.context;
	}
}