package grandroid.geo.sample;

import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import grandroid.geo.GeoComponent;
import grandroid.view.LayoutMaker;

/**
 * Created by Alan Ding on 2016/4/25.
 */
public class ComponentMap extends GeoComponent {
    protected boolean isMoved = false;

    @Override
    public void onMyLocationChanged(Location location) {
        Log.i("grandroid", "onMyLocationChanged.");
        if (!isMoved) {
            isMoved = true;
            animateTo(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onCreateView(LayoutMaker maker, Bundle savedInstanceState) {
        super.onCreateView(maker, savedInstanceState);
        maker.add(createMap(), maker.layFF());
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("grandroid", "On Map Ready.");
        setCenter(getDefaultPosition());
        enableMyLocationButton();
        enableToolbar();
        enableZoomControls();
        addMarker("Title", "My Location", getTaipei());
    }
}
