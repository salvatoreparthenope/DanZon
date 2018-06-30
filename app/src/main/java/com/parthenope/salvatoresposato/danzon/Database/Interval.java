package com.parthenope.salvatoresposato.danzon.Database;

import com.orm.SugarRecord;
import com.parthenope.salvatoresposato.danzon.WebService.object.GpsCoordinateJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.IntervalJson;

import java.io.Serializable;

public class Interval extends SugarRecord implements Serializable {

    public long idArea;
    public short dangerousLevel;
    public short type;              // Type of interval - Time interval, Day interval, Month interval
    public String start;
    public String end;
    public String description;

    /**
     *
     * @param json
     * @return
     */
    public static long InsertFromJson(IntervalJson json){

        Interval interval = new Interval();
        interval.idArea = json.idArea;
        interval.dangerousLevel = json.dangerousLevel;
        interval.type = json.type;
        interval.start = json.start;
        interval.end = json.end;
        interval.description = json.description;
        interval.save();

        return interval.getId();

    }

}
