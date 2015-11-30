/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.net;

/**
 *
 * @author Rovers
 */
public class BaseAPI {

    protected static String[] API_SITES = new String[]{""};
    protected static int DEFAULT_SITE_INDEX = 0;

    public static String getAPISite() {
        return API_SITES[DEFAULT_SITE_INDEX];
    }

    public static String getAPISite(int siteIndex) {
        return API_SITES[siteIndex];
    }

    protected static APICall createAPI() {
        return new APICall(null, getAPISite());
    }

    protected static APICall createAPI(Object actionTag) {
        return new APICall(actionTag, getAPISite());
    }

    protected static APICall createAPI(Object actionTag, int siteIndex) {
        return new APICall(actionTag, getAPISite(siteIndex));
    }

    protected static APICall createAPI(Object actionTag, String suffix) {
        return new APICall(actionTag, getAPISite(), suffix);
    }

    protected static APICall createAPI(Object actionTag, int siteIndex, String suffix) {
        return new APICall(actionTag, getAPISite(siteIndex), suffix);
    }

    public static APICall sampleCall(ResultHandler handler, String deviceID) {
        return createAPI(null).setHandler(handler);
    }
}
