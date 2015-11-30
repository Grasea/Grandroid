/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.alarm;

import grandroid.database.Identifiable;
import grandroid.database.Table;
import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
@Table("AlarmTask")
public class AlarmTask implements Identifiable {

    protected Integer _id;
    protected Long time;
    protected JSONObject json;
    protected Long interval;

    public AlarmTask() {
        interval = 0L;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Long getTime() {
        return time;
    }

    /**
     *
     * @param time scheduled time in milisecond
     */
    public void setTime(Long time) {
        this.time = time;
    }

    public Long getInterval() {
        return interval;
    }

    /**
     *
     * @param interval great than 0 imply this task is a repeat task, or set to 0 as a run-once task
     */
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

}
