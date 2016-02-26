/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import grandroid.geo.GPSUtil;
import grandroid.view.Face;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public class GeoFace extends Face {

    protected MapView map;
    protected MapManager manager;

    public MapManager getManager() {
        return manager;
    }

    protected MapView createMap() {
        return createMap(getDefaultPosition());
    }

    protected MapView createMap(LatLng pos) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pos)
                .zoom(getDefaultZoom())
                .build();
        //MapsInitializer.initialize(getActivity());
        map = new MapView(this, new GoogleMapOptions().camera(cameraPosition));
        map.onCreate(savedInstanceState);
        map.getMap().setMyLocationEnabled(showMyPosition());        
        manager = new MapManager(this, map);
        return map;
    }

    protected void insertMap(LayoutMaker maker, ViewGroup.LayoutParams layoutParams) throws Exception {
        insertMap(maker, layoutParams, getDefaultPosition());
    }

    protected void insertMap(LayoutMaker maker, ViewGroup.LayoutParams layoutParams, LatLng defaultPosition) throws Exception {
        try {
            MapsInitializer.initialize(this);
            if (map == null) {
                map = createMap(defaultPosition);
            }
            maker.add(map, layoutParams);
        } catch (Exception ex) {
            Toast.makeText(this, "請開啟Google Play更新Google Play Service及Google Map，以使用地圖功能", Toast.LENGTH_LONG).show();
            throw ex;
        }
        //try {
        //map.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(getDefaultPosition(), getDefaultZoom()));
        //} catch (GooglePlayServicesNotAvailableException ex) {
        //    Log.e("grandroid", null, ex);
        //}
    }

    protected LatLng getDefaultPosition() {
        Location loc = GPSUtil.getLastPosition(this);
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

    public MapView getMap() {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
            return map;
        } else {
            return manager.getMapView();
        }
    }

    protected LatLng getTaipei() {
        return new LatLng(25.000000, 121.500000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            manager.beforeActivitySaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            manager.beforeActivityLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    protected void onPause() {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            manager.beforeActivityPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            manager.beforeActivityResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (manager == null) {
            Log.e("grandroid", "no mapview, you must call insertMap() in onCraete.");
        } else {
            manager.beforeActivityDestroy();
        }
        super.onDestroy();
    }
}
