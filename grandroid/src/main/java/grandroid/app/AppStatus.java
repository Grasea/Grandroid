/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

/**
 * 記錄app的狀態，目前只有記錄「app是否已被結束」
 *
 * @author Rovers
 */
public class AppStatus {

    /**
     * 記錄目前app是否正顯示中
     */
    public static boolean ON_TOP = false;

    public static String getMetaData(Context cntext, String name) {
        try {
            ApplicationInfo ai = cntext.getPackageManager().getApplicationInfo(cntext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            if (metaData != null) {
                if (metaData.containsKey(name)) {
                    return metaData.get(name).toString();
                }
            }
        } catch (NameNotFoundException e) {
            Log.e("grandroid", null, e);
        }
        return "";
    }

    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
