/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.sidemenu;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

/**
 *
 * @author Rovers
 */
public class SideMenuController implements View.OnClickListener {

    HorizontalScrollView scrollView;
    /**
     * Menu must NOT be out/shown to start with.
     */
    View menu;
    View btn;
    boolean menuOut = false;
    int resBtnNormal = 0;
    int resBtnExpanded = 0;

    public SideMenuController(HorizontalScrollView scrollView, View menu, View btn) {
        this.scrollView = scrollView;
        this.menu = menu;
        this.btn = btn;
    }

    public SideMenuController(HorizontalScrollView scrollView, View menu, View btn, int resBtnNormal, int resBtnExpanded) {
        this.scrollView = scrollView;
        this.menu = menu;
        this.btn = btn;
        this.resBtnNormal = resBtnNormal;
        this.resBtnExpanded = resBtnExpanded;
    }

    public boolean isMenuOut() {
        return menuOut;
    }

    public void switchMenu() {
        int menuWidth = menu.getMeasuredWidth();
        // Ensure menu is visible
        menu.setVisibility(View.VISIBLE);
        if (!menuOut) {
            // Scroll to 0 to reveal menu
            int left = 0;
            if (resBtnExpanded > 0) {
                ((ImageView) btn).setImageResource(resBtnExpanded);
            }
            scrollView.smoothScrollTo(left, 0);
        } else {
            // Scroll to menuWidth so menu isn't on screen.
            int left = menuWidth;
            if (resBtnNormal > 0) {
                ((ImageView) btn).setImageResource(resBtnNormal);
            }
            scrollView.smoothScrollTo(left, 0);
        }
        menuOut = !menuOut;
    }

    public void closeMenu() {
        if (menuOut) {
            switchMenu();
        }
    }

    public void showMenu() {
        if (!menuOut) {
            switchMenu();
        }
    }

    public void onClick(View arg0) {
        switchMenu();
    }
}
