/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import grandroid.app.AppStatus;
import grandroid.service.MessageReceiver;
import grandroid.data.DataAgent;
import grandroid.action.Action;
import grandroid.action.AlertAction;
import grandroid.action.ContextAction;
import grandroid.action.NotifyAction;
import grandroid.action.PendingAction;
import grandroid.action.ToastAction;
import grandroid.dialog.CommandPickModel;
import grandroid.dialog.DateTimePickModel;
import grandroid.view.fragment.Component;
import grandroid.view.fragment.DataEvent;
import grandroid.view.fragment.DataEventHandler;
import grandroid.view.fragment.ObserverTarget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rovers
 */
public class NewFace extends FragmentActivity implements Pendable, DataEventHandler {

    /**
     *
     */
    protected ArrayList<Action> menuList;
    /**
     *
     */
    protected MessageReceiver bundledReceiver = null;
    /**
     *
     */
    protected int menuID;
    /**
     *
     */
    protected Menu menu;
    /**
     *
     */
    protected DataAgent dataAgent;
    /**
     *
     */
    protected DateTimePickModel model;
    protected ConcurrentHashMap<Integer, PendingAction> pendingActions;
    protected Bundle savedInstanceState;
    protected ConcurrentHashMap<String, ObserverTarget> observerMap;
    public static final String FRAGMENT_CLASS = "FRAGMENT_CLASS";
    protected boolean notifyLeaving;
    protected long leavingTime;
    /**
     *
     */
    protected Boolean mIsCanEixt = true;
    protected Boolean isRegistedListener = false;
    protected HashSet<Integer> backStackSet = new HashSet<>();
    private boolean needCheckBackStatck = false;
    private FragmentManager.OnBackStackChangedListener listener;
    private boolean isAddFragment = false;
    private int lastBackStackCount = 0;

    /**
     *
     */
    public NewFace() {
        super();
    }

    /**
     * 取得資料代理人，一般用途為保存view的資料，以及存取SharedPreference
     *
     * @return 資料代理人
     */
    public DataAgent getData() {
        if (dataAgent == null) {
            dataAgent = new DataAgent(this);
        }
        return dataAgent;
    }

    /**
     * 載入以view為單位的Layout XML (一般是以Activity為單位)
     *
     * @param resourceID
     * @return 具體化後的View物件
     */
    public View loadLayout(int resourceID) {
        LayoutInflater vi = this.getLayoutInflater();
        View vv = vi.inflate(resourceID, null, false);
        return vv;
        //layout.setBaselineAligned(disableLock)
        //layout.addView(vv, new LinearLayout.LayoutParams(layout.getLayoutParams().width, layout.getLayoutParams().height));
    }

//    /**
//     * 將view物件註冊為「需保存值資料」，同時載入前次的值 該view物件應設定過tag
//     *
//     * @param view
//     */
//    protected void keepViewData(View view) {
//        getData().keep(view);
//    }
//
//    /**
//     * 將view物件註冊為「需保存值資料」，同時載入前次的值 該view物件應設定過tag
//     *
//     * @param view
//     * @param autofill 是否載入前次的值
//     */
//    protected void keepViewData(View view, boolean autofill) {
//        getData().keep(view, autofill);
//    }
//
//    /**
//     * 將view物件註冊為「需保存值資料」，同時載入前次的值 該view物件應設定過tag
//     *
//     * @param viewID
//     */
//    protected void keepViewData(int viewID) {
//        getData().keep(this, viewID);
//    }
//
//    /**
//     * 將view物件註冊為「需保存值資料」，同時載入前次的值 該view物件應設定過tag
//     *
//     * @param viewID
//     * @param autofill 是否載入前次的值
//     */
//    protected void keepViewData(int viewID, boolean autofill) {
//        getData().keep(this, viewID, autofill);
//    }

