/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 *
 * @author Rovers
 */
public class PageChangeListener implements ViewPager.OnPageChangeListener {

    protected View currentView;
    protected ViewGroup vp;
    protected int orientation;
    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;

    public PageChangeListener(ViewGroup pager) {
        this.vp = pager;
        if (vp instanceof ViewPager) {
            orientation = HORIZONTAL;
        } else if (vp instanceof VerticalViewPager) {
            orientation = VERTICAL;
        }

        pager.post(new Runnable() {
            public void run() {
                currentView = getCurrentView(vp);
                if (vp instanceof ViewPager) {
                    afterPageSelected(((ViewPager) vp).getCurrentItem(), currentView);
                } else if (vp instanceof VerticalViewPager) {
                    afterPageSelected(((VerticalViewPager) vp).getCurrentItem(), currentView);
                }
            }
        });
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public final void onPageScrolled(int i, float f, int i1) {
    }

    public void onPageSelected(int i) {
    }

    public final void onPageScrollStateChanged(int status) {
        if (status == 0) {
            currentView = getCurrentView(vp);
            if (vp instanceof ViewPager) {
                afterPageSelected(((ViewPager) vp).getCurrentItem(), currentView);
            } else if (vp instanceof VerticalViewPager) {
                afterPageSelected(((VerticalViewPager) vp).getCurrentItem(), currentView);
            }

        }
    }

    public void afterPageSelected(int index, View currentView) {
    }

    public View getCurrentView(ViewGroup pager) {
        for (int i = 0; i < pager.getChildCount(); i++) {
            View child = pager.getChildAt(i);
            if (orientation == HORIZONTAL) {
                if (child.getX() >= pager.getScrollX() && child.getX() + child.getWidth() <= pager.getScrollX() + pager.getWidth()) {
                    return child;
                }
            } else {
                if (child.getY() >= pager.getScrollY() && child.getY() + child.getHeight() <= pager.getScrollY() + pager.getHeight()) {
                    return child;
                }
            }
        }
        return pager.getChildAt(0);
    }
}
