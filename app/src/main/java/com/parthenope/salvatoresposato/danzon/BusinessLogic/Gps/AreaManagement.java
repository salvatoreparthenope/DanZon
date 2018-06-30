package com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps;

import android.location.Location;

import com.parthenope.salvatoresposato.danzon.Database.Area;
import com.parthenope.salvatoresposato.danzon.Database.Interval;

import java.util.LinkedList;
import java.util.List;

public class AreaManagement {

    List<AreaStrategy> areasStrategy = new LinkedList<>();

    public AreaManagement(){
        List<Area> listArea = Area.listAll(Area.class);
        for (Area area:listArea) {
            areasStrategy.add(new AreaStrategy(area,new ClassicStrategy()));
        }
    }

    /**
     * Check if point is inside some area
     * @param location
     * @return
     */
    public AreaStrategy getArea(Location location){
        for (AreaStrategy areaStrategy:areasStrategy) {
            if(areaStrategy.isItInside(location)){
                return areaStrategy;
            }
        }
        return null;
    }

}
