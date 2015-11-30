/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Calendar;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public abstract class AlarmReceiver extends BroadcastReceiver {

    protected AlarmAgent agent;

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (agent == null) {
            agent = new AlarmAgent(context);
        }
        String action = intent.getAction();
        //Log.d("grandroid", "got action: " + action);
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            //re-schedule
            agent.reschedule();
        } else if (AlarmAgent.ACTION_ALARM.equals(action)) {
            //do scheduled task
            String taskID = intent.getCategories().iterator().next();
            //Log.d("grandroid", "task id=" + taskID);
            AlarmTask task = agent.getAlarmTask(Integer.parseInt(taskID));
            if (task != null) {
                execute(context, agent, task.getJson());
                if (task.getInterval() == 0) {
                    agent.deleteAlarm(task.get_id());
                }
            }
        }
    }

    /**
     *
     * @param context
     * @param agent
     * @param json
     */
    protected abstract void execute(Context context, AlarmAgent agent, JSONObject json);
}
