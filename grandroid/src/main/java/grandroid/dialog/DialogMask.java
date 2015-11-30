/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import grandroid.action.Action;
import grandroid.dialog.GDialog.DialogStyle;
import grandroid.view.LayoutMaker;
import grandroid.phone.DisplayAgent;
import java.lang.reflect.Field;

/**
 *
 * @author Rovers
 */
public abstract class DialogMask {

    /**
     *
     */
    protected Dialog dialog;
    /**
     *
     */
    protected Context context;
    /**
     *
     */
    protected GDialog.Builder builder;
    protected boolean cancelable;

    /**
     *
     * @param context
     */
    public DialogMask(Context context) {
        this.context = context;
        builder = new GDialog.Builder(context);
    }

    /**
     *
     * @param context
     * @param builder
     * @param maker
     * @return
     * @throws Exception
     */
    public abstract boolean setupMask(Context context, GDialog.Builder builder, LayoutMaker maker) throws Exception;

    /**
     *
     * @param <T>
     * @param view
     * @param action
     * @return
     */
    public <T extends View> T setButtonEvent(T view, final Action action) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                action.setSrc(view);
                action.execute();
            }
        });
        return view;
    }

    /**
     *
     * @param dialogInterface
     */
    public void onDismiss(DialogInterface dialogInterface) {
    }

    /**
     *
     * @param dialogInterface
     */
    public void onCancel(DialogInterface dialogInterface) {
    }

    /**
     *
     * @return
     */
    public Dialog getDialog() {
        return dialog;
    }

    /**
     *
     * @return
     */
    public boolean isShowing() {
        return dialog.isShowing();
    }

    public DialogMask cancelable(boolean bool) {
        this.cancelable = bool;
        return this;
    }

    public DialogMask cancelable() {
        return cancelable(true);
    }

    /**
     *
     */
    public void show() {
        show(DialogStyle.Grandroid);
    }

    /**
     *
     * @param style
     */
    public void show(DialogStyle style) {
        try {
            if (style == DialogStyle.Android) {
                AlertDialog.Builder b = new AlertDialog.Builder(context);
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                LayoutMaker maker = new LayoutMaker(context, ll);
                setupMask(context, builder, maker);
                b.setTitle(builder.getTitle());
                b.setView(maker.getMainLayout());
                b.setCancelable(cancelable || builder.isCancelable());
                if (builder.getPositiveButtonAction() != null) {
                    b.setPositiveButton(builder.getPositiveButtonAction().getActionName(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dia, int arg1) {
                            setDialogVisibility(dia, builder.getPositiveButtonAction().execute(), false);
                        }
                    });
                }
                if (builder.getMiddleButtonAction() != null) {
                    b.setNeutralButton(builder.getMiddleButtonAction().getActionName(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dia, int arg1) {
                            setDialogVisibility(dia, builder.getMiddleButtonAction().execute(), false);
                        }
                    });
                }
                if (builder.getNegativeButtonAction() != null) {
                    b.setNegativeButton(builder.getNegativeButtonAction().getActionName(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dia, int arg1) {
                            setDialogVisibility(dia, builder.getNegativeButtonAction().execute(), true);
                        }
                    });
                }
                dialog = b.create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        DialogMask.this.onDismiss(dialogInterface);
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialogInterface) {
                        DialogMask.this.onCancel(dialogInterface);
                    }
                });
                dialog.show();
            } else {
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                LayoutMaker maker = new LayoutMaker(context, ll);

                builder.beforeDialogContent(maker, style);

                setupMask(context, builder, maker);
                builder.setCancelable(cancelable);
                dialog = builder.create(maker, style);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        DialogMask.this.onDismiss(dialogInterface);
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialogInterface) {
                        DialogMask.this.onCancel(dialogInterface);
                    }
                });
                dialog.show();
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
    }

    protected void setDialogVisibility(DialogInterface dia, boolean dismiss, boolean isCancel) {
        if (dismiss) {
            try {
                Field field = dia.getClass().getSuperclass().getDeclaredField(
                        "mShowing");
                field.setAccessible(true);
                //   将mShowing变量设为false，表示对话框已关闭 
                field.set(dia, true);
            } catch (Exception e) {
            }
            if (!isCancel) {
                dia.dismiss();
            } else {
                dia.cancel();
            }
        } else {
            try {
                Field field = dia.getClass().getSuperclass().getDeclaredField(
                        "mShowing");
                field.setAccessible(true);
                //   将mShowing变量设为false，表示对话框已关闭 
                field.set(dia, false);
            } catch (Exception e) {
            }
        }
    }

//    public void showAsDialog() {
//        try {
//        } catch (Exception ex) {
//            Log.e(DialogMask.class.getName(), null, ex);
//        }
//    }
    /**
     *
     * @return
     */
    protected LinearLayout.LayoutParams getMaxSizeLayoutParams() {
        return getMaxSizeLayoutParams(0);
    }

    /**
     *
     * @param minus
     * @return
     */
    protected LinearLayout.LayoutParams getMaxSizeLayoutParams(int minus) {
        DisplayAgent lu = new DisplayAgent((Activity) context);
        return new LinearLayout.LayoutParams(lu.getWidth() - 60, lu.getHeight() - 100 - minus);
    }
}
