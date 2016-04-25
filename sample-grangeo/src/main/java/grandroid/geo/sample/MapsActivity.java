package grandroid.geo.sample;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import grandroid.geo.GeoFace;
import grandroid.view.LayoutMaker;

public class MapsActivity extends GeoFace implements OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutMaker maker=new LayoutMaker(this);
        try {
            insertMap(maker,maker.layFF());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Manipulates the mapView once available.
     * This callback is triggered when the mapView is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        LatLng taipei = new LatLng(24.12, 122.3);
        googleMap.addMarker(new MarkerOptions().position(taipei).title("Marker in Taipei"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(taipei));
    }
}
