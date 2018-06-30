package com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps;

import android.location.Location;

public interface CheckStrategy {
    boolean IsItInArea(AreaStrategy area, Location coordinate);
}
