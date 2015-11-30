/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import grandroid.action.Action;
import java.util.List;

/**
 *
 * @author Rovers
 */
public class GPSUtil {

    /**
     *
     */
    protected static double lon;
    /**
     *
     */
    protected static double lat;

    /**
     * 取得最後的位置
     *
     * @param context
     * @return [0]:Latitude [1]:Longitude，若完全無資料則回傳null
     */
    public static Location getLastPosition(Context context) {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        /*
         * Loop over the array backwards, and if you get an accurate location,
         * then break out the loop
         */
        Location l = null;

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        //String provider = lm.getBestProvider(criteria, true);
        if (providers != null) {
            for (int i = providers.size() - 1; i >= 0; i--) {
                Location loc = lm.getLastKnownLocation(providers.get(i));
                if (loc != null) {
                    if (l == null) {
                        l = loc;
                    } else {
                        if (l.getTime() < loc.getTime()) {
                            l = loc;
                        }
                    }
                }
            }
        }
        return l;
    }

    /**
     *
     * @param context
     * @param action callback物件。action裡的args陣列會有2個元素(Double)，[0]:Latitude
     * [1]:Longitude
     * @param repeat
     */
    public static void locate(final Context context, final Action action, final boolean repeat) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // Register the listener with the Location Manager to receive location updates
        String bestProvider = locationManager.getBestProvider(locationCriteria, true);
        if (bestProvider == null) {
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "未開啟AGPS定位功能", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            locationManager.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {
                boolean called;

                public synchronized void onLocationChanged(Location location) {
                    if (!called) {
                        called = true;
                        if (!repeat) {
                            locationManager.removeUpdates(this);
                        }
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        Log.d("grandroid", "change to location (" + lat + "," + lon + ")");
                        if (action != null) {
                            action.setArgs(location.getLatitude(), location.getLongitude(), location).execute();
                        }
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            });
        }
    }
}
