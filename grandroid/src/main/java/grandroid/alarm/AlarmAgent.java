/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import grandroid.database.FaceData;
import grandroid.database.GenericHelper;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public class AlarmAgent {

    public static final String ACTION_ALARM = "grandroid.action.SCHEDULED_TASK";
    static final String DB_NAME = "grandroid_alarm";
    protected Context context;
    protected FaceData fd;
    protected GenericHelper<AlarmTask> helper;
    protected Class receiverClass;

    public AlarmAgent(Context context) {
        this.context = context;
        fd = new FaceData(context, DB_NAME);
        helper = new GenericHelper<AlarmTask>(fd, AlarmTask.class);
        try {
            ActivityInfo[] receivers = context.getPackageManager().getPackageInfo(context.getPackageName(), 2).receivers;
            if (receivers != null) {
                for (ActivityInfo ai : receivers) {
                    //Log.d("grandroid", "ai package=" + ai.packageName + ", name=" + ai.name + ", target activity=" + ai.targetActivity);
                    try {
                        Class c = Class.forName(ai.name);
                        if (AlarmReceiver.class.isAssignableFrom(c)) {
                            receiverClass = c;
                        }
                    } catch (ClassNotFoundException ex) {
                        Log.e("grandroid", null, ex);
                    }
                }
                if (receiverClass == null) {
                    Log.e("grandroid", "you have to make your own AlarmReceiver and declare in AndroidManifest.xml");
                }
            }
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e("grandroid", null, ex);
        }
    }

    public Integer setAlarm(long triggerTime, JSONObject data) {
        try {
            AlarmTask task = createAlarmTask(triggerTime, data);
            PendingIntent pi = createPendingIntent(task.get_id());
            if (pi != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, task.getTime(), pi);
                //Log.d("grandroid", "register alarm success");
                return task.get_id();
            } else {
                deleteAlarm(task.get_id());
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return null;
    }

    public Integer setIntervalAlarm(long triggerTime, long interval, JSONObject data) {
        try {
            AlarmTask task = createAlarmTask(triggerTime, interval, data);
            PendingIntent pi = createPendingIntent(task.get_id());
            if (pi != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, task.getTime(), task.getInterval(), pi);
                //Log.d("grandroid", "register alarm success");
                return task.get_id();
            } else {
                deleteAlarm(task.get_id());
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return null;
    }

    public boolean cancelAllAlarm() {
        List<AlarmTask> tasks = helper.select();
        for (AlarmTask task : tasks) {
            deleteAlarm(task.get_id());
            PendingIntent pi = createPendingIntent(task.get_id());
            if (pi != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.cancel(pi);
            }
        }
        return true;
    }

    public boolean cancelAlarm(Integer taskID) {
        AlarmTask task = helper.selectSingle(taskID);
        if (task != null) {
            deleteAlarm(taskID);
            PendingIntent pi = createPendingIntent(task.get_id());
            if (pi != null) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.cancel(pi);
                return true;
            }
        }
        return false;
    }

    void reschedule() {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (AlarmTask task : getAllAlarmTasks()) {
            PendingIntent pi = createPendingIntent(task.get_id());
            if (pi != null) {
                if (task.getInterval() == 0) {
                    am.set(AlarmManager.RTC_WAKEUP, task.getTime(), pi);
                } else if (task.getInterval() > 0) {
                    long start = task.getTime();
                    if (task.getInterval() >= 60000L) {
                        while (start < System.currentTimeMillis()) {
                            start += task.getInterval();
                        }
                    }
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, start, task.getInterval(), pi);
                }
            }
        }
    }

    boolean deleteAlarm(Integer taskID) {
        return helper.delete(taskID);
    }

    protected AlarmTask createAlarmTask(Long time, JSONObject data) throws Exception {
        return createAlarmTask(time, 0L, data);
    }

    protected AlarmTask createAlarmTask(Long time, Long interval, JSONObject data) throws Exception {
        AlarmTask task = new AlarmTask();
        task.setJson(data);
        task.setTime(time);
        task.setInterval(interval);
        if (helper.insert(task)) {
            return task;
        } else {
            throw new Exception("fail to create AlarmTask");
        }
    }

    protected PendingIntent createPendingIntent(Integer taskID) {
        if (receiverClass != null) {
            Intent intent = new Intent(context, receiverClass);
            intent.setAction(ACTION_ALARM);
            //intent.setData(Uri.parse("grandroid://alarm/?id=" + taskID));
            intent.addCategory(String.valueOf(taskID));
            PendingIntent pi = PendingIntent.getBroadcast(context, taskID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return pi;
        } else {
            return null;
        }
    }

    public ArrayList<AlarmTask> getAllAlarmTasks() {
        return helper.select();
    }

    public AlarmTask getAlarmTask(Integer taskID) {
        return helper.selectSingle(taskID);
    }
}
