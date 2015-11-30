/*
 * AndroidManifest.xml必須修改
 * <receiver android:name="grandroid.service.MessageReceiver">
 * <intent-filter>
 * <action android:name="android.provider.Telephony.SMS_RECEIVED"/>
 * </intent-filter>
 * </receiver>
 * <uses-permission android:name="android.permission.RECEIVE_SMS" />
 *
 */
package grandroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import grandroid.action.Action;
import grandroid.phone.SMSHelper;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 統一的訊息接收物件，若要使用需要修改AndroidManifest.xml，請參考本類別的原始碼開頭
 * 應透過Face類別的registerBundledAction來使用本類別
 *
 * @author Rovers
 */
public class MessageReceiver extends BroadcastReceiver {

    /**
     *
     */
    protected ConcurrentHashMap<String, Action> actionMap;

    /**
     * 應透過Face來生成本類別之實體
     */
    public MessageReceiver() {
        actionMap = new ConcurrentHashMap<String, Action>();
    }

    /**
     * 註冊各action事件至目標Context (本函數通常是由Face來呼叫)
     *
     * @param context
     */
    public void registerAllEvent(Context context) {
        //context.unregisterReceiver(this);
        for (String key : actionMap.keySet()) {
            context.registerReceiver(this, new IntentFilter(key));
        }
    }

    /**
     * 新增一個對應的事件及處理Action
     *
     * @param event Android廣播所用的Key
     * @param action Action物件
     */
    public void addEvent(String event, Action action) {
        actionMap.put(event, action);
    }

    /**
     * 查詢actionMap是否已含有某個event的key值
     *
     * @param event
     * @return 該event已存在於actionMap
     */
    public boolean containsEvent(String event) {
        return actionMap.containsKey(event);
    }

    /**
     * 本函數將被Android呼叫，然後自動依註冊過的event執行各自的Action 不應直接呼叫本函數
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (actionMap.containsKey(intent.getAction())) {
            Action action = actionMap.get(intent.getAction());
            if (intent.getExtras() != null) {
                action.setArgs(intent.getExtras());
            }
            if (intent.getAction().equals(SMSHelper.SMS_REC)) {
                String message = new SMSHelper().retrieveSMS(context, intent);
                action.setArgs(message).execute();
            } else {
                action.execute();
            }
        }

//        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
//            Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, "******************catched ACTION_USER_PRESENT action!!**************");
//            Toast.makeText(context, "catched ACTION_USER_PRESENT action!!", Toast.LENGTH_LONG).show();
//        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//            Logger.getLogger(MessageReceiver.class.getName()).log(Level.INFO, "******************catched ACTION_SCREEN_ON action!!**************");
//            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//            km.newKeyguardLock("DingSchool").disableKeyguard();
//
//        }
    }
}
