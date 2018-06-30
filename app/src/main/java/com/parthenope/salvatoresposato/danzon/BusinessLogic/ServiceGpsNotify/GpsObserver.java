package com.parthenope.salvatoresposato.danzon.BusinessLogic.ServiceGpsNotify;

import android.content.Context;

public interface GpsObserver {

    GpsSubject gpsSubject = null;
    Context context = null;

    public abstract void update();

}
