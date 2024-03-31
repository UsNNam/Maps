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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SavePlaceFragment extends Fragment {
    MainActivity main;
    ImageButton back;
    CustomSavePlaceAdapter adapter;
    ListView listfavoriteplace;
    private Context context;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout save_place_layout =  (LinearLayout) inflater.inflate(R.layout.saveplace_fragment, null);
        back = (ImageButton) save_place_layout.findViewById(R.id.backButton);
        listfavoriteplace = (ListView) save_place_layout.findViewById(R.id.listfavoriteplace);
        Geocoder geocoder = new Geocoder(main, Locale.getDefault());
        List<Address> addresses = null;
        Toast.makeText(context, "chay trong nay", Toast.LENGTH_SHORT).show();
        try {
            addresses = geocoder.getFromLocation(1.222222, 106.2, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, addresses + " ket qua");
        if (addresses!=null) {
            adapter = new CustomSavePlaceAdapter(context, R.layout.favorite_place, addresses);
            listfavoriteplace.setAdapter(adapter);
            Toast.makeText(context, "chay adapter", Toast.LENGTH_SHORT).show();
        }
        else Toast.makeText(context, "khong co gi trong nay", Toast.LENGTH_SHORT).show();
        return save_place_layout;
    }
}
