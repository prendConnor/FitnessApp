package com.example.cse110_project.Firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cse110_project.utils.Route;
import com.example.cse110_project.utils.Team;
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

public class TeamCollection {

    FirebaseFirestore db;
    private final String TAG = "Firebase Team";


    /* Initialize firebase instance */
    public TeamCollection() {
        db = FirebaseFirestore.getInstance();
        if (this.db == null) {
            Log.d(TAG, "Unsuccessful firebase instance");
        } else {
            Log.d(TAG, "Success");
        }
    }


    /* Initialize Firebase App */
    public static void initFirebase(Context context) {
        FirebaseApp.initializeApp(context);
    }

    public String makeTeam(String deviceID) {
        Map<String, Object> teamMap = new HashMap<>();
        ArrayList<String> userIDs = new ArrayList<String>();
        userIDs.add(deviceID);
        teamMap.put("listOfUserIDs", userIDs );
        String teamID;

        db.collection("teams")
                .add(teamMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        teamID = getTeamID(deviceID);
        Log.d(TAG, "Got ID for new team");

        return teamID;

    }

    public String getTeamID(String deviceID) {
        DocumentReference docRef = db.collection("teams").document(deviceID);
        return docRef.getId();

    }

    public void sendInvitation(String userId, String teamId, String fromUserID){
        Map<String,Object> invitationMap = new HashMap<>();
        invitationMap.put("teamId", teamId);
        invitationMap.put("fromUserID", fromUserID);

        db.collection("users")
                .document(userId)
                .collection("invitations")
                .add(invitationMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.d(TAG, "Send an invite to " + userId + " from team " + teamId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        //add invited user to list of pending invitations
        Map<String, Object> teamMap = new HashMap<>();
        ArrayList<String> userIDs = new ArrayList<String>();
        userIDs.add(userId);
        teamMap.put("listOfUserIDs", userIDs );

        db.collection("teams")
                .document(teamId)
                .collection("listOfPendingUserIds")
                .add(invitationMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.d(TAG, "Send an invite to " + userId + " from team " + teamId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }


    /* Get current user for current device */
    public void sendInvitationEmail(String email, String teamId, String fromUserID) {

        db.collection("users")
                .whereEqualTo("gmail", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                sendInvitation(document.getId().toString(), teamId, fromUserID);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }
}
