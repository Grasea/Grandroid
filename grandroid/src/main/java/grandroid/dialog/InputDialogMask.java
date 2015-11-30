/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.dialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import grandroid.action.Action;
import grandroid.dialog.GDialog.Builder;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public abstract class InputDialogMask extends DialogMask {

    /**
     *
     */
    protected String defaultText;
    /**
     *
     */
    protected String hintText;
    /**
     *
     */
    protected String captionText;
    /**
     *
     */
    protected String titleText;

    /**
     *
     */
    protected int inputType;

    /**
     * @param context
     * @param titleText
     * @param defaultText
     * @param hintText
     * @param captionText
     * @param inputType
     */
    public InputDialogMask(Context context, String titleText, String defaultText, String hintText, String captionText, int inputType) {
        super(context);
        this.titleText = titleText;
        this.hintText = hintText;
        this.captionText = captionText;
        this.defaultText = defaultText == null ? "" : defaultText;
        this.inputType = inputType;
    }

    /**
     * @param context
     * @param titleText
     * @param defaultText
     * @param hintText
     * @param captionText
     */
    public InputDialogMask(Context context, String titleText, String defaultText, String hintText, String captionText) {
        this(context, titleText, defaultText, hintText, captionText, InputType.TYPE_CLASS_TEXT);
    }

    /**
     *
     * @param inputText
     * @return
     */
    public abstract boolean executeAction(String inputText);

    /**
     *
     * @param context
     * @param builder
     * @param maker
     * @return
     * @throws Exception
     */
    @Override
    public boolean setupMask(Context context, Builder builder, LayoutMaker maker) throws Exception {
        builder.setTitle(titleText);
        if (captionText != null && captionText.length() > 0) {
            maker.addTextView(captionText);
        }
        final EditText et = maker.addEditText(defaultText);
        if (inputType != et.getInputType()) {
            et.setInputType(inputType);
        }
        if (hintText != null && hintText.length() > 0) {
            et.setHint(hintText);
        }
        builder.setPositiveButton(new Action(context.getString(android.R.string.ok)) {
            @Override
            public boolean execute() {
                if (dialog != null) {
                    if (executeAction(et.getText().toString())) {
                        dialog.dismiss();
                    }
                } else {
                    return executeAction(et.getText().toString());
                }
                return true;
            }
        });
        builder.setNegativeButton(new Action(context.getString(android.R.string.cancel)) {
            @Override
            public boolean execute() {
                if (dialog != null) {
                    dialog.cancel();
                }
                return true;
            }
        });
        return true;
    }
}
