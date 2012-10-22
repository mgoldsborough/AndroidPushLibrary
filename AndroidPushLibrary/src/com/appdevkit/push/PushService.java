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

package com.appdevkit.push;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import pubnub.api.Callback;
import pubnub.api.Pubnub;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Service which receives push notifications from AppDevKit.com.
 * 
 * Remember to add the following to your AndroidManifest.xml file:
 * 
 * <pre>
 * <uses-permission android:name="android.permission.INTERNET" />
 * <application>
 *     <receiver android:name="com.appdevkit.push.PushServiceReceiver" >
 *         <intent-filter>
 *             <action android:name="android.intent.action.BOOT_COMPLETED" />
 *         </intent-filter>
 *     </receiver>
 * </application>
 * </pre>
 */
public class PushService extends Service {

    /**
     * Log tag.
     */
    private final static String TAG = "SubscriberService";

    /**
     * The PubNub subscriber key for AppDevKit.com
     */
    private String subscriberKey;

    /**
     * The PubNub 3.3 object used to receive push notifications.
     */
    private Pubnub pubnub;

    /**
     * The service binder.
     */
    private final Binder binder = new PushServiceBinder();

    /**
     * The handler which notifies your application. Bind to the service and then
     * call registerHandler(handler);
     */
    private Handler handler;

    /**
     * The channel name your service will subscribe to.
     */
    private String channelName;

    /**
     * The thread which manages the subscription to PubNub.
     */
    private Thread subscriberThread;

    @Override
    public void onCreate() {
	super.onCreate();
	Log.i(TAG, "PushService onCreate()");

	// Initialize PubNub 3.3 object with appropriate subscriber key.
	Resources res = getResources();
	this.subscriberKey = res.getString(R.string.adk_subscriber_key);
	
	Log.i(TAG, this.subscriberKey);
	
	this.channelName = res.getString(R.string.adk_channel_name);

	pubnub = new Pubnub("", this.subscriberKey);

	subscriberThread = new Thread(runner);
	subscriberThread.start();
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	Log.i(TAG, "PushService onDestroy()");

	// Interrupt the thread if it is running
	if (subscriberThread != null && subscriberThread.isAlive())
	    subscriberThread.interrupt();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.i(TAG, String.format("PushService start id %s: %s", startId, intent));

	// We want this service to continue running until it is explicitly
	// stopped, so return sticky.
	return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
	return binder;
    }

    /**
     * The runnable object which is executed by the subscriptionThread.
     */
    private Runnable runner = new Runnable() {
	public void run() {
	    Log.i(TAG, String.format("Subscribing to channel %s", channelName));

	    // Subscribe to channel.
	    // NOTE: Will block until interrupted.
	    pubnub.subscribe(channelName, callback);

	    Log.i(TAG, String.format("Unsubscribing", channelName));

	    // Interrupted. Service shutting down. Unsubscribe from channel.
	    HashMap<String, Object> map = new HashMap<String, Object>(1);
	    map.put("channel", channelName);
	    pubnub.unsubscribe(map);
	}
    };

    /**
     * PubNub 3.3 callback.
     */
    private Callback callback = new Callback() {
	public boolean subscribeCallback(String channel, Object message) {
	    Log.i(TAG, String.format("New message on channel '%s'", channel));
	    Log.i(TAG, message.toString());

	    // Message and data bindle which will be sent to "parent" process
	    // via the handler.
	    Message msg = new Message();
	    Bundle bundle = new Bundle();

	    try {
		// JSONArray jsonArray = new JSONArray(message.toString());
		JSONObject obj = new JSONObject(message.toString());

		// Assume 'Simple Push' type. Get subject/message
		String subject = obj.getString("subject");
		String messageText = obj.getString("message");

		Log.d(TAG, String.format("Subject/Message: %s - %s", subject, messageText));

		bundle.putInt("msg_type", PushMessageType.Simple.getValue());
		bundle.putString("subject", subject);
		bundle.putString("message", messageText);

	    } catch (JSONException e) {
		Log.e(TAG, "Received non-JSON message");
		return true;
	    }

	    msg.setData(bundle);

	    // If handler == null, no application has bound/registered a handler
	    // yet.
	    if (handler != null)
		handler.sendMessage(msg);

	    return true;
	}

	public void reconnectCallback(String channel) {
	}

	public boolean presenceCallback(String channel, Object message) {
	    return false;
	}

	public void errorCallback(String channel, Object message) {
	}

	public void disconnectCallback(String channel) {
	}

	public void connectCallback(String channel) {
	}
    };

    /**
     * Class which allows an application to bind to this service and register a
     * handler.
     */
    public class PushServiceBinder extends Binder implements IPushServiceMonitor {
	/**
	 * Sets the PushService.handler to the provided handler. This handler
	 * will be used to notify the external application of a received
	 * message.
	 */
	public void registerHandler(Handler h) {
	    Log.i(TAG, "Registering handler");
	    handler = h;
	}

	/**
	 * Sets the PushService.handler to null. No application will be notified
	 * of any push notifications.
	 */
	public void unregisterHandler() {
	    Log.i(TAG, String.format("Unsubscribing from channel: %s", channelName));

	    handler = null;
	}
    }
}
