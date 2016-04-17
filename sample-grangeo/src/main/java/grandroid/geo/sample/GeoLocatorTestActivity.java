package grandroid.geo.sample;

import android.location.Location;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import grandroid.geo.GeoLocator;
import grandroid.geo.LocationResult;

public class GeoLocatorTestActivity extends AppCompatActivity {
    public TextView tvLog;
    StringBuilder sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locator_test);
        tvLog = (TextView) findViewById(R.id.tv_log);
        sb = new StringBuilder("定位紀錄:\n");
        tvLog.setText(sb.toString());
        GeoLocator geoLocator = new GeoLocator(this);
        LocationResult locationResult = new LocationResult() {
            @Override
            public boolean gotLocation(Location location) {
                sb.append("次數:" + this.getExecuteCount() + " , " + location.getLatitude() + "/" + location.getLongitude() + "\n");
//                Toast.makeText(GeoLocatorTestActivity.this, "locateCount:" + locateCount, Toast.LENGTH_SHORT).show();
                tvLog.setText(sb.toString());
                return true;
            }
        };
        //持續追蹤
        locationResult.follow();
        geoLocator.addLocatingJob(locationResult);
        geoLocator.start();
    }
}
