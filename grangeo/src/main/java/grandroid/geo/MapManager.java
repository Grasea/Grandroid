/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
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

/**
 * @author Rovers
 */
public final class MapManager {

    public String defaultLayerName = "default";
    protected MapView mapView;
    protected GoogleMap map;
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

    public MapManager(Context context, MapView mapView, final OnMapReadyCallback onMapReadyCallback) {
        MapsInitializer.initialize(context);

        this.mapView = mapView;
        pos = null;
        lm = new LayerManager(mapView);
        locator = new GeoLocator(context);
        changeLayer(defaultLayerName);
        this.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (onMapReadyCallback != null) {
                    onMapReadyCallback.onMapReady(googleMap);
                }
            }
        });
    }

    public MapManager(Activity activity, Bundle savedInstanceState, final OnMapReadyCallback onMapReadyCallback) {
        MapsInitializer.initialize(activity);
        locator = new GeoLocator(activity);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getLastPosition())
                .zoom(16)
                .build();
        mapView = new MapView(activity, new GoogleMapOptions().camera(cameraPosition));
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                if (onMapReadyCallback != null) {
                    onMapReadyCallback.onMapReady(googleMap);
                }
            }
        });
        pos = null;
        lm = new LayerManager(mapView);
        changeLayer(defaultLayerName);

        //try {
        //mapView.getMapView().animateCamera(CameraUpdateFactory.newLatLngZoom(getDefaultPosition(), getDefaultZoom()));
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

    public GoogleMap getMap() {
        return map;
    }

    public GeoLocator getLocator() {
        return locator;
    }

    public LatLng getCenter() {
        return map.getCameraPosition().target;
    }

    public void setCenter(LatLng pos) {
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    public float getZoom() {
        return map.getCameraPosition().zoom;
    }

    public void setZoom(float zoom) {
        map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    public void setMarkerListener(GoogleMap.OnMarkerClickListener markerClickListener) {
        map.setOnMarkerClickListener(markerClickListener);
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

        pos = locator.getLastLocation();
        if (pos == null) {
            currPos = new LatLng(25.056024, 121.523002);
        } else {
            currPos = new LatLng(pos.getLatitude(), pos.getLongitude());
        }
        return currPos;
    }

    public void animateToMe(float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastPosition(), zoom));
    }

    public void animateToMe() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastPosition(), 17));
    }

    public void animateTo(double lat, double lon) {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
    }

    public void animateTo(LatLng ll) {
        map.animateCamera(CameraUpdateFactory.newLatLng(ll));
    }

    public void animateTo(double lat, double lon, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), zoom));
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

    public void setResMe(@DrawableRes int resMe) {
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
        me = map.addMarker(createUserMarker(ll));
        return me;
    }

    public Marker addUserMarker(int resUser) {
        MarkerOptions mo = new MarkerOptions();
        me = map.addMarker(mo.position(getLastPosition()));
        return me;
    }

    public Marker addMarker(@DrawableRes int resIcon, String title, LatLng pos) {
        return addMarker(resIcon, title, "", pos);
    }

    public Marker addMarker(@DrawableRes int resIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos)
                .anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        if (resIcon != 0) {
            mo.icon(BitmapDescriptorFactory.fromResource(resIcon));
        }
        Marker m = map.addMarker(mo);
        currentLayer.addMarker(m);
        return m;
    }

    public Marker addMarker(Bitmap bmpIcon, String title, LatLng pos) {
        return addMarker(bmpIcon, title, "", pos);
    }

    public Marker addMarker(Bitmap bmpIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos)
                .anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        if (bmpIcon != null) {
            mo.icon(BitmapDescriptorFactory.fromBitmap(bmpIcon));
        }
        Marker m = map.addMarker(mo);
        currentLayer.addMarker(m);
        return m;
    }

    public Marker addMarker(String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos).anchor(currentLayer.align.getXRatio(), currentLayer.align.getYRatio());
        Marker m = map.addMarker(mo);
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
        map.setInfoWindowAdapter(handler);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                animateToMarker(marker);
                handler.onClickBubble(marker);
            }
        });
    }

    public UiSettings getMapUiSetting() {
        return map.getUiSettings();
    }

    public void enableToolbar() {
        map.getUiSettings().setMapToolbarEnabled(true);
    }

    public void enableMyLocationButton() {
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
    }

    public void enableZoomControls() {
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    public boolean isMapPrepared() {
        return map != null;
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
                routePaths.add(map.addPolyline(rectLine));
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
