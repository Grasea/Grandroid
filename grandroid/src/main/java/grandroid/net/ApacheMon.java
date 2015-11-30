/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.net;

import android.util.Log;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Rovers
 */
public class ApacheMon extends Mon {

    protected HashMap<String, String> headerParams;
    ArrayList<NameValuePair> pairList;
    List<Cookie> cookies = null;

    /**
     *
     * @param uri 欲擷取資料的URL
     */
    public ApacheMon(String uri) {
        this(uri, false);
    }

    /**
     *
     * @param uri 欲擷取資料的URL
     */
    public ApacheMon(String uri, boolean keepCookie) {
        super(uri, keepCookie);
        pairList = new ArrayList<NameValuePair>();
    }

    public ApacheMon encode(String encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * 新增一組傳輸參數
     *
     * @param key 參數的名字
     * @param value 參數值
     * @return Mon物件本身，方便串接
     */
    @Override
    public ApacheMon put(String key, String value) {
        param.put(key, value);
        pairList.add(new BasicNameValuePair(key, value));
        return this;
    }

    public ApacheMon putHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<String, String>();
        }
        headerParams.put(key, value);
        return this;
    }

    @Override
    public ApacheMon asHttps() {
        this.isHttps = true;
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public ApacheMon asPost() {
        method = 0;
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public ApacheMon asGet() {
        method = 1;
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public ApacheMon asPut() {
        method = 2;
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public ApacheMon asDelete() {
        method = 3;
        return this;
    }

    /**
     * 清除變數
     */
    @Override
    public void clear() {
        param.clear();
        pairList.clear();
    }

    /**
     * 開始連線傳輸
     *
     * @return server端回應的字串傳回
     */
    public String send() {
        try {
            return sendWithError();
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return "{msg:\"" + ex.toString() + "\"}";
        }
    }

    protected SSLSocketFactory newSslSocketFactory() {
        try {
// Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
// Get the raw resource, which contains the keystore with
// your trusted certificates (root and any intermediate certs)
// Pass the keystore to the SSLSocketFactory. The factory is responsible
// for the verification of the server certificate.
            SSLSocketFactory sf = new SSLSocketFactory(trusted);
// Hostname verification from certificate
// http://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html#d4e506
            sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    public class MySSLSocketFactory extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
        }

        public MySSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            super(null);
            sslContext = context;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    protected HttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);

        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        if (isHttps) {
            try {
                //trustEveryone();
                X509TrustManager tm = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
                ssf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                schReg.register(new Scheme("https", ssf, 443));//SSLSocketFactory.getSocketFactory()
            } catch (Exception ex) {
                Log.e("grandroid", null, ex);
            }
        } else {
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        }
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        DefaultHttpClient httpClient = new DefaultHttpClient(conMgr, params);
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        if (keepingCookie && cookies != null) {
            for (Cookie c : cookies) {
                httpClient.getCookieStore().addCookie(c);
            }
        }
//        DefaultHttpClient client = new DefaultHttpClient();
//
//        SchemeRegistry registry = new SchemeRegistry();
//        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        registry.register(new Scheme("https", newSslSocketFactory(), 443));
//        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//        DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
////        httpClient.getParams().setParameter(
//                ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
// Set verifier     
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

//        return new DefaultHttpClient(conMgr, params);
        return httpClient;
    }

    public HttpResponse sendWithErrorGetHttpResponse() throws Exception {
        HttpClient client = createHttpClient();

//        DefaultHttpClient demo = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.content-charset", encoding);
//        client.getParams().setParameter(AllClientPNames.HANDLE_REDIRECTS, false);
//        client.getParams().setParameter("Cache-Control", "max-age=0");

        // Get Request Example，取得 google 查詢 httpclient 的結果
        HttpRequestBase request = null;
        switch (method) {

            case 0:
                request = new HttpPost(uri);
                if (pairList.size() > 0) {
                    StringEntity entity = new StringEntity(URLEncodedUtils.format(pairList, encoding));
                    ((HttpPost) request).setEntity(entity);
                }

                break;
            case 1:
                request = new HttpGet(uri + (param.isEmpty() ? "" : "?" + getParameters()));
                break;
            case 2:
                request = new HttpPut(uri);
                if (pairList.size() > 0) {
                    StringEntity entity = new StringEntity(URLEncodedUtils.format(pairList, encoding));
                    ((HttpPut) request).setEntity(entity);
                }
                break;
            case 3:
                request = new HttpDelete(uri + (param.isEmpty() ? "" : "?" + getParameters()));
                break;
        }
        //request.setHeader("Accept", "text/json,text/html,application/xhtml+xml,application/xml;");
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        if (headerParams != null) {
            for (String key : headerParams.keySet()) {
                request.setHeader(key, headerParams.get(key));
            }
        }
        HttpResponse response = client.execute(request);
        if (keepingCookie) {
            cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
        }

        return response;
    }

    @Override
    public String sendWithError() throws Exception {
        HttpResponse response = sendWithErrorGetHttpResponse();
        String responseString = EntityUtils.toString(response.getEntity());
        if (response.getStatusLine().getStatusCode() >= HttpStatus.SC_OK && response.getStatusLine().getStatusCode() < HttpStatus.SC_BAD_REQUEST) {
            // 如果回傳是 200~399 的話才輸出
            //System.out.println(responseString);
            return responseString;
        } else {
            throw new Exception("http connect fail code=" + response.getStatusLine().getStatusCode());
        }
    }
}
