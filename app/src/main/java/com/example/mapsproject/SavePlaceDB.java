package com.example.mapsproject;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SavePlaceDB  {
    FirebaseFirestore db;
    DocumentReference docRef;
    private LoadingDialog loadingDialog;
    Context context;

    SavePlaceDB(String username, Context context)
    {
        this.db= FirebaseFirestore.getInstance();
        this.context=context;
//        this.favoriteplace = new ArrayList<>();
        this.docRef = db.collection("SavePlace").document(username);
        this.loadingDialog = new LoadingDialog(context);
        loadingDialog.createLoadingDialog();
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

    public void removeSavePlace(String id, String name, Double latitude, Double longitude)
    {
        Map<String, Object> geo = new HashMap<>();
        geo.put("placeID",id);
        geo.put("placeName", name);
        geo.put("latitude",latitude);
        geo.put("longitude", longitude);
        docRef.update("favoriteplace", FieldValue.arrayRemove(geo));
    }

    public void readData(FirestoreCallback firestoreCallback)
    {
//        loadingDialog.showDialog();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if(!document.exists())
                    {
                        ArrayList<HashMap<String, Object>> favoriteplace = new ArrayList<HashMap<String, Object>> ();
                        try {
                            firestoreCallback.onCallback(favoriteplace);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, "get failed with !document exists ", task.getException());
                    }
                    else {   //Da tao database - add them vao
                        ArrayList<HashMap<String, Object>> favoriteplace = (ArrayList<HashMap<String, Object>>) document.get("favoriteplace");
                        Log.d(TAG, "get failed with document exists" + favoriteplace);
                        try {
                            firestoreCallback.onCallback(favoriteplace);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Log.d(TAG, "get failed with document exists" + favoriteplace);
                    }
                } else
                {
                    ArrayList<HashMap<String, Object>> favoriteplace = new ArrayList<HashMap<String, Object>> ();
                    try {
                        firestoreCallback.onCallback(favoriteplace);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "get failed with ", task.getException());
                }
//                loadingDialog.hideDialog();
            }

        });
    }

    public void createSavePlaceV2(String id, String name, Double latitude, Double longitude) {
        ArrayList<Object> place = new ArrayList<>();
        Map<String, Object> geo = new HashMap<>();
        geo.put("placeID",id);
        geo.put("placeName", name);
        geo.put("latitude",latitude);
        geo.put("longitude", longitude);
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

    public interface FirestoreCallback
    {
        void onCallback(ArrayList<HashMap<String, Object>> list) throws IOException;
    }
}
