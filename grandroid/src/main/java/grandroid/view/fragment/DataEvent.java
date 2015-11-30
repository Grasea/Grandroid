/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.view.fragment;

/**
 *
 * @author Rovers
 */
public class DataEvent {

    protected String key;
    protected Component source;
    protected Object data;

    public DataEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public DataEvent setKey(String key) {
        this.key = key;
        return this;
    }

    public Component getSource() {
        return source;
    }

    public void setSource(Component source) {
        this.source = source;
    }

    public Object getData() {
        return data;
    }

    public DataEvent setData(Object data) {
        this.data = data;
        return this;
    }
}
