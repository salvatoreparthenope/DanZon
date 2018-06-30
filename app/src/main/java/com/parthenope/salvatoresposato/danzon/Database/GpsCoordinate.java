package com.parthenope.salvatoresposato.danzon.Database;

import android.location.Location;

import com.orm.SugarRecord;
import com.parthenope.salvatoresposato.danzon.WebService.object.AreaJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.GpsCoordinateJson;

import java.io.Serializable;

public class GpsCoordinate extends SugarRecord implements Serializable {

    public long idArea;
    public double latitude;
    public double longitude;

    /**
     *
     * @param json
     * @return
     */
    public static long InsertFromJson(GpsCoordinateJson json){

        GpsCoordinate gpsCoordinate = new GpsCoordinate();
        gpsCoordinate.idArea = json.idArea;
        gpsCoordinate.latitude = json.latitude;
        gpsCoordinate.longitude = json.longitude;
        gpsCoordinate.save();

        return gpsCoordinate.getId();

    }

}
