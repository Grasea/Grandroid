/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import grandroid.data.StateKeeper;

/**
 *
 * @author Rovers
 */
public class FancyFace extends Face {

    protected LinearLayout rootLayout;
    protected LayoutMaker maker;
    protected int lastOrientitation;
    protected StateKeeper keeper;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setBackgroundColor(Color.WHITE);
        setContentView(rootLayout);
        maker = new LayoutMaker(this, rootLayout, false);
        maker.addColLayout(false, maker.layFF());
        if (init()) {
            createCommonLayout(maker);
            lastOrientitation = getResources().getConfiguration().orientation;
            if (isShowDetailLayout()) {
                if (lastOrientitation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    createVerticalLayout(maker);
                } else {
                    createHorizantalLayout(maker);
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (lastOrientitation != newConfig.orientation) {
            lastOrientitation = newConfig.orientation;
            updateLayout(isAutoSaveViewState());
        }
    }

    @Override
    protected void onDestroy() {
        if (keeper != null) {
            keeper.removeTemporaryKeys();
        }
        super.onDestroy();
    }

    /**
     * 呼叫後可以重建UI。配合isRebulidWhileOrientationChanged()以決定完全重建或部分重建
     */
    protected void updateLayout(boolean restoreSavedViewState) {
        if (isRebulidWhileOrientationChanged()) {
            if (keeper != null) {
                keeper.digest();
            }
            rootLayout.removeAllViews();
            ViewDesigner vd = maker.getDesigner();
            maker = new LayoutMaker(this, rootLayout, false);
            maker.setDesigner(vd);
            maker.addColLayout(false, maker.layFF());
            createCommonLayout(maker);
        }
        if (isShowDetailLayout()) {
            if (lastOrientitation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                createVerticalLayout(maker);
            } else {
                createHorizantalLayout(maker);
            }
        }
    }

    /**
     * 覆寫此函數以執行頁面的初始化工作，此函數只會在onCreate時執行過一次。
     *
     * @return false代表初始化失敗，會直接結束此Activity。
     */
    protected boolean init() {
        return true;
    }

    /**
     * 回傳是否在螢幕方向改變後重建整個UI (即CommonLayout+垂直/水平Layout)
     *
     * @return
     */
    protected boolean isRebulidWhileOrientationChanged() {
        return true;
    }

    /**
     * 覆寫此函數以決定是否要生成垂直/水平Layout
     * 若一個頁面需要執行ThreadAction以取得資料，則此函數應設計為false，待ThreadAction
     * callback後，再手動呼叫createVerticalLayout/createHorizantalLayout
     *
     * @return
     */
    protected boolean isShowDetailLayout() {
        return true;
    }

    protected boolean isAutoSaveViewState() {
        return true;
    }

    /**
     * 覆寫此函數以生成共用的頁面部分 (執行順序在重建垂直/水平Layout之前)
     *
     * @param maker
     */
    protected void createCommonLayout(LayoutMaker maker) {
    }

    /**
     * 建立直立式的Layout的code
     *
     * @param maker
     */
    protected void createVerticalLayout(LayoutMaker maker) {
    }

    /**
     * 建立直立式的Layout的code
     *
     * @param maker
     */
    protected void createHorizantalLayout(LayoutMaker maker) {
    }

    protected StateKeeper getKeeper() {
        if (keeper == null) {
            keeper = new StateKeeper(this);
        }
        return keeper;
    }
}
