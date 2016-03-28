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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import grandroid.activity.ComponentActivity;
import grandroid.app.AppStatus;
import grandroid.view.Face;
import grandroid.view.R;
import grandroid.view.fragment.Component;

import java.util.List;

/**
 * @author Rovers
 */
public class GoAction extends ContextAction {

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
    public GoAction(Context context, Class c) {
        super(context, "undefined");
        this.c = c;
    }

    /**
     * @param context
     * @param actionName
     * @param cp
     */
    public GoAction(Context context, String actionName, String cp) {
        super(context, actionName);
        try {
            c = Class.forName(cp);
        } catch (ClassNotFoundException ex) {
            Log.e(GoAction.class.getName(), null, ex);
        }
    }

    /**
     * @param context
     * @param actionName
     * @param c
     */
    public GoAction(Context context, String actionName, Class c) {
        super(context, actionName);
        this.c = c;
    }

    public GoAction(Activity activity, Class c, int container) {
        super(activity, "undefined");
        this.c = c;
        this.container = container;
    }

    public GoAction(Context context, Class c, int container) {
        super(context, "undefined");
        this.c = c;
        this.container = container;
    }

    public GoAction(Activity activity, String actionName, Class c, int container) {
        super(activity, actionName);
        this.c = c;
        this.container = container;
    }

    /**
     * @param bundle
     * @return
     */
    public GoAction setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public GoAction addBundleObject(String key, String value) {
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
    public GoAction addBundleObject(String key, int value) {
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
    public GoAction addBundleObject(String key, boolean value) {
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
    public GoAction addBundleObject(String key, String[] strarr) {
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
    public GoAction addBundleObject(String key, int[] intarr) {
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
    public GoAction addBundleObject(String key, double value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putDouble(key, value);
        return this;
    }

    public GoAction setTransition(int enterTransition, int leaveTransition) {
        this.leaveTransition = leaveTransition;
        this.enterTransition = enterTransition;
        return this;
    }

    public GoAction goDirection(Direction dir) {
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
    public GoAction setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public GoAction addFlag(int flag) {
        this.flag = flag | flag;
        return this;
    }

    public GoAction setContainer(int container) {
        this.container = container;
        return this;
    }

    /**
     * 注意，無法插入新Copmonent於Back Stack中最早的Component之前
     *
     * @param anchorClass
     * @return
     */
    public GoAction before(Class anchorClass) {
        beforeAnchor = true;
        this.anchorClass = anchorClass;
        return this;
    }

    public GoAction after(Class anchorClass) {
        beforeAnchor = false;
        this.anchorClass = anchorClass;
        return this;
    }

    /**
     * @return
     */
    public GoAction forgetCurrentFace() {
        forgetCurrent = true;
        return this;
    }

    public GoAction cancelAnimation() {
        noAnimation = true;
        return this;
    }

    /**
     * @return
     */
    public GoAction removeOldFace() {
        return setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * @return
     */
    public GoAction setSubTask() {
        return setSubTask(0);
    }

    /**
     * @param requestCode
     * @return
     */
    public GoAction setSubTask(int requestCode) {
        isSubTask = true;
        this.requestCode = requestCode;
        return this;
    }

    public GoAction setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public GoAction asActionView() {
        this.intentAction = Intent.ACTION_VIEW;
        return this;
    }

    public GoAction setIntentAction(String intentAction) {
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
                    final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    try {
                        if ((flag & Intent.FLAG_ACTIVITY_CLEAR_TOP) == Intent.FLAG_ACTIVITY_CLEAR_TOP) {
                            fm.popBackStack(c.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                        if (anchorClass != null) {
                            int anchorIndex = findAnchorTag(fm);
                            if (anchorIndex >= 0) {
                                fm.popBackStack(anchorIndex, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                            if (beforeAnchor) {
                                fm.popBackStack();
                            }
                        }
                    } catch (Exception ex) {
                        Log.e("grandroid", null, ex);
                    }
                    Fragment leavingFragment = findLastFragment(fm);
                    if (leavingFragment instanceof Component) {
                        if (((Component) leavingFragment).getForgottenState() == 1) {
                            ((Component) leavingFragment).setForgottenState(2);
                        }
                    }
                    FragmentTransaction ft = fm.beginTransaction();
                    if (enterTransition != 0 && leaveTransition != 0 && popEnterTransition != 0 && popLeaveTransition != 0) {
                        ft.setCustomAnimations(enterTransition, leaveTransition, popEnterTransition, popLeaveTransition);
                    }
                    try {
                        Fragment f = (Fragment) Component.createInstance(c);
                        if (bundle != null) {
                            f.setArguments(bundle);
                        }
                        if (forgetCurrent) {
                            if (f instanceof Component) {
                                ((Component) f).setForgottenState(1);
                            }
                        }
                        if (((Face) context).getLastFragment() != null) {
                            ft.hide(((Face) context).getLastFragment());
                        }
                        ft.add(container, f, c.getSimpleName());
                        //if (!forgetCurrent) {
                        if (fm.getFragments() != null && hasRecoverableFragment(fm)) {//
                            ft.addToBackStack(c.getSimpleName());
                            Log.d("grandroid", "addToBackStack: " + c.getSimpleName());
                            //}else{
                            //    Log.d("grandroid", "not addToBackStack: " + c.getSimpleName());
                        }
                        //}
                        ft.commit();
                    } catch (Exception ex) {
                        Log.e("grandroid", null, ex);
                    }
                } else {
                    Intent intent = new Intent();
                    String s = AppStatus.getMetaData(context, "BaseFaceClass");
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
                        bundle.putString(Face.FRAGMENT_CLASS, c.getName());
                        intent.putExtras(bundle);
                    } else {
                        bundle = new Bundle();
                        bundle.putString(Face.FRAGMENT_CLASS, c.getName());
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
        if (context instanceof Face) {
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
