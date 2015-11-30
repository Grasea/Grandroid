/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public abstract class MakerRowAdapter<T> extends SimpleRowAdapter<T> {

    public static final int DEFAULT_ORIENTATION = LinearLayout.VERTICAL;

    public int getDefaultOrientation() {
        return DEFAULT_ORIENTATION;
    }

    public View createRowView(Context context, int index, T item) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(getDefaultOrientation());
        return layout;
    }

    public void fillRowView(Context context, int index, View cellRenderer, T item) throws Exception {
        LinearLayout ll = ((LinearLayout) cellRenderer);
        ll.removeAllViews();
        fillRowView(context, index, new LayoutMaker(context, (LinearLayout) cellRenderer, false), item);
    }

    public abstract void fillRowView(Context context, int index, LayoutMaker m, T item) throws Exception;

    public void setVertical(LayoutMaker m) {
        m.getMainLayout().setOrientation(LinearLayout.VERTICAL);
    }

    public void setHorizontal(LayoutMaker m) {
        m.getMainLayout().setOrientation(LinearLayout.HORIZONTAL);
    }
}
