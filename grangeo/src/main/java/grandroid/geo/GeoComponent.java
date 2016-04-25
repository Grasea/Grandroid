package grandroid.geo;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import grandroid.database.FaceData;
import grandroid.view.LayoutMaker;
import grandroid.view.fragment.Component;

/**
 * Created by Alan Ding on 2016/4/19.
 */
public abstract class GeoComponent extends Component implements OnMapReadyCallback {
    protected MapView mapView;
    protected GoogleMap map;
    protected Location pos;
    protected GeoLocator locator;

    public abstract void onMyLocationChanged(Location location);

    @Override
    public void onCreateView(LayoutMaker maker, Bundle savedInstanceState) {
        super.onCreateView(maker, savedInstanceState);
    }


    protected MapView createMap() {
        MapsInitializer.initialize(getFace());
        locator = new GeoLocator(getFace());
        LocationResult locationResult = new LocationResult() {
            @Override
            public boolean gotLocation(Location location) {
                onMyLocationChanged(location);
                return true;
            }
        };
        locationResult.follow();
        locator.addLocatingJob(locationResult);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(getLastPosition())
                .zoom(16)
                .build();
        mapView = new MapView(getFace(), new GoogleMapOptions().camera(cameraPosition));
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                GeoComponent.this.onMapReady(googleMap);
                locator.start();
            }
        });
        pos = null;
        return mapView;
    }

    protected void insertMap(LayoutMaker maker, ViewGroup.LayoutParams layoutParams) throws Exception {
        insertMap(maker, layoutParams, getDefaultPosition());
    }

    protected void insertMap(LayoutMaker maker, ViewGroup.LayoutParams layoutParams, LatLng defaultPosition) throws Exception {
        try {
            if (mapView == null) {
                mapView = createMap();
            }
            maker.add(mapView, layoutParams);
        } catch (Exception ex) {
            Toast.makeText(getFace(), "請開啟Google Play更新Google Play Service及Google Map，以使用地圖功能", Toast.LENGTH_LONG).show();
            throw ex;
        }
    }

    public LatLng getCenter() {
        return map.getCameraPosition().target;
    }

    public void setCenter(LatLng pos) {
        map.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    protected LatLng getDefaultPosition() {
        Location loc = getLocator().getLastLocation();
        if (loc == null) {
            return getTaipei();
        }
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }

    protected boolean showMyPosition() {
        return true;
    }

    protected float getDefaultZoom() {
        return 17;
    }

    @Nullable
    public MapView getMapView() {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        }
        return mapView;
    }

    @Nullable
    public GoogleMap getMap() {
        return map;
    }

    protected LatLng getTaipei() {
        return new LatLng(25.000000, 121.500000);
    }

    public GeoLocator getLocator() {
        return locator;
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


    public Marker addMarker(@DrawableRes int resIcon, String title, LatLng pos) {
        return addMarker(resIcon, title, "", pos);
    }

    public Marker addMarker(@DrawableRes int resIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos);
        if (resIcon != 0) {
            mo.icon(BitmapDescriptorFactory.fromResource(resIcon));
        }
        Marker m = map.addMarker(mo);

        return m;
    }

    public Marker addMarker(Bitmap bmpIcon, String title, LatLng pos) {
        return addMarker(bmpIcon, title, "", pos);
    }

    public Marker addMarker(Bitmap bmpIcon, String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos);
        if (bmpIcon != null) {
            mo.icon(BitmapDescriptorFactory.fromBitmap(bmpIcon));
        }
        Marker m = map.addMarker(mo);
        return m;
    }

    public Marker addMarker(String title, String snippet, LatLng pos) {
        MarkerOptions mo = new MarkerOptions().title(title).snippet(snippet).position(pos);
        Marker m = map.addMarker(mo);
        return m;
    }

    public static LatLng point(double lat, double lon) {
        return new LatLng(lat, lon);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            mapView.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            mapView.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onPause() {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mapView == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

}
