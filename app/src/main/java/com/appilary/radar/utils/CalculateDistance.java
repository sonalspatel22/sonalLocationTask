package com.appilary.radar.utils;

import android.text.TextUtils;

/**
 * Created by vi.garg on 24/5/17.
 */

public class CalculateDistance {

    private static CalculateDistance mInstance;

    public static CalculateDistance getInstance() {
        if (mInstance == null)
            mInstance = new CalculateDistance();
        return mInstance;
    }

    public boolean isDistanceUnderValue(Double lat1, Double lon1, Double lat2, Double lon2, Integer disInMeter) {

        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null)
            return true;

        if (lat1 == 0 || lon1 == 0 || lat2 == 0 || lon2 == 0)
            return true;

        char unit = 'K';
//        if (!TextUtils.isEmpty(kmMile))
//            unit = kmMile.contains("Km") ? 'K' : 'M';

        if (disInMeter == null || disInMeter == 0)
            disInMeter = 800;

        double selectedDes = disInMeter/1000;

        if (selectedDes == 0)
            selectedDes = 10;


        double value = distance(lat1, lon1, lat2, lon2, unit);
        /* To provide extra distance check*/
//        if('K' == unit)  // Todo change to exact loc
//            selectedDes += 8;
//        else
//            selectedDes += 5;

       return value <= selectedDes;
//        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'M') + " Miles\n");
//        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'K') + " Kilometers\n");
//        System.out.println(distance(32.9697, -96.80322, 29.46786, -98.53506, 'N') + " Nautical Miles\n");
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2, String kmMile) {
        char unit = 'M';
        if (!TextUtils.isEmpty(kmMile))
            unit = kmMile.contains("Km") ? 'K' : 'M';

        return distance(lat1, lon1, lat2, lon2, unit);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
