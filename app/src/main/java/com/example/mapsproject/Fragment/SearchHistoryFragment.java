package com.example.mapsproject.Fragment;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mapsproject.CustomSavePlaceAdapter;
import com.example.mapsproject.CustomSearchHistoryAdapter;
import com.example.mapsproject.GlobalVariable;
import com.example.mapsproject.MainActivity;
import com.example.mapsproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchHistoryFragment extends Fragment {
    MainActivity main;
    ImageButton back;
    CustomSavePlaceAdapter adapter;
    ListView historyList;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    public static SearchHistoryFragment newInstance(String strArg1)
    {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.docRef = db.collection("SearchHistory").document(GlobalVariable.userName);
        main = (MainActivity) getActivity();
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        String saveField = "SearchPlaces";
        LinearLayout layout =  (LinearLayout) inflater.inflate(R.layout.search_history_fragment, null);
        back = (ImageButton) layout.findViewById(R.id.backButton);
        historyList = (ListView) layout.findViewById(R.id.historyList);
        Geocoder geocoder = new Geocoder(main, Locale.getDefault());

        this.docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    ArrayList<String> placeIdArray = (ArrayList<String>) documentSnapshot.get("SearchPlacesId");
                    ArrayList<String> placeNameArray = (ArrayList<String>) documentSnapshot.get("SearchPlacesName");
                    ArrayList<String> placeAddArray = (ArrayList<String>) documentSnapshot.get("SearchPlacesAddress");

                    if (placeIdArray != null) {
                        CustomSearchHistoryAdapter adapter = new CustomSearchHistoryAdapter(context, R.layout.search_history_item, reverseArrayList(placeIdArray), reverseArrayList(placeNameArray), reverseArrayList(placeAddArray));
                        historyList.setAdapter(adapter);
                    }

                } else {
                    Log.d("SearchHistoryFragment", "Document does not exist");
                }
            }
        });

        return layout;
    }

    public ArrayList<String> reverseArrayList(ArrayList<String> alist)
    {
        // Arraylist for storing reversed elements
        ArrayList<String> revArrayList = new ArrayList<String>();
        for (int i = alist.size() - 1; i >= 0; i--) {

            // Append the elements in reverse order
            revArrayList.add(alist.get(i));
        }

        // Return the reversed arraylist
        return revArrayList;
    }

}
