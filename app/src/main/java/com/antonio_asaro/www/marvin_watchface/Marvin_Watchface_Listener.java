/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.antonio_asaro.www.marvin_watchface;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

/**
 * A {@link WearableListenerService} listening for {@link Marvin_Watchface_Service} config messages
 * and updating the config {@link com.google.android.gms.wearable.DataItem} accordingly.
 */
public class Marvin_Watchface_Listener extends WearableListenerService {
    private static final String TAG = "Marvin_Watchface_List";
    private static final int FORGOT_PHONE_NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "onCapabilityChanged()");
    }

    @Override
    public void onConnectedNodes(List<Node> connectedNodes) {
        Log.d(TAG, "onConnectedNodes()");
    }

    @Override
    public void onPeerDisconnected(com.google.android.gms.wearable.Node peer) {
        if (Marvin_Watchface_Service.getmEngine().mCheckBT == 1) {
        Log.d(TAG, "onPeerDisconnected()");
            Notification.Builder notificationBuilder = new Notification.Builder(this)
                    .setContentTitle("Bluetooth disconnected")
                    .setContentText("out of range?")
                    .setSmallIcon(R.drawable.launcher_icon)
                    .setLocalOnly(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_MAX);
            Notification card = notificationBuilder.build();
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .notify(FORGOT_PHONE_NOTIFICATION_ID, card);
        }
    }

    @Override
    public void onPeerConnected(com.google.android.gms.wearable.Node peer) {
        if (Marvin_Watchface_Service.getmEngine().mCheckBT == 1) {
        Log.d(TAG, "onPeerConnected()");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .cancel(FORGOT_PHONE_NOTIFICATION_ID);
        }
    }

}
