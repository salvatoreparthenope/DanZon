package com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.gson.Gson;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps.AreaManagement;
import com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps.AreaStrategy;
import com.parthenope.salvatoresposato.danzon.Database.Interval;
import com.parthenope.salvatoresposato.danzon.Database.GpsCoordinate;
import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant.KEY_INTENT_LOCATION;
import static com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant.NOT_FOUND;

public class GpsSubject extends BroadcastReceiver {

    private List<GpsObserver> observers = new ArrayList<>();

    public Object[] getState() {

        String lastLocation = Variable.getValue(GlobalConstant.KEY_LAST_LOCATION);
        String lastTimeUpdateLocation = Variable.getValue(GlobalConstant.KEY_LAST_TIME_UPDATE_LOCATION);
        String lastTimeDangerousityInterval = Variable.getValue(GlobalConstant.KEY_LAST_TIME_DANGEROUSITY_INTERVAL);
        String descriptionArea = Variable.getValue(GlobalConstant.DESCRIPTION_AREA);

        if(lastLocation == null || lastLocation.equals(NOT_FOUND)){
            return null;
        }

        Location location = new Location("");
        if(lastLocation != null){
            String[] latLong = lastLocation.split(",");
            location.setLatitude(Double.valueOf(latLong[0]));
            location.setLongitude(Double.valueOf(latLong[1]));
        }

        return new Object[]{location,new Date(Long.valueOf(lastTimeUpdateLocation)),new Gson().fromJson(lastTimeDangerousityInterval,Interval.class),descriptionArea};
    }

    public void setState(Location location) {

        AreaStrategy areaStrategy = new AreaManagement().getArea(location);
        Interval interval = areaStrategy != null ? areaStrategy.getActualInterval() : null;

        if(interval == null) {
            Variable.updateOrAddVariabile(GlobalConstant.KEY_LAST_LOCATION, NOT_FOUND);
            Variable.updateOrAddVariabile(GlobalConstant.KEY_LAST_TIME_DANGEROUSITY_INTERVAL, NOT_FOUND);
        }else {
            Variable.updateOrAddVariabile(GlobalConstant.KEY_LAST_LOCATION, String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
            Variable.updateOrAddVariabile(GlobalConstant.KEY_LAST_TIME_UPDATE_LOCATION, String.valueOf(System.currentTimeMillis()));
            Variable.updateOrAddVariabile(GlobalConstant.KEY_LAST_TIME_DANGEROUSITY_INTERVAL, new Gson().toJson(interval));
            Variable.updateOrAddVariabile(GlobalConstant.DESCRIPTION_AREA, areaStrategy.area.description);
        }

        notifyAllObservers();
    }

    public void attach(GpsObserver observer){
        observers.add(observer);
    }

    public void notifyAllObservers(){
        for (GpsObserver observer : observers) {
            observer.update();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String latLong = intent.hasExtra(KEY_INTENT_LOCATION) ? intent.getStringExtra(KEY_INTENT_LOCATION) : null;
        if(latLong == null)
            return;
        Location location = new Location("");
        location.setLatitude(Double.valueOf(latLong.split(",")[0]));
        location.setLongitude( Double.valueOf(latLong.split(",")[1]));
        setState(location);

    }
}