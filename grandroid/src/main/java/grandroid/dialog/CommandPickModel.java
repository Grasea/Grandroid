/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.dialog;

import android.content.DialogInterface;

/**
 *
 * @author Rovers
 */
public abstract class CommandPickModel implements DialogInterface.OnClickListener {

    /**
     * 
     */
    protected String title;
    /**
     * 
     */
    protected String[] extraCmds;

    /**
     * 
     * @param title
     * @param extraCmds
     */
    public CommandPickModel(String title, String... extraCmds) {
        this.title = title;
        this.extraCmds = extraCmds;
    }

    /**
     * 
     * @return
     */
    public String[] getStringArray() {
        return extraCmds;
    }

    /**
     * 
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param arg0
     * @param index
     */
    public void onClick(DialogInterface arg0, int index) {
        onCommand(index);
    }

    /**
     * 
     * @param cmdIndex
     */
    public abstract void onCommand(int cmdIndex);
}
