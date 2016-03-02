/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Rovers
 */
public class LayerManager {

    protected HashMap<String, Layer> layerMap;
    protected ArrayList<Layer> layers;
    protected MapView mapView;

    public LayerManager(MapView mapView) {
        this.mapView = mapView;
        layerMap = new HashMap<String, Layer>();
        layers=new ArrayList<Layer>();
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public Layer getLayer(String name) {
        Layer layer = layerMap.get(name);
        if (layer == null) {
            layer = new Layer(name);
            layerMap.put(name, layer);
            layers.add(layer);
        }
        return layer;
    }

    public void removeLayer(String name) {
        Layer layer = getLayer(name);
        layer.removeAll();
        layerMap.remove(name);
        layers.remove(layer);
    }

    public void removeAllLayer() {
        for (String key : layerMap.keySet()) {
            removeLayer(key);
        }
    }
}
