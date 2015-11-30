/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.location.Address;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public class Geocoder {

    public static String convertAddress(double lat, double lng, boolean nation, boolean city, boolean district, boolean street) throws Exception {
        List<Address> adds = getFromLocation(lat, lng, 1);
        if (adds == null || adds.isEmpty()) {
            throw new Exception("no address can be found");
        } else {
            Address add = adds.get(0);
            StringBuilder sb = new StringBuilder();
            if (nation) {
                sb.append(add.getCountryName());
            }
            if (city) {
                sb.append(add.getAdminArea());
            }
            if (district) {
                sb.append(add.getLocality());
            }
            if (street) {
                sb.append(add.getAddressLine(0));
            }
            return sb.toString();
        }
    }

    public static String convertAddress(double lat, double lng) throws Exception {
        List<Address> adds = getFromLocation(lat, lng, 1);
        if (adds == null || adds.isEmpty()) {
            throw new Exception("no address can be found");
        } else {
            Address add = adds.get(0);
            if (add.getFeatureName() == null) {
                return add.getAdminArea() + add.getLocality() + add.getAddressLine(0);
            } else {
                return add.getFeatureName();
            }
        }
    }

    public static List<Address> getFromLocation(double lat, double lng, int maxResult) {
        return getFromLocation(Locale.TAIWAN, lat, lng, maxResult);
    }

    public static List<Address> getFromLocation(Locale locale, double lat, double lng, int maxResult) {
        String language = locale.getLanguage();
        if (locale == Locale.TAIWAN) {
            language = "zh-TW";
        }
        String address = String.format(locale, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + language, lat, lng);//locale.getCountry()
        HttpGet httpGet = new HttpGet(address);
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(AllClientPNames.USER_AGENT, "Mozilla/5.0 (Java) Gecko/20081007 java-geocoder");
        client.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 5 * 1000);
        client.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 25 * 1000);
        HttpResponse response;

        List<Address> retList = null;

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            retList = new ArrayList<Address>();

            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    for (int i = 0; i < results.length() && i < maxResult; i++) {
                        JSONObject result = results.getJSONObject(i);
                        //Log.e(MyGeocoder.class.getName(), result.toString());
                        Address addr = new Address(Locale.getDefault());
                        // addr.setAddressLine(0, result.getString("formatted_address"));

                        JSONArray components = result.getJSONArray("address_components");
                        String streetNumber = "";
                        String route = "";
                        for (int a = 0; a < components.length(); a++) {
                            JSONObject component = components.getJSONObject(a);
                            JSONArray types = component.getJSONArray("types");
                            for (int j = 0; j < types.length(); j++) {
                                String type = types.getString(j);
                                if (type.equals("locality") || type.equals("administrative_area_level_3")) {
                                    addr.setLocality(component.getString("long_name"));
                                } else if (type.equals("street_number")) {
                                    streetNumber = component.getString("long_name");
                                } else if (type.equals("route")) {
                                    route = component.getString("long_name");
                                } else if (type.equals("administrative_area_level_1")) {
                                    addr.setAdminArea(component.getString("long_name"));
                                } else if (type.equals("country")) {
                                    addr.setCountryName(component.getString("long_name"));
                                    addr.setCountryCode(component.getString("short_name"));
                                }
                            }
                        }
                        addr.setAddressLine(0, route + " " + streetNumber);
                        if (result.has("formatted_address")) {
                            addr.setFeatureName(result.getString("formatted_address"));
                        }
                        addr.setLatitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        addr.setLongitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        if (addr.getAdminArea() == null) {
                            addr.setAdminArea("");
                        }
                        retList.add(addr);
                    }
                }
            }
        } catch (ClientProtocolException e) {
            Log.e("grandroid", "Error calling Google geocode webservice.", e);
        } catch (IOException e) {
            Log.e("grandroid", "Error calling Google geocode webservice.", e);
        } catch (JSONException e) {
            Log.e("grandroid", "Error parsing Google geocode webservice response.", e);
        }
        return retList;
    }

    public static double[] getLocationFromString(String address) throws JSONException {
        try {
            HttpGet httpGet = new HttpGet(
                    "http://maps.google.com/maps/api/geocode/json?address="
                    + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject = new JSONObject(stringBuilder.toString());

            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            return new double[]{lat, lng};
        } catch (UnsupportedEncodingException ex) {
            Log.e("grandroid", null, ex);
            return new double[]{0, 0};
        }
    }
}
