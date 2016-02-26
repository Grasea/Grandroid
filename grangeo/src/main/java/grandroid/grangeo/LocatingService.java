/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import grandroid.action.ThreadAction;
import grandroid.geo.LocationResult;
import grandroid.service.HandlerService;

/**
 *
 * @author Rovers
 */
public abstract class LocatingService extends HandlerService {

    protected boolean serviceResult = true;
    protected GeoLocator locator;
    protected double lastLat;
    protected double lastLon;

    @Override
    public void onCreate() {
        super.onCreate();
        locator = new GeoLocator(this);
    }

    @Override
    protected boolean execute() {
        //Log.d("grandroid", "on Loactioning service execute");
        if (locator != null) {
            LocationResult lr = new LocationResult() {
                @Override
                public boolean gotLocation(final Location location) {
                    //Log.d("grandroid", "got position");
                    new ThreadAction(LocatingService.this, 0) {
                        @Override
                        public boolean execute(Context cntxt) {
                            //Log.d("grandroid", "on Loaction Result");
                            serviceResult = LocatingService.this.execute(location.getLatitude(), location.getLongitude(), (int) location.getSpeed(), (int) location.getBearing());
                            if (!serviceResult) {
                                Log.d("grandroid", "locating service will stop");
                                locator = null;
                                handler.removeCallbacks(run);
                                stopSelf();
                            }
                            return true;
                        }
                    };

                    return true;
                }
            };
            if (locator.isConnected()) {
                locator.requestLocating();
            } else {
                locator.addLocatingJob(lr.once());
                locator.start();
            }

            return true;
        } else {
            return false;
        }
    }

    protected abstract boolean execute(double lat, double lon, int speed, int azimuth);

    @Override
    public void onDestroy() {
        locator.stop();
        super.onDestroy();
    }

    protected boolean supportGPS() {
        return true;
    }
}
