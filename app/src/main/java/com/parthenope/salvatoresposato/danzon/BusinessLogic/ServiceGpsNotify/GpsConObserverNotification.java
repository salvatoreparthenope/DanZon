package com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify;

import android.app.Notification;
import android.content.Context;
import android.location.Location;

import com.parthenope.salvatoresposato.danzon.Database.Interval;
import com.parthenope.salvatoresposato.danzon.Database.Variable;
import com.parthenope.salvatoresposato.danzon.R;
import com.parthenope.salvatoresposato.danzon.Shared.GlobalConstant;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class GpsConObserverNotification implements GpsObserver {

    GpsSubject gpsSubject = null;
    Context context = null;

    public GpsConObserverNotification(GpsSubject gpsSubject, Context context){
        this.gpsSubject = gpsSubject;
        this.context = context;
        this.gpsSubject.attach(this);
    }

    @Override
    public void update() {

        Object[] state = gpsSubject.getState();

        if(state == null)
            return;

        Location location = (Location) state[0];
        Interval interval = (Interval) state[2];

        if(location == null)
            return;

        // Check if interval is been already fired
        String idFiredInterval = Variable.getValue(GlobalConstant.KEY_ID_FIRED_INTERVAL);
        if (interval != null && (idFiredInterval == null || Long.valueOf(idFiredInterval) != interval.getId())) {

            Variable.updateOrAddVariabile(GlobalConstant.KEY_ID_FIRED_INTERVAL, String.valueOf(interval.getId()));

            // if dangerousity level is more high of low level...
            if ( interval.dangerousLevel > GlobalConstant.DANGEROUSITY_LEVEL_MIDDLE)
                PugNotification.with(context)
                        .load()
                        .title(R.string.NOTIF_TITLE)
                        .message(R.string.NOTIF_MESS)
                        .smallIcon(R.drawable.pugnotification_ic_launcher)
                        .largeIcon(R.drawable.pugnotification_ic_launcher)
                        .flags(Notification.DEFAULT_ALL)
                        .simple()
                        .build();

        }
    }
}
