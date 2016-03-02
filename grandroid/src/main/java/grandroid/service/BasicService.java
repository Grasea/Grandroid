/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import grandroid.data.DataAgent;

/**
 * @author Rovers
 */
public class BasicService extends Service {

    /**
     *
     */
    protected DataAgent dataAgent;

    /**
     * @param arg0
     * @return
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * @return
     */
    public DataAgent getData() {
        if (dataAgent == null) {
            dataAgent = new DataAgent(this);
        }

        return dataAgent;
    }
}
