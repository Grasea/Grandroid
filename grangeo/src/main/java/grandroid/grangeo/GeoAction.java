/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import grandroid.action.Action;
import grandroid.action.ContextAction;
import grandroid.geo.LocationResult;

/**
 *
 * @author Rovers
 */
public abstract class GeoAction extends ContextAction {

    /**
     *
     */
    protected Handler handler;
    protected GeoLocator locator;

    public GeoAction(Context context, String message) {
        this(context, "", message, null);
    }

    public GeoAction(Context context, String message, final Action callback) {
        this(context, "", message, callback);
    }

    /**
     *
     * @param context
     * @param actionName
     * @param message
     * @param callback
     */
    public GeoAction(Context context, String actionName, String message, final Action callback) {
        super(context, actionName);
        locator = new GeoLocator(context);

        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage(message);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progress.dismiss();
                if (callback != null) {
                    callback.execute();
                }
            }
        };

        progress.show();
        locator.addLocatingJob(new LocationResult() {
            @Override
            public boolean gotLocation(Location location) {
                if (args == null) {
                    args = new Object[1];
                }
                args[0] = location;
                execute(GeoAction.this.context);
                return false;
            }
        },5000);
        locator.start();
    }

    @Override
    public final boolean execute(Context context) {
        boolean result = execute(context, (Location) args[0]);
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
        return result;
    }

    public abstract boolean execute(Context context, Location location);
}
