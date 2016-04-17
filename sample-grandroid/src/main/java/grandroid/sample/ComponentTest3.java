package grandroid.sample;

import android.os.Bundle;
import android.view.View;

import grandroid.action.GoAction;
import grandroid.view.LayoutMaker;
import grandroid.view.fragment.Component;

/**
 * Created by Alan Ding on 2016/3/29.
 */
public class ComponentTest3 extends ComponentBase {

    @Override
    public void onCreateView(LayoutMaker maker, Bundle savedInstanceState) {
        super.onCreateView(maker, savedInstanceState);
        maker.addTextView("page 3");
        maker.addButton("go page1").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GoAction(getFace(), ComponentTest1.class, R.id.frame).removeOldFace().goDirection(GoAction.Direction.Right).execute();
            }
        });
    }
    @Override
    protected MainActivity.UISetting getUISetting() {
        return new MainActivity.UISetting(true, "Title:Page3");
    }
}
