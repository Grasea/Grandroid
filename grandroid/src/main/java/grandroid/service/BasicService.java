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
import android.os.Bundle;
import android.os.IBinder;
import grandroid.data.DataAgent;

/**
 *
 * @author Rovers
 */
public class BasicService extends Service {

    /**
     * 
     */
    protected DataAgent dataAgent;

    /**
     * 
     * @param arg0
     * @return
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * 
     * @return
     */
    public DataAgent getData() {
        if (dataAgent == null) {
            dataAgent = new DataAgent(this);
        }

        return dataAgent;
    }

    /**
     * 
     * @param icon
     * @param contentTitle
     * @param contentText
     * @param targetFrame
     * @param bundle
     */
    public void notify(int icon, String contentTitle, String contentText, Class targetFrame, Bundle bundle) {
        NotificationManager manager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification no = new Notification(icon, contentTitle, System.currentTimeMillis());
        no.flags = Notification.FLAG_AUTO_CANCEL;
        Intent notificationIntent = new Intent(this, targetFrame);
        notificationIntent.putExtras(bundle);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        no.setLatestEventInfo(this, contentTitle, contentText, contentIntent);
        manager.notify(0, no);
    }
}
