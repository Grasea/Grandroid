/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.sidemenu;

import android.view.View;
import grandroid.view.LayoutMaker;

/**
 * Helper that remembers the width of the 'slide' button, so that the 'slide'
 * button remains in view, even when the menu is showing.
 */
public class SizeCallbackForMenu implements SideMenuHorizontalScrollView.SizeCallback {

    int btnWidth;
    View btnSlide;
    int mode;
    int amount;

    public SizeCallbackForMenu(View btnSlide, int mode, int amount) {
        super();
        this.btnSlide = btnSlide;
        this.mode = mode;
        this.amount = amount;
    }

    @Override
    public void onGlobalLayout() {
        if (mode == LayoutMaker.SIDEMENU_SHIFTMODE_RIGHT_FIT_BUTTON) {
            btnWidth = btnSlide.getMeasuredWidth() + amount;
        }
    }

    @Override
    public void getViewSize(int idx, int w, int h, int[] dims) {
        dims[0] = w;
        dims[1] = h;
        final int menuIdx = 0;
        if (idx == menuIdx) {
            switch (mode) {
                case LayoutMaker.SIDEMENU_SHIFTMODE_RIGHT_FIT_BUTTON:
                    dims[0] = w - btnWidth;
                    break;
                case LayoutMaker.SIDEMENU_SHIFTMODE_RIGHT:
                    dims[0] = w - amount;
                    break;
                case LayoutMaker.SIDEMENU_SHIFTMODE_LEFT:
                    dims[0] = amount;
                    break;
            }
        }
    }
}
