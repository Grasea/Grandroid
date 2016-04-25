package grandroid.geo.sample;

import android.os.Bundle;

import grandroid.action.GoAction;
import grandroid.view.Face;

/**
 * Created by Alan Ding on 2016/4/19.
 */
public class MapComponentActivity extends Face {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_component);
        new GoAction(this, ComponentMap.class, R.id.frame).execute();
    }
}