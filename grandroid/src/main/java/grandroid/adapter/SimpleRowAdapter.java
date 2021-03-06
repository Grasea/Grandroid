package grandroid.adapter;

import android.view.View;
import android.view.ViewGroup;
import grandroid.action.Action;

/**
 *
 * @author Jack Huang
 */
public abstract class SimpleRowAdapter<T> implements RowAdapter<T>, ItemClickable<T> {

    protected UniversalAdapter parentAdapter;

    public void setParentAdapter(UniversalAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    public <T extends View> T setButtonEvent(T view, final Action action) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                action.setSrc(view);
                action.execute();
            }
        });
        return view;
    }

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

    public void onLongPressItem(int index, View view, T item) {
    }

    public void onClickItem(int index, View view, T item) {
    }

    public void notifyDataSetChanged() {
        if (parentAdapter != null) {
            parentAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataSetInvalidated() {
        if (parentAdapter != null) {
            parentAdapter.notifyDataSetInvalidated();
        }
    }
}
