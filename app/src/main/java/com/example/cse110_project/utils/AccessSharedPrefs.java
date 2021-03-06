package com.example.cse110_project.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AccessSharedPrefs {

    private static final String TAG = "xxACCESS SHARED PREFS: ";


    public AccessSharedPrefs() {}

    private static SharedPreferences setUp(Context context) {
        return context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
    }

    public static void setUserInfo(Context context, String fName, String lName, int feet, int inch) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("firstname", fName);
        editor.putString("lastname", lName);
        editor.putInt("heightFt", feet);
        editor.putInt("heightInch", inch);
        editor.putBoolean("STORED", false);

        editor.apply();
    }

    public static String getFirstName(Context context) {
        return setUp(context).getString("firstname", "");

    }
    public static String getLastName(Context context) {
        return setUp(context).getString("firstname", "");

    }

    public static int getHtFeet(Context context) {
        return setUp(context).getInt("heightFt", -1);

    }

    public static int getHtInch(Context context) {
        return setUp(context).getInt("heightInch", -1);

    }

    public static void setWalkStatus(Context context, boolean status) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("walkStatus", status);
        editor.apply();
    }

    public static void setWalkStartTime(Context context, long startTIme) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();
        Log.d(TAG, "saving " + startTIme);
        editor.putLong("walkStartTime", startTIme);
        editor.apply();
    }

    public static long getWalkStartTime(Context context) {
        SharedPreferences prefs = setUp(context);
        Log.d(TAG, "CHECKING");
        if(!prefs.contains("walkStartTime")) return -1;
        Log.d(TAG, String.valueOf(prefs.getLong("walkStartTime", -1)));
        return prefs.getLong("walkStartTime", -1);
    }

    public static boolean getWalkStatus(Context context) {
        SharedPreferences prefs = setUp(context);
        if(!prefs.contains("walkStatus")) return false;
        return prefs.getBoolean("walkStatus", false);
    }

    public static void saveInitial(Context context, String initial) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("initial", initial);
        editor.apply();
    }

    public static String getInitial(Context context) {
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("initial"))
            return prefs.getString("initial", "");

        return "";
    }

    public static void saveColors(Context context, int red, int green, int blue){
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("red", red);
        editor.putInt("green", green);
        editor.putInt("blue", blue);

        editor.apply();
    }

    public static int[] getColors(Context context){
        int[] c = new int[3];
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("red")){
            c[0] = prefs.getInt("red", 123);
        }

        if(prefs.contains("green")){
            c[1] = prefs.getInt("green", 123);
        }

        if(prefs.contains("blue")){
            c[2] = prefs.getInt("blue", 123);
        }

        return c;
    }

    public static void saveWalk(Context context, String timer, String steps, String distance) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("distance", distance);
        editor.putString("timer", timer);
        editor.putString("steps", steps);

        editor.apply();
    }

    public static String getSavedSteps(Context context) {
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("steps"))
            return prefs.getString("steps", "");

        return "";
    }

    public static String getSavedDistance(Context context) {
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("distance"))
            return prefs.getString("distance", "");

        return "";
    }

    public static String getSavedTimer(Context context) {
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("timer"))
            return prefs.getString("timer", "");

        return "";
    }

    public static void clearSharedPrefs(Context context) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor edit= prefs.edit();
        edit.clear();
        edit.apply();
    }

    public static void saveDailyStatsTester(Context context, int dailySteps, int dailyDistance) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("dailyDistanceTester", dailyDistance);
        editor.putInt("dailyStepsTester", dailySteps);
        editor.apply();
    }

    public static void saveUserID(Context context, String newUserID) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("UserID", newUserID);
        edit.apply();
    }

    public static String getUserID(Context context){
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("UserID"))
            return prefs.getString("UserID", "");
        return "";
    }


    public static void saveTeamID(Context context, String newTeamID) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("TeamID", newTeamID);
        edit.apply();
    }

    public static String getTeamID(Context context){
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("TeamID"))
            return prefs.getString("TeamID", "");
        return "";
    }

    public static int getDailyStepsTester(Context context){
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("dailyStepsTester"))
            return prefs.getInt("dailyStepsTester", -1);
        return -1;
    }

    public static int getDailyDistanceTester(Context context){
        SharedPreferences prefs = setUp(context);
        if(prefs.contains("dailyDistanceTester"))
            return prefs.getInt("dailyDistanceTester", -1);
        return -1;
    }

    public static void saveOnTeam(Context context) {
        SharedPreferences prefs = setUp(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("OnTeam", true);
        edit.apply();
    }

    public static boolean getOnTeam(Context context) {
        SharedPreferences prefs = setUp(context);
        return (prefs.contains("OnTeam"));
    }

}
