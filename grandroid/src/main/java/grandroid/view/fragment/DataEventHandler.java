/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.fragment;

import java.util.Observer;

/**
 * Subject role in Observer Pattern
 * @author Rovers
 */
public interface DataEventHandler {

    public void registerDataEvent(String key, Observer obs);

    public void unregisterDataEvent(String key, Observer obs);

    public void notifyEvent(DataEvent event);
}
