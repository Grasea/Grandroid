/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.dialog;

import android.content.DialogInterface;
import java.util.List;

/**
 *
 * @param <T> 
 * @author Rovers
 */
public abstract class ObjectPickModel<T> extends CommandPickModel {

    /**
     * 
     */
    protected List<T> list;

    /**
     * 
     * @param title
     * @param list
     * @param cmds
     */
    public ObjectPickModel(String title, List<T> list, String... cmds) {
        super(title,cmds);
        this.list = list;
    }

    /**
     * 
     * @return
     */
    @Override
    public String[] getStringArray() {
        String[] typearr = new String[list.size() + extraCmds.length];
        for (int i = 0; i < list.size(); i++) {
            typearr[i] = getDisplayString(list.get(i));
        }
        System.arraycopy(extraCmds, 0, typearr, list.size(), extraCmds.length);
        return typearr;
    }

    /**
     * 
     * @param arg0
     * @param index
     */
    @Override
    public void onClick(DialogInterface arg0, int index) {
        if (index < list.size()) {
            onPicked(index, list.get(index));
        } else {
            onCommand(index - list.size());
        }
    }

    /**
     * 
     * @param obj
     * @return
     */
    protected abstract String getDisplayString(T obj);

    /**
     * 
     * @param index
     * @param obj
     */
    public abstract void onPicked(int index, T obj);

    /**
     * 
     * @param cmdIndex
     */
    public void onCommand(int cmdIndex) {
    }
}
