/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rovers
 */
public class GeoLocator implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected boolean connected;
    protected List<LocationResult> callbacks;//採用Observer Pattern
    protected long maxWaiting;
    protected long maxTimeout;
    protected long interval;
    protected Context context;
    protected boolean locateAfterConnected;

    public GeoLocator(Context context) {
        this.context = context;
        this.maxWaiting = maxWaiting;
        this.maxTimeout = 5000;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        callbacks = new CopyOnWriteArrayList<LocationResult>();
    }

    public long getInterval() {
        return interval;
    }

    public Location getLastLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            return null;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void start() {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    connected = true;
                    if(!callbacks.isEmpty()) {
                        locateAfterConnected = true;
                    }
                }
            });
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

    public void stop() {
        interval = 0;
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
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

    public void onConnected(Bundle bundle) {
        if (locateAfterConnected) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
/*            if (interval == 0) {
                locateCustom(LocationRequest.create().setInterval(0), listener);
            } else {
                locateCustom(LocationRequest.create().setInterval(interval), listener);
            }*/
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onDisconnected() {
    }

    public void onConnectionFailed(ConnectionResult cr) {
        Log.e("grandroid", "error while connect to google map serivce: " + cr.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        for (int i = callbacks.size() - 1; i >= 0; i--) {
            LocationResult result = callbacks.get(i);
            boolean keep = result.gotLocation(location);
            result.used();
            if (!keep || result.isExceedCount()) { //有任何一個callback回傳false
                //移除該callback
                callbacks.remove(i);
            }
        }
        if (callbacks.isEmpty()) {
            interval = 0;
            //stop request locating
            stop();
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

    }
}
