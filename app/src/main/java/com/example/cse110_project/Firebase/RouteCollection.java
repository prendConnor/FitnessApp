package com.example.cse110_project.Firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cse110_project.utils.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteCollection {

    FirebaseFirestore db;
    private final String TAG = "FirebaseRoutes";
    public ArrayList<Route> qryRoutes;
    ArrayList<QueryDocumentSnapshot> qryDocs;

    /* Initialize firebase instance */
    public RouteCollection() {
        db = FirebaseFirestore.getInstance();
        if (this.db == null) {
            Log.d(TAG, "Unsuccessful firebase instance");
        } else {
            Log.d(TAG, "Success");
        }
        this.qryRoutes = new ArrayList<Route>();
    }


    /* Initialize Firebase App */
    public static void initFirebase(Context context) {
        FirebaseApp.initializeApp(context);
    }


    /* add routes along with device ID */
    public void addRoute(Route addRoute, String deviceID, String initial, int[] colors,
                         String timer, String steps, String distance) {

        Map<String, Object> route = addRoute.getFeatureMap();
        route.put("initials", initial);
        route.put("deviceID", deviceID);
        route.put("createdBy", initial);
        route.put("red", colors[0]);
        route.put("green", colors[1]);
        route.put("blue", colors[2]);
        Log.d(TAG, "Adding COLORS to Route: " + colors[0] +", " + colors[1] + ", " + colors[2]);


         // Add a new document with a generated ID
        db.collection("routes")
                .add(route)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with Route-ID: " + documentReference.getId());

                        addRoute.setId(documentReference.getId());
                        //addToCompletedRoutes(deviceID, documentReference.getId(), addRoute.getLastCompletedTime(),
                                //addRoute.getLastCompletedDistance(), addRoute.getLastCompletedSteps());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }



    /* Update walking stats to existing routes */
    public void updateRouteStats(String id, String deviceID, String lastCompletedTime, String lastCompletedSteps, String lastCompletedDistance, String initials) {

        //if route not is already completed, add to completed routes

        addToCompletedRoutes(id, deviceID, lastCompletedSteps, lastCompletedDistance, lastCompletedTime);

        db.collection("routes").document(id)
                .update(
                        "lastCompletedTime", lastCompletedTime,
                        "lastCompletedSteps", lastCompletedSteps,
                        "lastCompletedDistance", lastCompletedDistance,
                        "initials", initials
                );
    }

    public void addToCompletedRoutes(String routeID, String deviceID, String steps, String distance,
                                     String time) {
        Log.d(TAG, "saving route " + routeID + " to user " + deviceID);

        Map<String,Object> map = new HashMap<>();
        map.put("distance", distance);
        map.put("steps", steps);
        map.put("time", time);

        db.collection("users")
                .document(deviceID)
                .collection("completedRoutes")
                .document(routeID)
                .set(map);


        db.collection("routes")
                .document(routeID)
                .collection("completedUsers")
                .document(deviceID)
                .set(map);

    }


    /* Delete selected Route by ID */
    public void deleteRoute(String id) {
        db.collection("routes").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Route is deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting rooute", e);
                    }
                });
    }

    /* Get routes for current device */
    public void getRoutes(String deviceID, final MyCallback myCallback){

        ArrayList<String> completedRoutesID = new ArrayList<>();
        HashMap<String, QueryDocumentSnapshot> statsMap = new HashMap<>();

        db.collection("users")
                .document(deviceID)
                .collection("completedRoutes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Completed Routes: ");


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "Completed this route " + document.getId() + " by " + deviceID);
                                Log.d(TAG, "with stats as " + document.getData());
                                completedRoutesID.add(document.getId());
                                statsMap.put(document.getId(), document);
                            }

                            db.collection("routes")
                                    .whereEqualTo("deviceID", deviceID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                ArrayList<Route> routesSimpleList = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    try {
                                                        //Route newRoute = makeRoute(document);
                                                        Log.d(TAG, "DATA: " + document.getData());

                                                        if(completedRoutesID.contains(document.getId())) {
                                                            QueryDocumentSnapshot statsDoc = statsMap.get(document.getId());
                                                            routesSimpleList.add(makeRoute(document,deviceID, true, statsDoc));
                                                        } else {
                                                            routesSimpleList.add(makeRoute(document, deviceID, false, null));
                                                        }

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Log.d(TAG, "THIS ROUTE CAN't be ADDED");
                                                    }

                                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                                }

                                                Log.d(TAG, "CURRENT LIST OF ROUTES: " + routesSimpleList.size());
                                                for(Route curr : routesSimpleList) {
                                                    Log.d(TAG, curr.getName() + " " + curr.getId());
                                                }
                                                myCallback.getRoutes(routesSimpleList);

                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }


    /* This method makes Route object from returning query document snapshot */
    private Route makeRoute(QueryDocumentSnapshot qry, String deviceID, boolean hasWalked, QueryDocumentSnapshot statsMap){


        try {

            Route newRoute;
            String id = qry.getId();

            String title = "";

            if(qry.getData().get("title") != null) {
                title = qry.getData().get("title").toString();
            }

            String start_position = "";

            if(qry.getData().get("start_position") != null){
                start_position = qry.getData().get("start_position").toString();
            }


            String notes = "";

            if(qry.getData().get("notes") != null){
                notes = qry.getData().get("notes").toString();
            }


            boolean out = false;
            boolean loop= false;
            boolean flat= false;
            boolean hills= false;
            boolean even= false;
            boolean rough= false;
            boolean street= false;
            boolean trail= false;
            boolean easy= false;
            boolean medium= false;
            boolean hard= false;
            boolean favorite= false;

            String initial = "";

            if(qry.getData().get("createdBy") != null) {
                initial = qry.getData().get("createdBy").toString();
            }

            if(qry.getData().get("out") != null){
                out = Boolean.parseBoolean(qry.getData().get("out").toString());
            }
            if(qry.getData().get("loop") != null){
                loop = Boolean.parseBoolean(qry.getData().get("loop").toString());
            }
            if(qry.getData().get("flat") != null){
                flat = Boolean.parseBoolean(qry.getData().get("flat").toString());
            }
            if(qry.getData().get("hills") != null){
                hills = Boolean.parseBoolean(qry.getData().get("hills").toString());
            }
            if(qry.getData().get("even") != null){
                even  = Boolean.parseBoolean(qry.getData().get("even").toString());
            }
            if(qry.getData().get("rough") != null){
                rough = Boolean.parseBoolean(qry.getData().get("rough").toString());
            }
            if(qry.getData().get("street") != null){
                street = Boolean.parseBoolean(qry.getData().get("street").toString());
            }
            if(qry.getData().get("trail") != null){
                trail = Boolean.parseBoolean(qry.getData().get("trail").toString());
            }
            if(qry.getData().get("easy") != null){
                easy = Boolean.parseBoolean(qry.getData().get("easy").toString());
            }
            if(qry.getData().get("medium") != null){
                medium = Boolean.parseBoolean(qry.getData().get("medium").toString());
            }
            if(qry.getData().get("hard") != null){
                hard = Boolean.parseBoolean(qry.getData().get("hard").toString());
            }
            if(qry.getData().get("favorite") != null){
                favorite = Boolean.parseBoolean(qry.getData().get("favorite").toString());
            }

            boolean[] tags ={out, loop, flat, hills, even, rough, street, trail, easy, medium, hard, favorite};
            newRoute = new Route(title, start_position, tags, notes);

            newRoute.setNotes(notes);
            newRoute.setId(qry.getId().toString());

            newRoute.setFavorite(favorite);

            newRoute.setCreatedBy(initial);
            Log.d(TAG, "ROUTE IS CREATED BY : " + initial + " and got " + newRoute.getCreatedBy());

            String time;
            String steps;
            String distance;



            if(qry.getData().get("lastCompletedTime") != null ){
                time = qry.getData().get("lastCompletedTime").toString();
                if(time != null){
                    newRoute.setLastCompletedTime(time);
                }
            }

            if(qry.getData().get("lastCompletedSteps") != null){
                steps = qry.getData().get("lastCompletedSteps").toString();
                if(steps != null){
                    newRoute.setLastCompletedSteps(steps);
                }
            }

            if(qry.getData().get("lastCompletedDistance") != null){
                distance = qry.getData().get("lastCompletedDistance").toString();
                if(distance != null){
                    newRoute.setLastCompletedDistance(distance);
                }
            }

            Log.d(TAG, "checking prev walked from make route");

            if(hasWalked) {
                Log.d(TAG, "THIS USER WALKED ON THIS ROUTE: " + id);
                Log.d(TAG, statsMap.getData().toString());
                newRoute.setLastCompletedDistance(statsMap.getData().get("distance").toString());
                newRoute.setLastCompletedSteps(statsMap.getData().get("steps").toString());
                newRoute.setLastCompletedTime(statsMap.getData().get("time").toString());
                newRoute.setPrevWalked(true);
            }

            newRoute.setId(id);
            return newRoute;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "PROBLEMS WITH GETTING BACK ROUTES");
            return null;
        }

    }

    public void checkPrevWalked(String routeID, String deviceID, Route route) {
        Log.d(TAG, "checking route with ID:  " + routeID);
        db.collection("users")
                .document(deviceID)
                .collection("completedRoutes")
                .document(routeID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 DocumentSnapshot doc = task.getResult();
                 if(doc.exists()) {
                     Log.d(TAG, "route is prev completed with ID: " + doc.getId());
                     String lastCompletedDist = (String) doc.get("distance");
                     String lastCompletedSteps = (String) doc.get("steps");
                     String lastCompletedTime = (String) doc.get("time");
                     route.setLastCompletedDistance(lastCompletedDist);
                     route.setLastCompletedSteps(lastCompletedSteps);
                     route.setLastCompletedTime(lastCompletedTime);
                     route.setPrevWalked(true);
                     Log.d(TAG, "prev walk exist with");
                     Log.d(TAG, "steps: " + lastCompletedSteps);
                     Log.d(TAG, "dist: " + lastCompletedDist);
                     Log.d(TAG, "time: " + lastCompletedTime);
                 }
             }
         });
    }


    /* TODO: Update firebase with newest time */

    /* TODO: Add to firebase with extra information */




}
