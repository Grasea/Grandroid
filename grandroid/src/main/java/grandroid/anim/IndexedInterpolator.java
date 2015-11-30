/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.anim;

import android.view.animation.Interpolator;

/**
 *
 * @author Rovers
 */
public class IndexedInterpolator implements Interpolator {

    protected int spanCount;
    protected float spanWidth;

    public IndexedInterpolator(int spanCount) {
        this.spanCount = spanCount;
        spanWidth = 1f / spanCount;
    }

    public float getInterpolation(float t) {
        int index = Math.round((t + spanWidth / 2) / spanWidth);
        return spanWidth * (index >= spanCount ? 0 : index);
    }

}
