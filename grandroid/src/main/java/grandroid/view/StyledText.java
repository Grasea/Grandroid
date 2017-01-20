/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Rovers
 */
public class StyledText {

    protected TextView tv;
    protected int size;
    protected Unit unit;
    protected boolean b;
    protected int gravity;
    protected Integer co;
    protected Matrix m;
    protected Object t;
    protected boolean strike;
    protected CharSequence txt;
    protected int[] padding;
    protected int maxl;
    protected Typeface tf;
    protected float high;
    protected boolean nowrap;
    protected boolean underline;
    protected Integer bgc = null;
    protected Integer bg = null;
    protected String hint;
    protected String allowChars;
    protected Format format;

    public enum Unit {

        Px, Dip, Sp, Pt, Auto;
    }

    public enum Format {

        Default, None, Number, Phone, English, Password;
    }

    public StyledText() {
        unit = Unit.Sp;
        format = Format.Default;
        size = -1;
        txt = null;
        //padding = new int[]{0, 0, 0, 0};
        high = 1;
    }

    public StyledText color(int co) {
        this.co = co;
        return this;
    }

    public StyledText color(String coHex) {
        this.co = Color.parseColor(coHex);
        return this;
    }

    public StyledText bg(int backgroundResource) {
        this.bg = backgroundResource;
        return this;
    }

    public StyledText bgc(int backgoundColor) {
        this.bgc = backgoundColor;
        return this;
    }

    public StyledText hint(String hint) {
        this.hint = hint;
        return this;
    }

    public StyledText format(Format inputFormat) {
        this.format = inputFormat;
        return this;
    }

    public StyledText allowChars(String regularExpression) {
        allowChars = regularExpression;
        return this;
    }

    public StyledText lock(boolean lockEdit) {
        if (lockEdit) {
            this.format = Format.None;
        }
        return this;
    }

    public StyledText bold() {
        b = true;
        return this;
    }

    public StyledText strike() {
        strike = true;
        return this;
    }

    public StyledText size(int size) {
        this.size = size;
        return this;
    }

    public StyledText size(Unit unit, int size) {
        this.size = size;
        this.unit = unit;
        return this;
    }

    public StyledText center() {
        this.gravity = Gravity.CENTER;
        return this;
    }

    public StyledText right() {
        this.gravity = Gravity.RIGHT;
        return this;
    }

    public StyledText gravity(int g) {
        this.gravity = g;
        return this;
    }

    public StyledText tag(Object tagObj) {
        t = tagObj;
        return this;
    }

    public StyledText nowrap() {
        nowrap = true;
        return this;
    }

    public StyledText text(CharSequence txt) {
        this.txt = txt;
        return this;
    }

    public StyledText underline() {
        underline = true;
        return this;
    }

    public StyledText html(String strHTML) {
        this.txt = Html.fromHtml(strHTML);
        return this;
    }

    public StyledText padding(int left, int top, int right, int bottom) {
        if (padding == null) {
            padding = new int[4];
        }
        padding[0] = left;
        padding[1] = top;
        padding[2] = right;
        padding[3] = bottom;
        return this;
    }

    public StyledText maxLine(int line) {
        maxl = line;
        return this;
    }

    /**
     * 設定行高
     *
     * @param mult 行高倍數，default是1
     * @return
     */
    public StyledText high(float mult) {
        this.high = mult;
        return this;
    }

    public TextView create(Context context) {
        TextView textView = new TextView(context);
        apply(textView);
        return textView;
    }

    public StyledText set(TextView tv) {
        this.tv = tv;
        return this;
    }

    public StyledText setMatrix(Matrix m) {
        this.m = m;
        return this;
    }

    public StyledText font(Typeface tf) {
        this.tf = tf;
        return this;
    }

    public TextView get() {
        if (txt == null && tv.getText().toString().contains("</")) {
            this.txt = Html.fromHtml(tv.getText().toString());
        }
        apply(tv, this.txt);
        return tv;
    }

    public TextView apply(TextView tv) {
        return apply(tv, this.txt);
    }

    public TextView apply(TextView tv, CharSequence txt) {
        if (bg != null) {
            tv.setBackgroundResource(bg);
        }
        if (bgc != null) {
            tv.setBackgroundColor(bgc);
        }
        if (b) {
            tv.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (strike) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        if (gravity != 0) {
            tv.setGravity(gravity);
        }
        //Log.d("familife", "use co=" + co);
        if (co != null) {
            tv.setTextColor(co);
        }
        if (size > 0) {
            if (unit == Unit.Auto && m != null) {
                tv.setTextSize(unit.Px.ordinal(), m.mapRadius(size));
            } else {
                tv.setTextSize(unit.ordinal(), size);
            }
        }
        if (high != 1f) {
            tv.setLineSpacing(0, high);
        }
        if (underline) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
        if (t != null) {
            tv.setTag(t);
        }
        if (txt != null) {
            tv.setText(txt);
        }
        if (hint != null) {
            ((EditText) tv).setHint(hint);
        }
        if (allowChars != null) {
            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    String replacement = source.subSequence(start, end).toString();
                    String finalString = replacement.replaceAll("[^" + allowChars + "]", "");
                    return finalString.equals(replacement) ? null : finalString;
                }
            };
            ((EditText) tv).setFilters(new InputFilter[]{filter});
        }
        if (padding != null) {
            if (m != null) {
                tv.setPadding((int) m.mapRadius(padding[0]), (int) m.mapRadius(padding[1]), (int) m.mapRadius(padding[2]), (int) m.mapRadius(padding[3]));
            } else {
                tv.setPadding(padding[0], padding[1], padding[2], padding[3]);
            }
        }
        if (maxl > 0) {
            tv.setMaxLines(maxl);
            if (maxl == 1) {
                tv.setSingleLine(true);
            }
        }
        if (nowrap) {
            tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        if (tf != null) {
            tv.setTypeface(tf);
        }
        if (format != Format.Default) {
            switch (format) {
                case None:
                    ((EditText) tv).setInputType(InputType.TYPE_NULL);
                    break;
                case Number:
                    ((EditText) tv).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    break;
                case Phone:
                    ((EditText) tv).setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case English:
                    ((EditText) tv).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    break;
                case Password:
                    ((EditText) tv).setTransformationMethod(PasswordTransformationMethod.getInstance());
                    ((EditText) tv).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
            }
        }
        return tv;
    }

    public static void lock(EditText et) {
        et.setInputType(InputType.TYPE_NULL);
        et.setFocusable(false);
        et.setFocusableInTouchMode(false);
    }

    public static void unlock(EditText et, Format format) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        switch (format) {
            case None:
                et.setInputType(InputType.TYPE_NULL);
                break;
            case Number:
                et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case Phone:
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case English:
//                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case Password:
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                et.setImeOptions(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case Default:
                et.setInputType(InputType.TYPE_CLASS_TEXT);
//                et.setImeOptions(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }
}
