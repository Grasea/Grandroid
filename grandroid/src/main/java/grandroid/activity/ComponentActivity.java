/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.activity;

import android.os.Bundle;
import grandroid.view.Face;
import grandroid.view.LayoutMaker;

/**
 *
 * @author Rovers
 */
public class ComponentActivity extends Face {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutMaker maker = new LayoutMaker(this);
        maker.addComponent(createPassedComponent());
    }
}
