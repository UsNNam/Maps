package com.example.mapsproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SavePlaceFragment extends Fragment {
    MainActivity main;
    ImageButton back;
    ImageButton history;
    CustomSavePlaceAdapter adapter;
    ListView listfavoriteplace;
    private Context context;
    PlacesClient placesClient;
    public String api_key= "AIzaSyC4eQTS4oxvsgONLXCsbeBFUp68WhXYTJ0";
    public static SavePlaceFragment newInstance(String strArg1)
    {
        SavePlaceFragment fragment = new SavePlaceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Activities containing this fragment must implement MainCallbacks
//        if (!(getActivity() instanceof MainCallbacks)) throw new IllegalStateException(">>> Activity must implement MainCallbacks");
        main = (MainActivity) getActivity();
        context = getActivity();
        Places.initialize(context,api_key );
        this.context=context;
        placesClient = Places.createClient(this.context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout save_place_layout =  (LinearLayout) inflater.inflate(R.layout.saveplace_fragment, null);

        listfavoriteplace = (ListView) save_place_layout.findViewById(R.id.listfavoriteplace);

        List<String> addresses = new ArrayList<>();
        history = (ImageButton) save_place_layout.findViewById(R.id.historyBtn);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "location history clicked", Toast.LENGTH_SHORT).show();
                main.showLocationHistory();
            }
        });
//        Geocoder geocoder = new Geocoder(main, Locale.getDefault());
//        List<Address> addresses = null;
        Toast.makeText(context, "chay trong nay", Toast.LENGTH_SHORT).show();

        SavePlaceDB sp = new SavePlaceDB("test", context);
        sp.readData(new SavePlaceDB.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<HashMap<String, Object>> list) throws IOException {
                for (HashMap j : list)
                {
                    String temp = j.get("placeName").toString();
                    addresses.add(temp);
                }
                if (addresses!=null) {
                    adapter = new CustomSavePlaceAdapter(context, R.layout.favorite_place, addresses);
                    listfavoriteplace.setAdapter(adapter);
                    Toast.makeText(context, "chay adapter" + addresses, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(context, "khong co gi trong nay", Toast.LENGTH_SHORT).show();
                listfavoriteplace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MainActivity.onMsgFromSPToMain("SAVEPLACE", String.valueOf(list.get(position).get("placeID").toString()),
                               list.get(position).get("placeName").toString(), (Double) list.get(position).get("latitude"), (Double) list.get(position).get("longitude"));
                        Log.d("TESTCLICK", position+ " ");
                    }
                });
            }
        });
        Log.d(TAG, addresses + " ket qua");



        return save_place_layout;
    }

}