    /**
     * @param menuID
     */
    protected void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (menuList != null) {
            for (int i = 0; i < menuList.size(); i++) {
                if (menuList.get(i).getActionName().contains(",")) {
                    String[] menuNames = menuList.get(i).getActionName().split(",");
                    menu.add(0, i, i, menuNames[0]);
                    menu.getItem(i).setIcon(Integer.valueOf(menuNames[1]));
                } else {
                    menu.add(0, i, i, menuList.get(i).getActionName());
                }
                //System.out.println("menuList.get(i).getActionName()=" + menuList.get(i).getActionName());
            }
        }
        this.menu = menu;
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() < menuList.size()) {
                menuList.get(item.getItemId()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Log message with info leverl
     *
     * @param msg
     */
    public void logi(String msg) {
        Log.i("grandroid", msg);
    }

    /**
     * Log message with error leverl
     *
     * @param msg
     */
    public void loge(String msg) {
        Log.e("grandroid", msg);
    }

    /**
     * 顯示Toast訊息
     *
     * @param msg
     */
    public void toast(String msg) {
        new ToastAction(this).setMessage(msg).execute();
    }

    /**
     * 顯示帶有一個OK按鈕的Dialog
     *
     * @param title
     * @param msg
     */
    public void alert(String title, String msg) {
        //System.out.println(msg);
        alert(title, msg, new Action().setActionName("OK"));
    }

    /**
     * 顯示帶有一個按鈕的Dialog，按鈕名稱為actPositive的name屬性
     *
     * @param title
     * @param msg
     * @param actPositive
     */
    public void alert(String title, String msg, final Action actPositive) {
        alert(title, msg, actPositive, null);
    }

    /**
     * 顯示帶有兩個按鈕的Dialog，按鈕名稱為actPositive、actNegative的name屬性
     *
     * @param title
     * @param msg
     * @param actPositive
     * @param actNegative
     */
    public void alert(String title, String msg, final Action actPositive, final Action actNegative) {
        new AlertAction(this).setData(title, msg, actPositive, actNegative).execute();
    }

    /**
     * 顯示訊息及標題於Notification Bar
     *
     * @param title
     * @param msg
     */
    public void notify(String title, String msg) {
        new NotifyAction(this).setContent(title, msg).execute();
    }

    /**
     * 將按鈕設定為按下時觸發Action的execute方法 只支援Button與ImageButton
     *
     * @param btnID Resource ID
     * @param act
     * @return return view refered to btnID
     */
    protected View setButtonEvent(int btnID, final Action act) {
        final View btn = findViewById(btnID);
        setButtonEvent(btn, act);
        return btn;
    }

    /**
     * 將按鈕設定為按下時觸發Action的execute方法 只支援Button與ImageButton
     *
     * @param <T>
     * @param btn
     * @param act
     * @return return param btn
     */
    protected <T extends View> T setButtonEvent(T btn, final Action act) {

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                act.setSrc(view);
                act.execute();
            }
        });
        return btn;
    }

    /**
     * 新增一個menu選項，menu的名字是act的name屬性 (按下手機menu鍵時跳出來的選單，即ContextMenu)
     * 只適合製作靜態的menu
     *
     * @param act
     */
    protected void addMenu(final Action act) {
        if (menuList == null) {
            menuList = new ArrayList<Action>();
        }
        menuList.add(act);
        //menuActions.put(menuItemID, act);
    }

    /**
     * @param act
     * @param icon
     */
    protected void addMenu(final Action act, int icon) {
        if (menuList == null) {
            menuList = new ArrayList<Action>();
        }
        act.setActionName(act.getActionName() + "," + icon);
        menuList.add(act);
        //menuActions.put(menuItemID, act);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
        }
        if (observerMap == null) {
            observerMap = new ConcurrentHashMap<String, ObserverTarget>();
        }
        registBackStackChangedListener();
