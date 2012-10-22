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

/**
 * Message type list.
 */
public enum PushMessageType {
    /**
     * A simple JSON push. Message structure:
     * 
     * <pre>
     * {
     *    "subject":"Subject of the message",
     *    "message":"This is the message text."
     * }
     * </pre>
     */
    Simple(1);

    private final int value;

    private PushMessageType(int value) {
	this.value = value;
    }

    public int getValue() {
	return this.value;
    }
}
