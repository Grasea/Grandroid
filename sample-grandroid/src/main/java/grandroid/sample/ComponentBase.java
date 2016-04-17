package grandroid.sample;

import grandroid.view.fragment.Component;

/**
 * Created by Alan Ding on 2016/3/31.
 */
public class ComponentBase extends Component {
    @Override
    public void onResume() {
        super.onResume();
        fireDataEvent("UI_CHANGE", getUISetting());
    }

    protected MainActivity.UISetting getUISetting() {
        return new MainActivity.UISetting(true, "");
    }

}
