/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.net;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

/**
 * @author Rovers
 */
public class FilePoster {

    protected String postParamName;
    protected HashMap<String, String> params;
    protected HashMap<String, String> headerParams;
    protected String fileContentType;

    public FilePoster() {
        postParamName = "file";
        fileContentType = "application/octet-stream";
    }

    public FilePoster setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public FilePoster put(String key, String value) {
        if (params == null) {
            params = new HashMap<String, String>();
        }

        params.put(key, value);
        return this;
    }

    public FilePoster putHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<String, String>();
        }
        headerParams.put(key, value);
        return this;
    }

    public FilePoster setPostParamName(String postParamName) {
        this.postParamName = postParamName;
        return this;
    }

    /**
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    public String post(String url, File file) throws Exception {
        return post(url, file, null, null, null);
    }

    /**
     * @param url
     * @param file
     * @param col
     * @param json
     * @param site
     * @return
     * @throws Exception
     */
    public String post(String url, File file, String col, String json, String site) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(url);

        PostFileEntity mpEntity = new PostFileEntity();
        if (col != null && col.length() > 0) {
            mpEntity.addPart("col", col);
        }
        if (json != null && json.length() > 0) {
            mpEntity.addPart("json", json);
        }
        if (site != null && site.length() > 0) {
            mpEntity.addPart("site", site);
        }
        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                httppost.addHeader(key, headerParams.get(key));
            }
        }
        if (params != null) {
            for (String key : params.keySet()) {
                mpEntity.addPart(key, params.get(key));
            }
        }
        mpEntity.addPart(postParamName, file, fileContentType);
        httppost.setEntity(mpEntity);
        //System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        //System.out.println(response.getStatusLine());
        String responseText = "";
        if (resEntity != null) {
            responseText = EntityUtils.toString(resEntity, "UTF-8");
        }
        if (resEntity != null) {
            resEntity.consumeContent();
        }

        httpclient.getConnectionManager().shutdown();
        return responseText.trim();
    }

    public String post(String url, String filename, InputStream is) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        HttpPost httppost = new HttpPost(url);

        PostFileEntity mpEntity = new PostFileEntity();
        mpEntity.addPart(postParamName, filename, is, fileContentType);
        httppost.setEntity(mpEntity);

        //System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        //System.out.println(response.getStatusLine());
        String responseText = "";
        if (resEntity != null) {
            responseText = EntityUtils.toString(resEntity);
        }
        if (resEntity != null) {
            resEntity.consumeContent();
        }

        httpclient.getConnectionManager().shutdown();
        return responseText.trim();
    }
}
