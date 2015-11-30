/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.net;

import android.content.Context;
import grandroid.action.AsyncAction;
import grandroid.net.Mon;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public class APICall<T> {

    protected T actionTag;
    protected Mon mon;
    protected ResultHandler handler;
    protected boolean showDialog;
    protected Context context;
    protected String message;

    public APICall(T actionTag, String apiSite) {
        this(actionTag, apiSite, "");
    }

    public APICall(T actionTag, String apiSite, String suffix) {
        this.actionTag = actionTag;
        mon = new Mon(apiSite + suffix);
        showDialog = false;
    }

    public ResultHandler getHandler() {
        return handler;
    }

    public APICall setHandler(ResultHandler handler) {
        this.handler = handler;
        return this;
    }

    public APICall asGet() {
        mon.asGet();
        return this;
    }

    public APICall asPost() {
        mon.asPost();
        return this;
    }

    public APICall put(String key, String value) {
        mon.put(key, value);
        return this;
    }

    public Mon getMon() {
        return mon;
    }

    public APICall message(String message) {
        this.message = message;
        return this;
    }

    public APICall showLoading(Context context) {
        this.context = context;
        showDialog = true;
        return this;
    }

    public void execute() {
        AsyncAction<JSONObject> act = new AsyncAction<JSONObject>(context) {

            @Override
            public void afterExecution(JSONObject result) {
                if (handler != null) {
                    handler.onAPIResult(actionTag, result);
                }
            }

            @Override
            public boolean execute(Context context) {
                try {
                    setResult(mon.sendAndWrap());
                } catch (Exception ex) {
                    if (handler != null) {
                        handler.onAPIError(actionTag, ex);
                    }
                }
                return true;
            }

        };
        if (!showDialog) {
            act.silence();
        }
        if (message != null) {
            act.message(message);
        }
        act.execute();
    }
}
