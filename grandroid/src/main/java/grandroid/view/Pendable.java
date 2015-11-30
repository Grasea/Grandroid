/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view;

import grandroid.action.PendingAction;

/**
 *
 * @author Rovers
 */
public interface Pendable {
    public void registerPendingAction(PendingAction pa);
}
