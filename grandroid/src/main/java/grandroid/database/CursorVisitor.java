/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.database;

import android.database.Cursor;

/**
 *
 * @author Rovers
 */
public abstract class CursorVisitor {

    /**
     * 
     * @param cursor
     * @return
     */
    public abstract boolean handleRow(Cursor cursor);
    
    /**
     * 
     */
    public void afterVisit(){
        
    }

    /**
     * 
     * @param ex
     */
    public void onError(Exception ex) {
    }
}
