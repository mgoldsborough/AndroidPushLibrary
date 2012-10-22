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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receiver to start app on boot. Add the following to your AndroidManifest.xml
 * file:
 * 
 * <pre>
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * <application>
 *     <receiver android:name="com.appdevkit.push.PushServiceReceiver" >
 *         <intent-filter>
 *             <action android:name="android.intent.action.BOOT_COMPLETED" />
 *         </intent-filter>
 *     </receiver>
 * </application>
 * </pre>
 */
public class PushServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "PushServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
	Log.i(TAG, "Starting ADK PushService");

	Intent service = new Intent(context, PushService.class);
	context.startService(service);
    }
}
