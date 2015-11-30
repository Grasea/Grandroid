/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.fragment;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Rovers
 */
public class ObserverTarget extends Observable {

    protected String key;

    public ObserverTarget(String key) {
        this.key = key;
    }

    @Override
    public void addObserver(Observer observer) {
        this.deleteObserver(observer);
        super.addObserver(observer);
    }

    public void dirty() {
        setChanged();
    }
}
