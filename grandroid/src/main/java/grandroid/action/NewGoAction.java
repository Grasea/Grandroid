/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.action;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import grandroid.activity.ComponentActivity;
import grandroid.app.AppStatus;
import grandroid.view.NewFace;
import grandroid.view.R;
import grandroid.view.fragment.Component;

import java.util.List;

/**
 * @author Rovers
 */
public class NewGoAction extends ContextAction {

    /**
     *
     */
    protected Bundle bundle;
    /**
     *
     */
    protected int flag = 0;
    /**
     *
     */
    protected boolean isSubTask;
    /**
     *
     */
    protected Class c;
    /**
     *
     */
    protected int requestCode = 0;
    protected boolean noAnimation;
    protected int container;
    protected boolean forgetCurrent;
    protected Uri uri;
    protected String intentAction;
    protected Class anchorClass = null;
    protected boolean beforeAnchor;
    protected int leaveTransition;
    protected int enterTransition;
    protected int popEnterTransition;
    protected int popLeaveTransition;

    public enum Direction {

        Left, Left_Only, Right, Up, Up_Only, Down
    }

    /**
     * @param context
     * @param c       target activity
     */
    public NewGoAction(Context context, Class c) {
        super(context, "undefined");
        this.c = c;
    }

    /**
     * @param context
     * @param actionName
     * @param cp
     */
    public NewGoAction(Context context, String actionName, String cp) {
        super(context, actionName);
        try {
            c = Class.forName(cp);
        } catch (ClassNotFoundException ex) {
            Log.e(NewGoAction.class.getName(), null, ex);
        }
    }

    /**
     * @param context
     * @param actionName
     * @param c
     */
    public NewGoAction(Context context, String actionName, Class c) {
        super(context, actionName);
        this.c = c;
    }

    public NewGoAction(Activity activity, Class c, int container) {
        super(activity, "undefined");
        this.c = c;
        this.container = container;
    }

    public NewGoAction(Context context, Class c, int container) {
        super(context, "undefined");
        this.c = c;
        this.container = container;
    }

    public NewGoAction(Activity activity, String actionName, Class c, int container) {
        super(activity, actionName);
        this.c = c;
        this.container = container;
    }

    /**
     * @param bundle
     * @return
     */
    public NewGoAction setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public NewGoAction addBundleObject(String key, String value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, value);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public NewGoAction addBundleObject(String key, int value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, value);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public NewGoAction addBundleObject(String key, boolean value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(key, value);
        return this;
    }

