AppDevKit.com Push Notifications for Android
============================================

Android library and demo for receiving push notifications from https://www.AppDevKit.com.  You MUST have an account on AppDevKit first.

Usage
-----
	
1. Import AndroidPushLibrary into Eclipse.
2. Right click on your Android project and select 'Properties'.
3. Click 'Android'.
4. Under the 'Libraries' section click 'Add...'.  
5. Select 'AndroidPushLibrary' and click 'Ok'. Click 'Ok' to close the properties window.
6. Add the following to you AndroidManifest.xml file:
	
```xml
<manifest>

	<!-- Other declarations -->

	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
	
	<application>
		<!-- Other activites and services -->
		
		<service
			android:name="com.appdevkit.push.PushService"
			android:enabled="true"
			android:exported="false" >
		</service>

		<receiver android:name="com.appdevkit.push.PushServiceReceiver" >
			<intent-filter>
				<action android:name="android.intent.action.BOOT_COMPLETED" />
			</intent-filter>
		</receiver>
	</application>
</manifest>
```

7. In your res/values folder create a new xml file named 'adk.xml' and add the following contents.  NOTE: Be sure to update the 'adk_channel_name' field with the channel name generated for you app on AppDevKit.com.

```xml
<resources xmlns:tools="http://schemas.android.com/tools" tools:ignore="TypographyDashes">
	<!-- ADK Production subscription key -->
    <string name="adk_subscriber_key">sub-c-20cd0eb0-1b7c-11e2-8d2e-15ce9941f8cb</string>
    <!-- ADK App Channel Name -->
  	<string name="adk_channel_name">UPDATE-TO-YOUR-CHANNEL-NAME</string>
</resources>
```

8. In your main activity, create a new android.os.Handler object.  This will receive the push notification messages from the PushService.
	
```java
private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		
		// Assume 'Simple' push. Contains subject and message.
	    int msgType = msg.getData().getInt("msg_type");
	    String subject = msg.getData().getString("subject");
	    String messageText = msg.getData().getString("message");

	    String message = String.format("Msg Type: %s\nSubject: %s\nMessage: %s", msgType, subject, messageText);

	    Toast.makeText(PushDemo.this, message, Toast.LENGTH_LONG).show();
	}
};
```

9. Also in your main activity, create a new android.content.ServiceConnection object.  This will allow you to register the handler with the service.
	
```java
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
```

10. When your application starts, bind to the PushService in the onCreate(Bundle) method.  Unregister the handler and unbind to the Push service in the onDestroy() method.
	
```java
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
```

That's it!  Now when you send a 'Push' from AppDevKit.com the PushService will receive it and pass it to your handler.  This allows you to handle it however you'd like.

API Documentation
-----------------
	https://www.AppDevKit.com/api/docs

License
-------

	Copyright 2012 MG2 Innovations (http://www.mg2innovations.com)
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.