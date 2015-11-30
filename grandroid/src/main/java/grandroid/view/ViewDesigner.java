/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.*;

/**
 *
 * @author Rovers
 */
public class ViewDesigner {

    /**
     *
     */
    protected int fontSize = 20;
    /**
     *
     */
    protected int fontColor = Color.BLACK;

    /**
     *
     * @param tv
     * @return
     */
    public TextView stylise(TextView tv) {
        tv.setTextColor(fontColor);
        tv.setTextSize(fontSize);
        return tv;
    }

    /**
     *
     * @param et
     * @return
     */
    public EditText stylise(EditText et) {
        et.setTextColor(fontColor);
        et.setTextSize(fontSize);
        return et;
    }

    /**
     *
     * @param lv
     * @return
     */
    public ListView stylise(ListView lv) {
        lv.setCacheColorHint(Color.argb(0, 0, 0, 0));
        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        return lv;
    }

    /**
     *
     * @param gallery
     * @return
     */
    public Gallery stylise(Gallery gallery) {
        return gallery;
    }

    /**
     *
     * @param gv
     * @return
     */
    public GridView stylise(GridView gv) {
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        return gv;
    }

    /**
     *
     * @param btn
     * @return
     */
    public Button stylise(Button btn) {
        btn.setTextColor(fontColor);
        btn.setTextSize(fontSize);
        return btn;
    }

    /**
     *
     * @param iv
     * @return
     */
    public ImageView stylise(ImageView iv) {
        iv.setPadding(0, 0, 0, 0);
        iv.setBackgroundResource(0);
        return iv;
    }

    /**
     *
     * @param ib
     * @return
     */
    public ImageButton stylise(ImageButton ib) {
        ib.setPadding(0, 0, 0, 0);
        return ib;
    }

    /**
     *
     * @param spinner
     * @return
     */
    public Spinner stylise(Spinner spinner) {
        return spinner;
    }

    /**
     *
     * @param cb
     * @return
     */
    public CheckBox stylise(CheckBox cb) {
        cb.setTextColor(fontColor);
        cb.setTextSize(fontSize);
        return cb;
    }

    /**
     *
     * @param rb
     * @return
     */
    public RadioButton stylise(RadioButton rb) {
        rb.setTextSize(fontSize - 4);
        rb.setTextColor(fontColor);
        return rb;
    }

    /**
     *
     * @param layout
     * @return
     */
    public LinearLayout stylise(LinearLayout layout) {
        return layout;
    }
}
