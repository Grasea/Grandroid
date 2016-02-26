/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import grandroid.action.AsyncAction;
import grandroid.database.FaceData;
import grandroid.geo.GPSUtil;
import grandroid.geo.LocationResult;

/**
 *
 * @author Rovers
 */
public final class MapManager {

    public String defaultLayerName = "default";
    protected MapView mapView;
    protected Location pos;
    protected GeoLocator locator;
    protected FaceData fd;
    protected LayerManager lm;
    protected Layer currentLayer;
    protected Marker me;
    protected int resMe;
    protected Bitmap bmpMe;
    protected CopyOnWriteArrayList<Polyline> routePaths;

    public enum MarkerAlign {

        CenterBottom(0.5f, 1f), CenterCenter(0.5f, 0.5f);
        protected float x;
        protected float y;

        MarkerAlign(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getXRatio() {
            return x;
        }

        public float getYRatio() {
            return y;
        }
    }

    public MapManager(Context context, MapView mapView) {
        this.mapView = mapView;
        pos = null;
        lm = new LayerManager(mapView);
        locator = new GeoLocator(context);
        changeLayer(defaultLayerName);
    }

    public MapManager(Activity activity, Bundle savedInstanceState) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getLastPosition())
                .zoom(16)
                .build();
        mapView = new MapView(activity, new GoogleMapOptions().camera(cameraPosition));
        mapView.onCreate(savedInstanceState);
        pos = null;
        lm = new LayerManager(mapView);
        locator = new GeoLocator(activity);
        changeLayer(defaultLayerName);

        //try {
        MapsInitializer.initialize(activity);
        //map.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(getDefaultPosition(), getDefaultZoom()));
        //} catch (GooglePlayServicesNotAvailableException ex) {
        //    Log.e("grandroid", null, ex);
        //}
    }

    public FaceData getFd() {
        return fd;
    }

    public void setFd(FaceData fd) {
        this.fd = fd;
    }

    public MapView getMapView() {
        return mapView;
    }

    public GeoLocator getLocator() {
        return locator;
    }

    public LatLng getCenter() {
        return mapView.getMap().getCameraPosition().target;
    }

    public void setCenter(LatLng pos) {
        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    public float getZoom() {
        return mapView.getMap().getCameraPosition().zoom;
    }

    public void setZoom(float zoom) {
        mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    public void setMarkerListener(GoogleMap.OnMarkerClickListener markerClickListener) {
        mapView.getMap().setOnMarkerClickListener(markerClickListener);
    }

    public Layer changeLayer(String name) {
        currentLayer = lm.getLayer(name);
        return currentLayer;
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public LatLng getLastPosition() {
        LatLng currPos = null;
        if (locator != null && locator.isConnected()) {
            Location loc = locator.getLastLocation();
            if (loc != null) {
                return new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        }

        pos = GPSUtil.getLastPosition(mapView.getContext());
        if (pos == null) {
            currPos = new LatLng(25.056024, 121.523002);
        } else {
            currPos = new LatLng(pos.getLatitude(), pos.getLongitude());
        }
        return currPos;
    }

    public void animateToMe(float zoom) {
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(getLastPosition(), zoom));
    }

    public void animateToMe() {
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(getLastPosition(), 17));
    }

    public void animateTo(double lat, double lon) {
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
    }

    public void animateTo(LatLng ll) {
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLng(ll));
    }

    public void animateTo(double lat, double lon, float zoom) {
        mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom));
    }

    public void animateToDataCenter(String tableName, String where) {
        if (fd == null) {
            fd = new FaceData(mapView.getContext(), "app");
        }
        Cursor cursor = fd.query("select avg(lat),avg(lon) from " + tableName + " " + where);
        if (cursor.moveToNext()) {
            animateTo(cursor.getDouble(0), cursor.getDouble(1));
        }
        cursor.close();
    }

    public void animateToLayer(String name) {
        Layer layer = lm.getLayer(name);
        if (layer != null) {
            animateToLayer(layer);
        }
    }

    public void animateToLayer(Layer layer) {
        animateTo(layer.getCenter());
    }

    public void animateToMarker(Marker marker) {
        animateTo(marker.getPosition());
    }

    public int getResMe() {
        return resMe;
    }

    public void setResMe(int resMe) {
        this.resMe = resMe;
    }

    public Bitmap getBmpMe() {
        return bmpMe;
    }

    public void setBmpMe(Bitmap bmpMe) {
        this.bmpMe = bmpMe;
    }

    public MarkerOptions createUserMarker(LatLng ll) {
        MarkerOptions mo = new MarkerOptions();
        if (resMe > 0) {
            mo.icon(BitmapDescriptorFactory.fromResource(resMe));
        } else if (bmpMe != null) {
            mo.icon(BitmapDescriptorFactory.fromBitmap(bmpMe));
        }
        locator.addLocationCallback(new LocationResult() {
            @Override
            public boolean gotLocation(Location location) {
                if (me != null) {
                    me.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                } else {
                    //me = addUserMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                return true;
            }
        });

        return mo.position(ll);
    }

    public Marker getMe() {
        return me;
    }

    public Marker addUserMarker(LatLng ll) {
        me = mapView.getMap().addMarker(createUserMarker(ll));
        return me;
    }

    public Marker addUserMarker(int resUser) {
        MarkerOptions mo = new MarkerOptions();
        me = mapView.getMap().addMarker(mo.position(getLastPosition()));
        return me;
    }

    public Marker addMarker(int resIcon, String title, LatLng pos) {
        return addMarker(resIcon, title, "", pos);
    }

    public Marker addMarker(int resIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos)
                .icon(BitmapDescriptorFactory.fromResource(resIcon)).anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        Marker m = mapView.getMap().addMarker(mo);
        currentLayer.addMarker(m);
        return m;
    }

    public Marker addMarker(Bitmap bmpIcon, String title, LatLng pos) {
        return addMarker(bmpIcon, title, "", pos);
    }

    public Marker addMarker(Bitmap bmpIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos)
                .icon(BitmapDescriptorFactory.fromBitmap(bmpIcon)).anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        Marker m = mapView.getMap().addMarker(mo);
        currentLayer.addMarker(m);
        return m;
    }

    public Marker addMarker(String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos).anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        Marker m = mapView.getMap().addMarker(mo);
        currentLayer.addMarker(m);
        return m;
    }

    public void clearCurrentLayer() {
        currentLayer.removeAll();
    }
