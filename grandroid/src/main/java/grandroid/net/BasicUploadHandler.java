/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.net;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.File;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public class BasicUploadHandler extends Handler {

    /**
     *
     */
    protected Context context;
    /**
     *
     */
    protected String url;
    /**
     *
     */
    protected String col;

    /**
     *
     * @param context
     * @param looper
     * @param url
     * @param col
     */
    public BasicUploadHandler(Context context, Looper looper, String url, String col) {
        super(looper);
        this.context = context;
        this.url = url;
        this.col = col;
    }

    /**
     * need bundle parameters PATH & JSON
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        Bundle b = msg.getData();
        String path = b.getString("PATH");
        String json = b.getString("JSON");

        try {
            String result = new FilePoster().post(url, new File(path), col, json, null);
            Log.d("grandroid", "upload photo result = " + result);
            JSONObject uploadResult = new JSONObject(result);
            afterUpload(b, path, uploadResult);
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }

    }

    /**
     *
     * @param b
     * @param path
     * @param uploadResult
     * @throws Exception
     */
    public void afterUpload(Bundle b, String path, JSONObject uploadResult) throws Exception {
    }
}
