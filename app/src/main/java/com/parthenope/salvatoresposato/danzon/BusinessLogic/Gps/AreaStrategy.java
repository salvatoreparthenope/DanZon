package com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps;

import android.location.Location;

import com.parthenope.salvatoresposato.danzon.Database.Area;
import com.parthenope.salvatoresposato.danzon.Database.Interval;
import com.parthenope.salvatoresposato.danzon.Database.GpsCoordinate;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AreaStrategy {

    public List<GpsCoordinate> listCoordinates;
    public CheckStrategy checkStrategy;
    public Area area;

    public AreaStrategy(Area area,CheckStrategy strategy){
        this.area = area;
        listCoordinates = GpsCoordinate.find(GpsCoordinate.class,"id_area = ?",String.valueOf(area.getId()));
        checkStrategy = strategy;
    }

    /**
     *
     * @return
     */
    public Interval getActualInterval(){

        String timeStampDate = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        String timeStampTime = timeStampDate.substring(8);

        Interval actualInterval = null;
        List<Interval> intervals = area.getIntervals();

        for (Interval interval:intervals) {

            switch (interval.type){
                case GlobalConstant.TYPE_INTERVAL_TIME:

                    if( interval.start.compareTo(timeStampTime) <= 0 &&
                            interval.end.compareTo(timeStampTime) > 0 ){
                        actualInterval = actualInterval == null ? interval : actualInterval.dangerousLevel < interval.dangerousLevel ? interval : actualInterval;
                    }

                    break;
                case GlobalConstant.TYPE_INTERVAL_DATE:

                    if( interval.start.compareTo(timeStampDate) < 0 &&
                            interval.end.compareTo(timeStampDate) > 0 ){
                        actualInterval = actualInterval == null ? interval : actualInterval.dangerousLevel < interval.dangerousLevel ? interval : actualInterval;
                    }

                    break;
            }

        }

        return actualInterval;

    }

    /**
     * Change algorithm
     * @param strategy
     */
    public void setStrategy(CheckStrategy strategy){
        if( strategy != null ){
            checkStrategy = strategy;
        }
    }

    /**
     * Check if point is inside the area
     * @param location
     * @return
     */
    public boolean isItInside(Location location){
        if(location != null){
            return checkStrategy.IsItInArea(this,location);
        }
        return false;
    }
}
