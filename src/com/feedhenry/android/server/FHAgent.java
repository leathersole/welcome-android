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
package com.feedhenry.android.server;

import org.json.fh.JSONArray;
import org.json.fh.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.feedhenry.android.MyApplication;
import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHCloudRequest;

public class FHAgent {
	
    public static boolean initialised = false;
    
    public void cloudCall(FHActCallback fhActCallback){
        JSONObject param = new JSONObject("{}");
        this.call("hello", param, fhActCallback);
    }
    

    public void dataBrowser(String userName, FHActCallback fhActCallback) {
    	JSONObject param = new JSONObject("{'collection':'Users', 'document': {username: " + JSONObject.quote(userName) + "}}");
    	this.call("saveData", param, fhActCallback);
	}
    
    
    public void getWeather(double lat, double lng, FHActCallback fhActCallback){
        JSONObject param = new JSONObject("{'lat':'" + lat + "', 'lon':'" + lng + "'}");
        this.call("getWeather", param, fhActCallback);
    }
    
    public void getFHVars(FHActCallback fhActCallback){
        JSONObject param = new JSONObject("{}");
        this.call("getFhVars", param, fhActCallback);
    }
        
    public static boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() ? true : false;
    }
    
    
    private void call(final String path, final JSONObject params, final FHActCallback callback) {
        try {
            FHCloudRequest request = FH.buildCloudRequest(path, "POST", null, params);
            request.executeAsync(callback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("fh", "Init Failed!");
        }
    }
    
    
    protected FHResponse getFakedErrorResponse(String errMessage){
        FHResponse fakedErrorResponse=new FHResponse(new JSONObject(),new JSONArray(),new Throwable(errMessage),errMessage);
        return fakedErrorResponse;
    }


    public static void init(final FHActCallback callback) {
        if (initialised) {
            callback.success(null);
        } else {
            FH.init(MyApplication.getAppContext(), new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    initialised = true;
                    callback.success(fhResponse);
                }
                @Override
                public void fail(FHResponse fhResponse) {
                    initialised = false;
                    callback.fail(fhResponse);
                }
            });
        }
    }
}
