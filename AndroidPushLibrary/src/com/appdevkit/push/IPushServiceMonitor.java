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

import android.os.Handler;

/**
 * Interface to the AppDevKit push service. Provides the ability to register and
 * unregister handlers.
 */
public interface IPushServiceMonitor {
    void registerHandler(Handler handler);

    void unregisterHandler();
}
