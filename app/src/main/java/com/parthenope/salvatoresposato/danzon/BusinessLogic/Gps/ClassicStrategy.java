package com.parthenope.salvatoresposato.danzon.BusinessLogic.Gps;

import android.location.Location;

public class ClassicStrategy implements CheckStrategy {

    public static double TWOPI = 2*Math.PI;

    @Override
    public boolean IsItInArea(AreaStrategy area, Location location) {

        int i;
        int n = area.listCoordinates.size();

        double angle=0;
        double point1_lat;
        double point1_long;
        double point2_lat;
        double point2_long;

        for (i=0;i<area.listCoordinates.size();i++) {
            point1_lat = area.listCoordinates.get(i).latitude - location.getLatitude();
            point1_long = area.listCoordinates.get(i).longitude - location.getLongitude();
            point2_lat = area.listCoordinates.get((i+1)%n).latitude - location.getLatitude();
            point2_long = area.listCoordinates.get((i+1)%n).longitude - location.getLongitude();
            angle += Angle2D(point1_lat,point1_long,point2_lat,point2_long);
        }

        if (Math.abs(angle) < Math.PI)
            return false;
        else
            return true;
    }

    public static double Angle2D(double y1, double x1, double y2, double x2)
    {
        double dtheta,theta1,theta2;

        theta1 = Math.atan2(y1,x1);
        theta2 = Math.atan2(y2,x2);
        dtheta = theta2 - theta1;
        while (dtheta > Math.PI)
            dtheta -= TWOPI;
        while (dtheta < -Math.PI)
            dtheta += TWOPI;

        return(dtheta);
    }
}
