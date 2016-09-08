package org.mwg.ml.common.distance;

/**
 * Created by assaad on 06/09/16.
 */
public class GeoDistance implements Distance {

    //x=[lat,long]
    //y=[lat,long]

    @Override
    public double measure(double[] x, double[] y) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(y[0] - x[0]);
        double dLng = Math.toRadians(y[1] - x[1]);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(x[0])) * Math.cos(Math.toRadians(y[0])) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;

    }

    @Override
    public boolean compare(double x, double y) {
        return x < y;
    }


    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return Double.MAX_VALUE;
    }
}