    /**
     * @param key
     * @param strarr
     * @return
     */
    public NewGoAction addBundleObject(String key, String[] strarr) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putStringArray(key, strarr);
        return this;
    }

    /**
     * @param key
     * @param intarr
     * @return
     */
    public NewGoAction addBundleObject(String key, int[] intarr) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putIntArray(key, intarr);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public NewGoAction addBundleObject(String key, double value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putDouble(key, value);
        return this;
    }

    public NewGoAction setTransition(int enterTransition, int leaveTransition) {
        this.leaveTransition = leaveTransition;
        this.enterTransition = enterTransition;
        return this;
    }

    public NewGoAction goDirection(Direction dir) {
        switch (dir) {
            case Left:
                leaveTransition = R.anim.slide_in_left;
                enterTransition = R.anim.slide_out_right;
                popEnterTransition = R.anim.slide_in_right;
                popLeaveTransition = R.anim.slide_out_left;
                break;
            case Left_Only:
                leaveTransition = R.anim.no_animation;
                enterTransition = R.anim.slide_in_right;
                break;
            case Right:
                leaveTransition = R.anim.slide_out_left;
                enterTransition = R.anim.slide_in_right;
                popEnterTransition = R.anim.slide_in_left;
                popLeaveTransition = R.anim.slide_out_right;
                break;
            case Up:
                leaveTransition = R.anim.slide_out_up;
                enterTransition = R.anim.slide_in_bottom;
                popEnterTransition = R.anim.slide_in_up;
                popLeaveTransition = R.anim.slide_out_bottom;
                break;
            case Up_Only:
                leaveTransition = R.anim.no_animation;
                enterTransition = R.anim.slide_in_bottom;
                break;
            case Down:
                leaveTransition = R.anim.slide_out_up;
                enterTransition = R.anim.slide_in_bottom;
                popEnterTransition = R.anim.slide_in_up;
                popLeaveTransition = R.anim.slide_out_bottom;
                break;
        }
        return this;
    }

    /**
     * @param flag
     * @return
     */
    public NewGoAction setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public NewGoAction addFlag(int flag) {
        this.flag = flag | flag;
        return this;
    }

    public NewGoAction setContainer(int container) {
        this.container = container;
        return this;
    }

    /**
     * 注意，無法插入新Copmonent於Back Stack中最早的Component之前
     *
     * @param anchorClass
     * @return
     */
    public NewGoAction before(Class anchorClass) {
        beforeAnchor = true;
        this.anchorClass = anchorClass;
        return this;
    }

    public NewGoAction after(Class anchorClass) {
        beforeAnchor = false;
        this.anchorClass = anchorClass;
        return this;
    }

    /**
     * @return
     */
    public NewGoAction forgetCurrentNewFace() {
        forgetCurrent = true;
        return this;
    }

    public NewGoAction cancelAnimation() {
        noAnimation = true;
        return this;
    }

    /**
     * @return
     */
    public NewGoAction removeOldNewFace() {
        return setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * @return
     */
    public NewGoAction setSubTask() {
        return setSubTask(0);
    }

    /**
     * @param requestCode
     * @return
     */
    public NewGoAction setSubTask(int requestCode) {
        isSubTask = true;
        this.requestCode = requestCode;
        return this;
    }

    public NewGoAction setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public NewGoAction asActionView() {
        this.intentAction = Intent.ACTION_VIEW;
        return this;
    }

    public NewGoAction setIntentAction(String intentAction) {
        this.intentAction = intentAction;
        return this;
    }


    /**
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        if (context != null && c != null) {
            if (Fragment.class.isAssignableFrom(c)) {
                if (container > 0) {
                    NewFace face = ((NewFace) context);
                    face.prepareTurnToFragment();
                    android.support.v4.app.FragmentManager fm = face.getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentByTag(c.getSimpleName());
                    boolean isFragmentExist = fragment != null;
//                    if (fragment == null) {
                    Component lastComponent = face.getLastComponent();
                    if (isFragmentExist) {
                        Log.i("grandroid", "lastComponent:" + lastComponent.getClass().getSimpleName() + ", new Component:" + c.getSimpleName());
                    }
                    if ((flag & Intent.FLAG_ACTIVITY_CLEAR_TOP) == Intent.FLAG_ACTIVITY_CLEAR_TOP && isFragmentExist
                            && lastComponent.getClass().getSimpleName().equals(c.getSimpleName())) {
                    } else {
                        try {
//                        isFragmentExist = false;
                            fragment = (Fragment) Component.createInstance(c);
                            fragment.setArguments(new Bundle());
                        } catch (Exception e) {
                            Log.e("grandroid", null, e);
                        }
                    }

//                    }

//                    if (fragment.isAdded()) {
//
//                        return true;
//                    }
                    if (bundle != null && !bundle.isEmpty()) {
                        fragment.getArguments().putAll(bundle);
                    }
                    FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
//                R.anim.slide_in_right, R.anim.slide_out_left);
                    if ((flag & Intent.FLAG_ACTIVITY_CLEAR_TOP) == Intent.FLAG_ACTIVITY_CLEAR_TOP) {
                        if (lastComponent != null && lastComponent.getClass().getSimpleName().equals(c.getSimpleName())) {
                            lastComponent.onResume();
                        } else {
                            if (!isFragmentExist && enterTransition != 0 && leaveTransition != 0 && popEnterTransition != 0 && popLeaveTransition != 0) {
                                ft.setCustomAnimations(enterTransition, leaveTransition, popEnterTransition, popLeaveTransition);
                            }
                            if (lastComponent != null) {
                                Log.e("grandroid", "Clear Top & Hide Component:" + lastComponent.getClass().getSimpleName());
                                ft.hide(lastComponent);
                            } else {
                                Log.e("grandroid", "Clear Top getLastFragment is null");
                            }
                            if (isFragmentExist) {
                                fm.popBackStack(c.getSimpleName(), 0);
                                face.resetBackStackSet();
                                Log.e("grandroid", "Clear Top & popBackStack:" + c.getSimpleName());
                            } else {
                                ft.add(container, fragment, c.getSimpleName());
                                ft.addToBackStack(c.getSimpleName());
                                Log.e("grandroid", "Clear Top & add new Component:" + c.getSimpleName());
                            }
                        }

                    } else {
                        if (enterTransition != 0 && leaveTransition != 0 && popEnterTransition != 0 && popLeaveTransition != 0) {
                            ft.setCustomAnimations(enterTransition, leaveTransition, popEnterTransition, popLeaveTransition);
                        }
                        if (lastComponent != null) {
                            Log.e("grandroid", "Hide Component:" + lastComponent.getClass().getSimpleName());
                            ft.hide(lastComponent);
                        }

                        if (isFragmentExist) {
                            ft.add(container, fragment, c.getSimpleName());
                            ft.addToBackStack(c.getSimpleName());
                            Log.e("grandroid", "addToBackStack & Component is Exist:" + c.getSimpleName());
                        } else {
                            ft.add(container, fragment, c.getSimpleName());
                            ft.addToBackStack(c.getSimpleName());
                            Log.e("grandroid", "addToBackStack & Component is NOT Exist:" + c.getSimpleName());
                        }
                    }
                    ft.commit();
                    if (forgetCurrent) {
                        face.addComponentToBackStack(1);
                    }
                } else {
                    Intent intent = new Intent();
                    String s = AppStatus.getMetaData(context, "BaseNewFaceClass");
                    if (s.isEmpty()) {
                        intent.setClass(context, ComponentActivity.class);
                    } else {
                        try {
                            intent.setClass(context, Class.forName(s));
                        } catch (ClassNotFoundException ex) {
                            Log.e("grandroid", null, ex);
                            intent.setClass(context, ComponentActivity.class);
                        }
                    }
                    if (bundle != null) {
                        bundle.putString(NewFace.FRAGMENT_CLASS, c.getName());
                        intent.putExtras(bundle);
                    } else {
                        bundle = new Bundle();
                        bundle.putString(NewFace.FRAGMENT_CLASS, c.getName());
                        intent.putExtras(bundle);
                    }
                    if (flag > 0) {
                        intent.setFlags(flag);
                    }
                    if (isSubTask && context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        context.startActivity(intent);
                    }
                    if (noAnimation) {
                        ((Activity) context).overridePendingTransition(0, 0);
                    } else if (enterTransition != 0 && leaveTransition != 0) {
                        ((Activity) context).overridePendingTransition(enterTransition, leaveTransition);
                    }
                }
            } else {
                Intent intent = new Intent();
                intent.setClass(context, c);
                if (uri != null) {
                    intent.setData(uri);
                }
                if (intentAction != null) {
                    intent.setAction(intentAction);
                }
                if (bundle != null) {
                    intent.putExtras(bundle);
                }
                if (forgetCurrent) {
                    flag = flag | Intent.FLAG_ACTIVITY_NO_HISTORY;
                }
                if (flag > 0) {
                    intent.setFlags(flag);
                }
                if (isSubTask && context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
                if (noAnimation) {
                    ((Activity) context).overridePendingTransition(0, 0);
                } else if (enterTransition != 0 && leaveTransition != 0) {
                    ((Activity) context).overridePendingTransition(enterTransition, leaveTransition);
                }
            }
            return true;
        }
        return false;
    }

    protected int findAnchorTag(FragmentManager fm) {
        if (fm.getFragments() != null) {
            List<Fragment> frags = fm.getFragments();
            for (int i = frags.size() - 1; i >= 0; i--) {
                if (frags.get(i) != null && frags.get(i).getClass().equals(anchorClass)) {
                    //if (beforeAnchor) {
                    //    return i - 1;
                    //} else {
                    return i;
                    //}
                }
            }
        }
        return -1;
    }

    protected boolean hasRecoverableFragment(FragmentManager fm) {
        if (fm.getFragments() != null) {
            for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                if (fm.getFragments().get(i) != null && fm.getFragments().get(i) instanceof Component) {
                    if (((Component) fm.getFragments().get(i)).getForgottenState() == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Fragment findLastFragment(FragmentManager fm) {
        if (context instanceof NewFace) {
            if (fm.getFragments() != null) {
                for (int i = fm.getFragments().size() - 1; i >= 0; i--) {
                    if (fm.getFragments().get(i) != null) {
                        return fm.getFragments().get(i);
                    }
                }
            }
        }
        return null;
    }

}
