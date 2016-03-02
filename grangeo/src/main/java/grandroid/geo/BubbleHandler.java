/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public abstract class BubbleHandler implements GoogleMap.InfoWindowAdapter {

    protected LinearLayout layout;
    protected boolean initialized;

    public BubbleHandler(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        //layout.setVisibility(View.INVISIBLE);
    }

    public LinearLayout getView() {
        return layout;
    }

    public View getInfoWindow(Marker marker) {
        if (!initialized) {
            createBubble(new LayoutMaker(layout.getContext(), layout, false));
            initialized = true;
        }
        fillBubble(layout, marker.getPosition(), marker);
        return layout;
    }

    public View getInfoContents(Marker marker) {
        return null;
    }

    public abstract void createBubble(LayoutMaker lm);

    public abstract void fillBubble(LinearLayout layout, LatLng point, Marker marker);

    public void onClickBubble(Marker marker) {
    }

    /**
     *
     * @param <T>
     * @param v
     * @param tag
     * @param c
     * @return
     */
    protected <T extends View> T findView(View v, String tag, Class<T> c) {
        if (v.getTag() != null && v.getTag().equals(tag)) {
            return (T) v;
        }
        if (v instanceof ViewGroup) {
            View answer = null;
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                answer = findView(((ViewGroup) v).getChildAt(i), tag, c);
                if (answer != null) {
                    return (T) answer;
                }
            }
            return null;
        }
        return null;
    }
}