//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//
//            public void onBackStackChanged() {
//                Fragment f = getLastFragment();
//                if (f != null && f instanceof Component) {
//                    Log.e("grandroid", "lastFragment.getForgotten()=" + ((Component) f).getForgottenState());
//                    if (((Component) f).getForgottenState() == 2) {
//                        getSupportFragmentManager().popBackStack();
//                    }
//                }
//            }
//        });
    }

    public Component getLastComponent() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null && fm.getFragments().get(i) instanceof Component) {
                    return (Component) fm.getFragments().get(i);
                }
            }
        }
        return null;
    }

    public Fragment getLastFragment() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null) {
                    return fm.getFragments().get(i);
                }
            }
        }
        return null;
    }

    protected int findLastFragmentIndex() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null) {
                    return i;
                }
            }
        }
        return -1;
    }

    protected int findLastComponentIndex() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null && fm.getFragments().get(i) instanceof Component) {
                    return i;
                }
            }
        }
        return -1;
    }


    /**
     * 新增一個Fragment並且顯示在最上層
     */
    public void prepareTurnToFragment() {
        isAddFragment = true;
        needCheckBackStatck = false;
        mIsCanEixt = false;
    }

    /**
     * @param mode 1: 下一頁不加進Back歷程,2: ClearTop
     */
    public void addComponentToBackStack(int mode) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        if (mode == 1) {
            backStackSet.add(fm.getBackStackEntryCount() + 1);
        }
    }

    public void resetBackStackSet() {
        backStackSet.clear();
    }

    public void registBackStackChangedListener() {
        if (!isRegistedListener) {
            isRegistedListener = true;
            if (listener == null) {
                listener = getOnBackStackChangedListener();
            }
            getSupportFragmentManager().addOnBackStackChangedListener(listener);
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        //Log.d("grandroid", "OnBackPress BackStackEntryCount:" + count);
        if (count == 0) {
            int index = findLastComponentIndex();
            if (index > -1) {
                List<Fragment> frags = getSupportFragmentManager().getFragments();
                if (frags.get(index) instanceof Component) {
                    Component lastCom = (Component) frags.get(index);
                    //Log.d("grandroid", "Component " + lastCom.getClass().getSimpleName() + " can back?");
                    if (!lastCom.onBackPressed()) {
                        return;
                    }
                }
            }
            if (notifyLeaving && System.currentTimeMillis() - leavingTime > 3000) {
                leavingTime = System.currentTimeMillis();
                Toast.makeText(this, getPressBackAgainMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            super.onBackPressed();
            //additional code
        } else {
            boolean isNeedRemoveCount = backStackSet.contains(count);
            if (isNeedRemoveCount) {
                backStackSet.remove(count);
            }
            if (count == 1) {
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }

    }


    protected String getPressBackAgainMessage() {
        return "再按一次返回鍵離開";
    }

    /**
     *
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        AppStatus.ON_TOP = true;
        if (bundledReceiver != null) {
            bundledReceiver.registerAllEvent(this);
        }
//            if (disableLock) {
//                log("redisable Lock");
//                KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
//                km.newKeyguardLock("Grandroid").disableKeyguard();
//            }
    }

    /**
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AppStatus.ON_TOP = true;
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        AppStatus.ON_TOP = false;
//        if (dataAgent != null) {
//            dataAgent.digest();
//        }
        if (bundledReceiver != null) {
            this.unregisterReceiver(bundledReceiver);
        }
//        if (receiver != null) {
//            if (disableLock) {
//                log("reenable Lock");
//                KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
//                km.newKeyguardLock("Grandroid").reenableKeyguard();
//            }
//        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (this.savedInstanceState == null && savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState;
            if (this.pendingActions != null) {
                for (PendingAction pa : this.pendingActions.values()) {
                    pa.restoreState(savedInstanceState);
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (this.pendingActions != null) {
            for (PendingAction pa : this.pendingActions.values()) {
                pa.saveState(outState);
            }
        }
        super.onSaveInstanceState(outState);
        if (outState != null && !outState.isEmpty()) {
            this.savedInstanceState = outState;
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pendingActions != null && pendingActions.containsKey(requestCode)) {
            pendingActions.get(requestCode).handleActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 向Android註冊欲處理的事件 使用本機制需注意，只在頁面為目前使用者觀看的頁面時有效
     * 當本頁面發生onPouse事件後，即不再有效，直到onResume發生，又會重新有效
     *
     * @param event
     * @param action
     */
    public void registerBundledAction(String event, ContextAction action) {
        if (bundledReceiver == null) {
            bundledReceiver = new MessageReceiver();
        }
        bundledReceiver.addEvent(event, action);
        this.registerReceiver(bundledReceiver, new IntentFilter(event));
    }

    /**
     * 移除所有向Android註冊的事件
     */
    public void unregisterAllBundledAction() {
        if (bundledReceiver != null) {
            this.unregisterReceiver(bundledReceiver);
            bundledReceiver = null;
        }
    }

    /**
     * @param id
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d("grandroid", "dialog id=" + id);
        return model.createDialog(this);
    }

    /**
     * @param model
     */
    protected void pickDateTime(DateTimePickModel model) {
        this.model = model;
        showDialog(model.getMode());
    }

    /**
     * @param <T>
     * @param cpm
     */
    public <T> void pickObject(CommandPickModel cpm) {
        new AlertDialog.Builder(this).setItems(cpm.getStringArray(), cpm).setTitle(cpm.getTitle()).show();
    }

    public void registerPendingAction(PendingAction pa) {
        if (pendingActions == null) {
            pendingActions = new ConcurrentHashMap<Integer, PendingAction>();
        }
        if (savedInstanceState != null) {
            pa.restoreState(savedInstanceState);
        }
        pendingActions.put(pa.getRequestCode(), pa);
    }

    public void registerDataEvent(String key, Observer obs) {
        if (observerMap.containsKey(key)) {
            observerMap.get(key).addObserver(obs);
        } else {
            ObserverTarget ot = new ObserverTarget(key);
            ot.addObserver(obs);
            observerMap.put(key, ot);
        }
    }

    public void unregisterDataEvent(String key, Observer obs) {
        if (observerMap.containsKey(key)) {
            observerMap.get(key).deleteObserver(obs);
        }
    }

    public void notifyEvent(DataEvent event) {

        if (observerMap.containsKey(event.getKey())) {
            observerMap.get(event.getKey()).dirty();
            observerMap.get(event.getKey()).notifyObservers(event);
        }
    }

    public boolean isNotifyLeaving() {
        return notifyLeaving;
    }

    public void setNotifyLeaving(boolean notifyLeaving) {
        this.notifyLeaving = notifyLeaving;
    }

    protected Fragment createPassedComponent() {
        return createComponent(FRAGMENT_CLASS);
    }

    protected Fragment createComponent(String className) {
        try {
            if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey(className)) {
                return (Fragment) Component.createInstance(Class.forName(this.getIntent().getExtras().getString(className)));
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return new Component();
    }

    private FragmentManager.OnBackStackChangedListener getOnBackStackChangedListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    //Log.d("grandroid", "OnBackStackChangedListener_count:" + backStackEntryCount);

                    if (backStackEntryCount == 0) {
                        if (!isAddFragment) {
                            finish();
                        }
                    } else {
                        boolean needForgetCurrentFace = backStackSet.contains(getSupportFragmentManager().getBackStackEntryCount());
                        if (!needForgetCurrentFace || !needCheckBackStatck) {
                            needCheckBackStatck = true;
                            if (!isAddFragment) {

                                Fragment fragment = manager.getFragments()
                                        .get(backStackEntryCount - 1);
                                if (fragment != null) {
                                    fragment.onResume();
                                }
                                //Log.d("grandroid", "OnBackStackChangedListener_" + fragment.getClass().getSimpleName() + " onResume.");
                            } else {
                                if (lastBackStackCount > backStackEntryCount) {
                                    Fragment fragment = manager.getFragments()
                                            .get(backStackEntryCount - 1);
                                    if (fragment != null) {
                                        fragment.onResume();
                                    }
                                }
                            }
                            isAddFragment = false;
                        } else {
                            FragmentTransaction fragmentTransaction = manager.beginTransaction();
                            fragmentTransaction.remove(manager.getFragments().get(getSupportFragmentManager().getBackStackEntryCount()));
                            fragmentTransaction.commit();
                            getSupportFragmentManager().popBackStack();
                            backStackSet.remove(getSupportFragmentManager().getBackStackEntryCount());
                        }
                    }
                    lastBackStackCount = backStackEntryCount;
                }
            }
        };
        return result;
    }
}
