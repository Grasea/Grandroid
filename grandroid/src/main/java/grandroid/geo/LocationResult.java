/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.geo;

import android.location.Location;

/**
 * @author Rovers
 */
public abstract class LocationResult {
    protected int maxCount = 1;
    protected int executeCount = 0;

    public void reset() {
        maxCount = 1;
        executeCount = 0;
    }

    public boolean isExceedCount() {
        return executeCount >= maxCount;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public LocationResult once() {
        this.maxCount = 1;
        return this;
    }

    /**
     * 如要定時追蹤，需先呼叫此方法
     *
     * @return
     */
    public LocationResult follow() {
        this.maxCount = Integer.MAX_VALUE;
        return this;
    }

    public void used() {
        executeCount++;
    }

    public abstract boolean gotLocation(Location location);

    public void onNoDeviceSupport() {
    }

}
