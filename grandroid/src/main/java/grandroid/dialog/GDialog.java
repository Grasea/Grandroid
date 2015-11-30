//基本使用方式
//                DSDialog.Builder customBuilder = new DSDialog.Builder(FrameShowWord.this);
//                customBuilder.setTitle("Custom title").setMessage("Custom body").setNegativeButton(new Action("Cancel") {
//
//                    @Override
//                    public boolean execute() {
//                        ((Dialog) this.args[0]).dismiss();
//                        return true;
//                    }
//                }).setPositiveButton(new Action("Confirm") {
//
//                    @Override
//                    public boolean execute() {
//                        ((Dialog) this.args[0]).dismiss();
//                        return true;
//                    }
//                });
//                Dialog dialog = customBuilder.create();
//                dialog.show();
package grandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import grandroid.action.Action;
import grandroid.view.LayoutMaker;

/**
 *
 * Create custom Dialog windows for your application Custom dialogs rely on
 * custom layouts wich allow you to create and use your own look & feel.
 *
 * Under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 *
 * @author antoine vianey
 *
 */
public class GDialog extends Dialog {

    /**
     *
     */
    public enum DialogStyle {

        /**
         *
         */
        Android,
        /**
         *
         */
        Grandroid,
        /**
         *
         */
        Custom;
    }

    /**
     *
     * @param context
     * @param theme
     */
    public GDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     *
     * @param context
     */
    public GDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        //private View contentView;
        private Action positiveButtonAction,
                negativeButtonAction, middleButtonAction;
        private TextView tvTitle;
        private boolean cancelable;
        /**
         *
         */
        protected GDialog dialog;

        /**
         *
         * @param context
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         *
         * @return
         */
        public String getTitle() {
            return title;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param action
         * @return
         */
        public Builder setPositiveButton(Action action) {
            this.positiveButtonAction = action;
            return this;
        }

        /**
         * Set the middle button resource and it's listener
         *
         * @param action
         */
        public void setMiddleButton(Action action) {
            this.middleButtonAction = action;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param action
         * @return
         */
        public Builder setNegativeButton(Action action) {
            this.negativeButtonAction = action;
            return this;
        }

        /**
         *
         * @return
         */
        public Action getNegativeButtonAction() {
            return negativeButtonAction;
        }

        /**
         *
         * @return
         */
        public Action getPositiveButtonAction() {
            return positiveButtonAction;
        }

        public Action getMiddleButtonAction() {
            return middleButtonAction;
        }

        /**
         *
         * @param maker
         * @param style
         */
        public void beforeDialogContent(LayoutMaker maker, DialogStyle style) {
            switch (style) {
                case Grandroid:
                    maker.getMainLayout().setMinimumWidth(280);
                    maker.getMainLayout().setBackgroundColor(Color.TRANSPARENT);
                    LinearLayout layoutTitle = maker.addColLayout();
                    layoutTitle.setBackgroundColor(Color.TRANSPARENT);
                    layoutTitle.setBackgroundResource(maker.getResourceID("drawable/dialog_header"));
                    tvTitle = (TextView) maker.add(maker.createTextView("Dialog"), maker.layWW(0));
                    tvTitle.setPadding(8, 0, 8, 0);
                    tvTitle.setBackgroundResource(maker.getResourceID("drawable/dialog_title"));
                    tvTitle.setTextSize(18);
                    tvTitle.setTextColor(Color.BLACK);
                    tvTitle.setTypeface(null, Typeface.BOLD);
                    maker.escape();

                    LinearLayout layoutContent = maker.addColLayout();
                    layoutContent.setBackgroundResource(maker.getResourceID("drawable/dialog_center"));
                    break;
            }
        }

        public boolean isCancelable() {
            return cancelable;
        }

        public void setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
        }

        /**
         * Create the custom dialog
         *
         * @param maker
         * @param style
         * @return
         */
        public GDialog create(LayoutMaker maker, DialogStyle style) {
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            if (style != DialogStyle.Android) {
                String uri = "style/GrandroidDialog";
                int resStyle = context.getResources().getIdentifier(uri, null, context.getPackageName());
                dialog = new GDialog(context, resStyle);
            } else {
                dialog = new GDialog(context);
                dialog.setTitle(title);
            }
            dialog.setCancelable(cancelable);
            if (style != DialogStyle.Custom) {
                maker.escape();

                LinearLayout layoutBottom = maker.addRowLayout();
                if (style == DialogStyle.Grandroid) {
                    layoutBottom.setBackgroundColor(Color.TRANSPARENT);
                    layoutBottom.setBackgroundResource(maker.getResourceID("drawable/dialog_footer"));
                }
                if (positiveButtonAction != null) {
                    Button btn = maker.add(maker.createButton(positiveButtonAction.getActionName()), maker.layFW(1));

                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonAction.setArgs(dialog);
                            positiveButtonAction.setSrc(v);
                            positiveButtonAction.execute();
                        }
                    });
                }
                if (middleButtonAction != null) {
                    Button btn = maker.add(maker.createButton(middleButtonAction.getActionName()), maker.layFW(1));

                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            middleButtonAction.setArgs(dialog);
                            middleButtonAction.setSrc(v);
                            middleButtonAction.execute();
                        }
                    });
                }
                // set the cancel button
                if (negativeButtonAction != null) {
                    Button btn = maker.add(maker.createButton(negativeButtonAction.getActionName()), maker.layFW(1));
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonAction.setArgs(dialog);
                            negativeButtonAction.setSrc(v);
                            negativeButtonAction.execute();
                        }
                    });
                }
                if (style == DialogStyle.Grandroid) {
                    if (title != null) {
                        tvTitle.setText(title);
                    }
                }
            }
            dialog.setContentView(maker.getMainLayout(), new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }
    }
}
