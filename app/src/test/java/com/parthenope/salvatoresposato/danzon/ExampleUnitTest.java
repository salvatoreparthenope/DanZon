package com.parthenope.salvatoresposato.danzon;

import com.google.gson.Gson;
import com.parthenope.salvatoresposato.danzon.WebService.object.AreaJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.GpsCoordinateJson;
import com.parthenope.salvatoresposato.danzon.WebService.object.IntervalJson;

import org.junit.Test;

import java.util.LinkedList;
import java.util.logging.Level;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test_json() {

        AreaJson areaJson = new AreaJson();
        areaJson.gpsCoordinate =  new LinkedList<GpsCoordinateJson>();
        areaJson.intervals =  new LinkedList<IntervalJson>();

        GpsCoordinateJson gps = new GpsCoordinateJson();
        gps.latitude = 30;
        gps.longitude = 40;
        areaJson.gpsCoordinate.add(gps);
        areaJson.gpsCoordinate.add(gps);

        IntervalJson interval = new IntervalJson();
        interval.dangerousLevel = 3;
        interval.description = "Prova";
        interval.end="10/10/2093";
        interval.start = "10/11/2099";
        interval.type = 0;
        areaJson.intervals.add(interval);
        areaJson.intervals.add(interval);
        areaJson.intervals.add(interval);

        String s = new Gson().toJson(areaJson);

    }
}