//
//    public void requestLocating(LocationResult result) {
//        locator.requestLocating(result);
//    }

    public static LatLng point(double lat, double lon) {
        return new LatLng(lat, lon);
    }

    public void setBubbleHandler(final BubbleHandler handler) {
        mapView.getMap().setInfoWindowAdapter(handler);
        mapView.getMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                animateToMarker(marker);
                handler.onClickBubble(marker);
            }
        });
    }

    public void beforeActivitySaveInstanceState(Bundle outState) {
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    public void beforeActivityLowMemory() {
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    public void beforeActivityPause() {
        if (mapView != null) {
            mapView.onPause();
            locator.stop();
        }
    }

    public void beforeActivityResume() {
        if (mapView != null) {
            mapView.onResume();
            locator.start();
        }
    }

    public void beforeActivityDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    public void route(final LatLng start, final LatLng destination, final String mode, final int color, final int lineWidth) {
        final MapRoute mr = new MapRoute();
        new AsyncAction<Document>(this.mapView.getContext()) {

            @Override
            public boolean execute(Context context) {
                Document doc = mr.getDocument(start, destination, defaultLayerName);
                setResult(doc);
                return true;
            }

            @Override
            public void afterExecution(Document doc) {
                ArrayList<LatLng> directionPoint = mr.getDirection(doc);
                PolylineOptions rectLine = new PolylineOptions().width(lineWidth).color(color);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                if (routePaths == null) {
                    routePaths = new CopyOnWriteArrayList<Polyline>();
                }
                routePaths.add(mapView.getMap().addPolyline(rectLine));
            }
        }.message("正在規劃路徑").execute();
    }

    public void clearRouteResult() {
        if (routePaths != null) {
            for (Polyline pl : routePaths) {
                pl.remove();
            }
            routePaths.clear();
        }
    }
}
