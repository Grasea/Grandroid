/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import grandroid.action.Action;
import grandroid.data.DataAgent;
import grandroid.dialog.CommandPickModel;
import grandroid.view.Face;
import grandroid.view.LayoutMaker;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Rovers
 */
public class Component extends Fragment implements Observer {

    protected DataEventHandler eh;
    protected CopyOnWriteArrayList<String> watchKeys;
    protected ConcurrentHashMap<String, Object> valueMap;
    protected DataAgent da;
    protected View rootView;
    protected int forgottenState;
    protected Face face;

    public Component() {
        super();
        valueMap = new ConcurrentHashMap<String, Object>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 不該override此函數
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        container.removeAllViews();
        if (rootView == null) {
            LayoutMaker maker = createLayoutMaker();
            onCreateView(maker, savedInstanceState);
            rootView = maker.getRootLayout();
        } else {
            if (eh != null && watchKeys != null) {
                for (String key : watchKeys) {
                    eh.registerDataEvent(key, this);
                }
            }
        }
        return rootView;
    }

    /**
     * 請Override此函數以產生View
     *
     * @param maker
     * @param savedInstanceState
     */
    public void onCreateView(LayoutMaker maker, Bundle savedInstanceState) {

    }

    public LayoutMaker createLayoutMaker() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LayoutMaker maker = new LayoutMaker(getActivity(), layout, false);
        return maker;
    }

    public Object addKeyValue(String key, Object data) {
        valueMap.put(key, data);
        return data;
    }

    public void watchEvent(String key) {
        Log.d("grandroid", "watch event key=" + key);
        if (watchKeys == null) {
            watchKeys = new CopyOnWriteArrayList<String>();
        }
        if (!watchKeys.contains(key)) {
            watchKeys.add(key);

            if (eh != null) {
                Log.d("grandroid", "register event key=" + key + " to face");
                eh.registerDataEvent(key, this);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof Face) {
            this.face = (Face) activity;
        }
        if (DataEventHandler.class.isInstance(activity)) {
            eh = (DataEventHandler) activity;
        }
    }

    public Face getFace() {
        return face;
    }

    @Override
    public void onDestroyView() {
        if (eh != null && watchKeys != null) {
            //Log.d("grandroid", this.getClass().getSimpleName() + " onDestroyView, unregister all DataEvent");
            for (String key : watchKeys) {
                eh.unregisterDataEvent(key, this);
            }
            //watchKeys.clear();
        }
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        super.onDestroyView(); //To change body of generated methods, choose Tools | Templates.
    }

    public final void update(Observable observable, Object data) {
        DataEvent de = (DataEvent) data;
        if (de.getData() != null) {
            valueMap.put(de.getKey(), de.getData());
        }
        this.onDataEvent(de, de.getSource() == this);
    }

    public void fireDataEvent(String key) {
        this.fireDataEvent(key, null);
    }

    public void fireDataEvent(String key, Object data) {
        DataEvent de = new DataEvent(key);
        de.setData(data);
        de.setSource(this);
        if (de.getData() != null) {
            valueMap.put(de.getKey(), de.getData());
        }
        eh.notifyEvent(de);
    }

    public void onDataEvent(DataEvent event, boolean isFromSelf) {
        Log.d("grandroid", "onDataEvent of " + this.getClass().getSimpleName() + ", key=" + event.getKey() + ", data=" + event.getData());
    }

    public static <T> T createInstance(final Class<T> clazz) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, java.lang.InstantiationException, IllegalAccessException, InvocationTargetException {

        T instanceToReturn = null;
        Class< ?> enclosingClass = clazz.getEnclosingClass();

        if (enclosingClass != null) {
            Object instanceOfEnclosingClass = createInstance(enclosingClass);

            Constructor<T> ctor = clazz.getConstructor(enclosingClass);

            if (ctor != null) {
                instanceToReturn = ctor.newInstance(instanceOfEnclosingClass);
            }
        } else {
            instanceToReturn = clazz.newInstance();
        }
        return instanceToReturn;
    }

    protected DataAgent getData() {
        if (da == null) {
            da = new DataAgent(getFace());
        }
        return da;
    }

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
     *
     * @param <T>
     * @param cpm
     */
    public <T> void pickObject(CommandPickModel cpm) {
        new AlertDialog.Builder(getActivity()).setItems(cpm.getStringArray(), cpm).setTitle(cpm.getTitle()).show();
    }

    public int getForgottenState() {
        return forgottenState;
    }

    public boolean canResume() {
        return true;
    }

    public void setForgottenState(int forgottenState) {
        this.forgottenState = forgottenState;
    }

    public boolean onBackPressed() {
        return true;
    }
}
