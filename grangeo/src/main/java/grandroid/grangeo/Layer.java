/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.grangeo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import grandroid.geo.MapManager.MarkerAlign;

/**
 *
 * @author Rovers
 */
public class Layer {

    protected String name;
    protected ArrayList<Marker> markers;
    protected boolean visible;
    protected MarkerAlign align;

    public Layer(String name) {
        visible = true;
        this.name = name;
        markers = new ArrayList<Marker>();
        align = MarkerAlign.CenterBottom;
    }

    public void addMarker(Marker m) {
        m.setVisible(visible);
        markers.add(m);
    }

    public Layer setAlign(MarkerAlign align) {
        this.align = align;
        return this;
    }

    public MarkerAlign getAlign() {
        return this.align;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
        for (Marker m : markers) {
            m.setVisible(visible);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return markers.size();
    }

    public LatLng getCenter() {
        double lat = 0;
        double lng = 0;
        if (markers.isEmpty()) {
            return null;
        } else {
            for (Marker m : markers) {
                lat += m.getPosition().latitude;
                lng += m.getPosition().longitude;
            }
            lat = lat / markers.size();
            lng = lng / markers.size();
            return new LatLng(lat, lng);
        }
    }

    public void removeAll() {
        for (Marker m : markers) {
            m.remove();
        }
        markers.clear();
    }
}
