/*
 * Copyright 2012 MG2 Innovations LLC (http://www.mg2innovations.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	
 *	http://www.apache.org/licenses/LICENSE-2.0 	
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */

package com.appdevkit.push.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.appdevkit.push.IPushServiceMonitor;
import com.appdevkit.push.PushService;

/**
 * Demo Android application to demonstrate AppDevKit.com's push notifications.
 */
public class PushDemo extends Activity {

    /**
     * Log tag.
     */
    private final static String TAG = "PushDemo";

    /**
     * The push notification service reference.
     */
    private IPushServiceMonitor service = null;

    /**
     * Object which handles messages sent to the application by the PushService.
     */
    private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {

	    int msgType = msg.getData().getInt("msg_type");
	    String subject = msg.getData().getString("subject");
	    String messageText = msg.getData().getString("message");

	    String message = String.format("Msg Type: %s\nSubject: %s\nMessage: %s", msgType, subject, messageText);

	    Toast.makeText(PushDemo.this, message, Toast.LENGTH_LONG).show();
	}
    };

    private ServiceConnection svcConn = new ServiceConnection() {
	public void onServiceConnected(ComponentName className, IBinder binder) {
	    Log.i(TAG, "onServiceConnected");

	    // Get reference to service and register the handler.
	    service = (IPushServiceMonitor) binder;
	    service.registerHandler(handler);
	}

	public void onServiceDisconnected(ComponentName className) {
	    service = null;
	}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	Log.i(TAG, "Binding to push service");

	Intent service = new Intent(this, PushService.class);
	startService(service);
	bindService(new Intent(this, PushService.class), svcConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	// If service has not been started, service is NULL
	if (service != null)
	    service.unregisterHandler();

	unbindService(svcConn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }
}
