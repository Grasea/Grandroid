/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.phone;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import grandroid.data.DataAgent;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Rovers
 */
public class PhoneUtil {

    public final static String KEY_GRNADROID_INSTALLATION_ID = "GRANDROID_INSTALLATION_ID";

    /**
     *
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable();
    }

    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 需要權限<uses-permission android:name="android.permission.READ_PHONE_STATE" />
     *
     * @param context
     * @return
     */
    public static String getDeviceID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = tm.getDeviceId();
        if (id == null || id.length() == 0) {
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 只能用於背景執行緒呼叫
     *
     * @param context
     * @return
     */
    public static String getAdvertisingID(final Context context) {
        final ArrayList<String> list = new ArrayList<String>();
        Thread t = new Thread(new Runnable() {

            public void run() {
                Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    String adid = adInfo.getId();
                    list.add(adid);
                    final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                } catch (IOException e) {
                    // Unrecoverable error connecting to Google Play services (e.g.,
                    // the old version of the service doesn't support getting AdvertisingId).
                    Log.e("grandroid", null, e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Google Play services is not available entirely.
                    Log.e("grandroid", null, e);
                } catch (IllegalStateException e) {
                    Log.e("grandroid", null, e);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("grandroid", null, e);
                }
            }

        });
        try {
            t.start();
            //latch.await();
            t.join();
        } catch (InterruptedException ex) {
            Log.e("grandroid", null, ex);
        }

        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 可用於前景及背景執行緒
     *
     * @param context
     * @param callback
     */
    public static void requestAdvertisingID(final Context context, final StringCallback callback) {
        Thread t = new Thread(new Runnable() {

            public void run() {
                Info adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    final String adid = adInfo.getId();
                    final boolean isLAT = adInfo.isLimitAdTrackingEnabled();
                    callback.doInBackgroundThread(context, adid);
                    if (context instanceof Activity) {
                        ((Activity) context).runOnUiThread(new Runnable() {

                            public void run() {
                                callback.doInMainThread((Activity) context, adid);
                            }
                        });
                    }
                } catch (IOException e) {
                    // Unrecoverable error connecting to Google Play services (e.g.,
                    // the old version of the service doesn't support getting AdvertisingId).
                    Log.e("grandroid", null, e);
                    callback.caughtException(context, e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Google Play services is not available entirely.
                    Log.e("grandroid", null, e);
                    callback.caughtException(context, e);
                } catch (IllegalStateException e) {
                    Log.e("grandroid", null, e);
                    callback.caughtException(context, e);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e("grandroid", null, e);
                    callback.caughtException(context, e);
                }
            }

        });
        t.start();
    }

    /**
     * 取回安裝當時的Advertising ID。安裝後第一次呼叫時需要使用背景執行緒來執行此方法。 如果第一次呼叫時，裝置因Google Play
     * Service不存在或太舊導致取得ID失敗，則使用Device ID代替。 此時若App未具有READ_PHONE_STATE權限，則會發生錯誤。
     *
     * @param context
     * @return
     */
    public static String getInstallationID(Context context) {
        DataAgent da = new DataAgent(context);
        if (da.getPreferences().contains(KEY_GRNADROID_INSTALLATION_ID)) {
            return da.getPreference(KEY_GRNADROID_INSTALLATION_ID);
        } else {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                String id = getAdvertisingID(context);
                if (id != null) {
                    da.putPreference(KEY_GRNADROID_INSTALLATION_ID, id);
                    Log.d("grandroid", "use Advertising ID as Installation ID");
                    return id;
                }
            }
            String id = getDeviceID(context);
            if (id != null) {
                Log.d("grandroid", "use Device ID as Installation ID");
                da.putPreference(KEY_GRNADROID_INSTALLATION_ID, id);
                return id;
            } else {
                return null;
            }
        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(final Activity activity, final View view) {
        view.post(new Runnable() {
            public void run() {
//                if (view.requestFocus()) {
//                    activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(view, 0);
                }
            }
        });

    }

    public static class StringCallback {

        public void doInBackgroundThread(Context context, String str) {

        }

        public void doInMainThread(Activity activity, String str) {

        }

        public void caughtException(Context context, Exception ex) {

        }
    }
}
