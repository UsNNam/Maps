package com.example.mapsproject;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SavePlaceDB {
    FirebaseFirestore db;
    DocumentReference docRef;

    SavePlaceDB(String username)
    {
        this.db= FirebaseFirestore.getInstance();
//        this.favoriteplace = new ArrayList<>();
        this.docRef = db.collection("SavePlace").document(username);
    }
    public void createSavePlace(Double longtitude, Double latitude)
    {

        ArrayList<Object> place = new ArrayList<>();
        Map<String, Object> geo = new HashMap<>();
        geo.put("latitude",latitude);
        geo.put("longtitude", longtitude);
        Collections.addAll(place,geo);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {

                    DocumentSnapshot document = task.getResult();
                    if(!document.exists())   //chưa tạo database cho người dùng
                    {
                        Map<String, Object> user = new HashMap<>();
                        user.put("favoriteplace",place);
                        docRef.set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    }
                    else {   //Da tao database - add them vao
                        docRef.update("favoriteplace",FieldValue.arrayUnion(geo));
                    }
                } else
                {
                Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void removeSavePlace(Double longtitude, Double latitude)
    {
        Map<String, Object> geo = new HashMap<>();
        geo.put("latitude",latitude);
        geo.put("longtitude", longtitude);

        docRef.update("favoriteplace", FieldValue.arrayRemove(geo));
    }
    public void returnSavePlace()
    {
        ArrayList<HashMap<String,Object>> temp = new ArrayList<>();

//        Log.d(TAG, "Cached document data: " + favoriteplace);
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<HashMap<String, Object>> list) {
                Log.d(TAG, "alo " + list.toString());
//                favoriteplace=list;
            }
        });
//        Log.d(TAG, "alo " + favoriteplace);

    }
    public void readData(FirestoreCallback firestoreCallback)
    {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(!document.exists())
                    {
                        ArrayList<HashMap<String, Object>> favoriteplace = new ArrayList<HashMap<String, Object>> ();
                        firestoreCallback.onCallback(favoriteplace);
                        Log.d(TAG, "get failed with !document exists ", task.getException());
                    }
                    else {   //Da tao database - add them vao
                        ArrayList<HashMap<String, Object>> favoriteplace = (ArrayList<HashMap<String, Object>>) document.get("favoriteplace");
                        firestoreCallback.onCallback(favoriteplace);
                        Log.d(TAG, "get failed with document exists");
                    }
                } else
                {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }

        });
    }
    public interface FirestoreCallback
    {
        void onCallback(ArrayList<HashMap<String, Object>> list);
    }
}
