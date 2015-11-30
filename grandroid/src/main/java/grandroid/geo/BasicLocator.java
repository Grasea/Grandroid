/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Rovers
 */
public class BasicLocator {

    //Timer timer1;
    Timer timer2;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    int maxWaitingSecond;
    boolean isCalled;
    Context context;
    long lastCalledTime;
    Location lastLocation;
    int countdown = 10;

    public BasicLocator(Context context) {
        this(context, 12, true, true);
    }

    public BasicLocator(Context context, int maxWaitingSecond, boolean gps_enabled, boolean network_enabled) {
        this.context = context;
        this.maxWaitingSecond = maxWaitingSecond;
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        //exceptions will be thrown if provider is not permitted.
        try {
            this.gps_enabled = gps_enabled && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            this.gps_enabled = false;
        }
        try {
            this.network_enabled = network_enabled && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            this.network_enabled = false;
        }
    }

    public long getLastCalledTime() {
        return lastCalledTime;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public boolean isGPSEnabled() {
        return gps_enabled;
    }

    public boolean isNetworkEnabled() {
        return network_enabled;
    }

    public boolean start(LocationResult result) {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        locationResult.once();
        Log.d("grandroid", "start to locate...");
        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            Log.e("grandroid", "no any device enable to detect location");
            result.onNoDeviceSupport();
            return false;
        }
        locate();
        return true;
    }

    private void requestLocating(final LocationResult result) {
        if (lastLocation != null && System.currentTimeMillis() - lastLocation.getTime() < 20000) {
            result.gotLocation(lastLocation);
            return;
        }
        countdown = maxWaitingSecond;
        if (timer2 != null) {
            timer2.cancel();
        }
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.d("grandroid", "check relocate position...");
                //timer2 = null;
                countdown--;
                if (lastLocation != null && System.currentTimeMillis() - lastLocation.getTime() < 20000) {
                    timer2.cancel();
                    result.gotLocation(lastLocation);

                }
                if (countdown <= 0) {
                    timer2.cancel();
                    Location loc1 = null;
                    if (gps_enabled && lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        loc1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }

                    Location loc2 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (loc1 != null && System.currentTimeMillis() - loc1.getTime() < 60000) {
                        result.gotLocation(loc1);
                    } else if (loc2 != null && System.currentTimeMillis() - loc2.getTime() < 60000) {
                        result.gotLocation(loc2);
                    } else if (loc1 != null || loc2 != null) {
                        result.gotLocation(loc1 == null ? loc2 : loc1);
                    }
                }
            }
        }, 100, 1000);

    }

    public void stop() {
        lm.removeUpdates(locationListenerNetwork);
        lm.removeUpdates(locationListenerGps);
        if (timer2 != null) {
            try {
                timer2.cancel();
            } catch (Exception ex) {
            }
        }
        Log.d("grandroid", "locating job is stoped");
    }

    protected void locate() {
        Log.d("grandroid", "now start locating..." + gps_enabled + "," + network_enabled + ", gps=" + locationListenerGps + ", net=" + locationListenerNetwork);

        if (gps_enabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }
        if (network_enabled) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }
    }

    public synchronized void beforeCallback(Location location, boolean isGPS) {
        if (isGPS) {
            lastLocation = location;
        } else {
            if (lastLocation == null || System.currentTimeMillis() - lastLocation.getTime() > 120000) {
                lastLocation = location;
            }
        }
        if (lastLocation != null) {
            lastCalledTime = System.currentTimeMillis();
            Log.d("grandroid", "call gotLocation: gps=" + isGPS);
            boolean keepLocating = locationResult.gotLocation(location);
            locationResult.used();
            if (!keepLocating || locationResult.isExceedCount()) {
                stop();
            }
        }

    }
    LocationListener locationListenerGps = new LocationListener() {

        public void onLocationChanged(Location location) {
            beforeCallback(location, true);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    LocationListener locationListenerNetwork = new LocationListener() {

        public void onLocationChanged(Location location) {
            beforeCallback(location, false);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
}
