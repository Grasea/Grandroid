/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import grandroid.action.AsyncAction;
import grandroid.geo.LocationResult;

/**
 *
 * @author Rovers
 */
public class GeoLocator implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    protected LocationClient lm;
    protected List<LocationResult> callbacks;//採用Observer Pattern
    protected long maxWaiting;
    protected long maxTimeout;
    protected long interval;
    protected Context context;
    protected boolean locateAfterConnected;
    protected LocationListener listener;

    public GeoLocator(Context context) {
        this(context, 3000);
    }

    public GeoLocator(Context context, long maxWaiting) {
        this.context = context;
        if (maxWaiting < 20) {
            maxWaiting = maxWaiting * 1000;
        }
        this.maxWaiting = maxWaiting;
        this.maxTimeout = 5000;
        callbacks = new CopyOnWriteArrayList<LocationResult>();
        if (lm == null) {
            lm = new LocationClient(context, this, this);
        }
        listener = new LocationListener() {
            public void onLocationChanged(Location lctn) {

                for (int i = callbacks.size() - 1; i >= 0; i--) {
                    LocationResult result = callbacks.get(i);
                    boolean keep = result.gotLocation(lctn);
                    result.used();
                    if (!keep || result.isExceedCount()) { //有任何一個callback回傳false
                        //移除該callback
                        callbacks.remove(i);
                    }
                }
                if (callbacks.isEmpty()) {
                    interval = 0;
                    if (lm.isConnected()) {
                        stop();
                    }

                }
            }
        };
    }

    public long getMaxWaiting() {
        return maxWaiting;
    }

    public GeoLocator setMaxWaiting(long maxWaiting) {
        this.maxWaiting = maxWaiting;
        return this;
    }

    public long getMaxTimeout() {
        return maxTimeout;
    }

    public GeoLocator setMaxTimeout(long maxTimeout) {
        this.maxTimeout = maxTimeout;
        return this;
    }

    public long getInterval() {
        return interval;
    }

    public GeoLocator setInterval(long interval) {
        if (this.interval > 0) {
            if (interval > 0) {
                locateCustom(LocationRequest.create().setInterval(interval), listener);
            } else {
                cancelAllCallback();
            }
        }
        this.interval = interval;
        return this;
    }

    public Location getLastLocation() {
        if (lm.isConnected()) {
            return lm.getLastLocation();
        } else {
            return null;
        }
    }

    public void useLastLocation(final LocationResult locResult) {
        new AsyncAction<Boolean>(context) {

            @Override
            public void afterExecution(Boolean r) {
                if (r) {
                    locResult.gotLocation(getLastLocation());
                    stop();
                }
            }

            @Override
            public boolean execute(Context context) {
                setResult(waitingConnection());
                return true;
            }

        }.execute();
    }

    public boolean isConnected() {
        return lm != null && lm.isConnected();
    }

    public void start() {
        this.locateAfterConnected = true;
        if (lm != null) {
            if (!lm.isConnected() && !lm.isConnecting()) {
                lm.connect();
            }
        }
    }
//
//    public void startAndLocateOnce(boolean locateAfterConnected) {
//        this.locateAfterConnected = locateAfterConnected;
//        if (lm != null) {
//            if (!lm.isConnected() && !lm.isConnecting()) {
//                lm.connect();
//            }
//        }
//    }
//
//    public void startAndLocateContinuous(long interval) {
//        this.interval = interval;
//        this.locateAfterConnected = true;
//        if (lm != null) {
//            if (!lm.isConnected() && !lm.isConnecting()) {
//                lm.connect();
//            }
//        }
//    }

    public List<LocationResult> getLocationCallbacks() {
        return callbacks;
    }

    public GeoLocator addLocationCallback(LocationResult result) {
        callbacks.add(result);
        return this;
    }

    public GeoLocator removeLocationCallback(LocationResult result) {
        callbacks.remove(result);
        if (callbacks.isEmpty()) {
            interval = 0;
            lm.removeLocationUpdates(listener);
        }
        return this;
    }

    public void cancelAllCallback() {
        callbacks.clear();
        if (lm.isConnected()) {
            lm.removeLocationUpdates(listener);
        }
        interval = 0;
    }

    public void stop() {
        interval = 0;
        if (lm.isConnected() || lm.isConnecting()) {
            lm.removeLocationUpdates(listener);
            lm.disconnect();
        }
    }

    /**
     * 用在onCreated之內，地圖初始化完之後，執行一次性的定位工作 之後還是要呼叫start()
     *
     * @param result
     */
    public void addLocatingJob(LocationResult result) {
        addLocationCallback(result);
    }

    /**
     * 用在onCreated之內，地圖初始化完之後，開啟定時追蹤 之後還是要呼叫start()
     *
     * @param result
     * @param interval
     */
    public void addLocatingJob(LocationResult result, long interval) {
        this.interval = interval;
        result.reset();
        if (result.getMaxCount() <= 1) {
            result.setMaxCount(Integer.MAX_VALUE);
        }
        addLocationCallback(result);
    }

    protected void locateCustom(LocationRequest request, final LocationListener listener) {
        if (waitingConnection()) {
            if (request.getNumUpdates() > 3) {
                interval = request.getInterval();
            }
            lm.requestLocationUpdates(request, listener);
        }
    }
//
//    public void locateInterval(LocationResult result, long interval) {
//        if (waitingConnection() && !callbacks.contains(result)) {
//            this.interval = interval;
//            result.setMaxCount(Integer.MAX_VALUE);
//            addLocationCallback(result);
//            lm.requestLocationUpdates(LocationRequest.create().setInterval(interval), listener);
//        }
//    }
//
//    /**
//     * synchronize method, waiting for connected
//     *
//     * @param result
//     */
//    public void locateOnce(LocationResult result) {
//        if (waitingConnection()) {
//            if (interval > 0) {
//                result.gotLocation(lm.getLastLocation());
//            } else {
//                result.setMaxCount(1);
//                addLocationCallback(result);
//                lm.requestLocationUpdates(LocationRequest.create().setNumUpdates(1), listener);
//                //執行完後自然會從callbacks裡刪除
//            }
//        } else {
//            Log.e("grandroid", "cannot connect lm");
//        }
//    }

    /**
     * after calling start(), you can call this method to request locating job
     * manually.
     *
     * @return if request successfully
     */
    public boolean requestLocating() {
        if (lm.isConnected()) {
            locateCustom(LocationRequest.create().setInterval(0), listener);
            return true;
        }
        return false;
    }

    protected boolean waitingConnection() {
        long startTime = System.currentTimeMillis();
        if (!lm.isConnected() && !lm.isConnecting()) {
            lm.connect();
        }

        while (!lm.isConnected() && System.currentTimeMillis() - startTime < (maxTimeout)) {
            try {
                Thread.currentThread().sleep(100);
                Log.d("grandroid", "wating for map service connected");
            } catch (InterruptedException ex) {
                Log.e("grandroid", null, ex);
            }
        }
        return lm.isConnected();
    }

    public void onConnected(Bundle bundle) {
        if (locateAfterConnected && callbacks.size() > 0) {
            if (interval == 0) {
                locateCustom(LocationRequest.create().setInterval(0), listener);
            } else {
                locateCustom(LocationRequest.create().setInterval(interval), listener);
            }
        }
    }

    public void onDisconnected() {
    }

    public void onConnectionFailed(ConnectionResult cr) {
        Log.e("grandroid", "error while connect to google map serivce: " + cr.toString());
    }
}
