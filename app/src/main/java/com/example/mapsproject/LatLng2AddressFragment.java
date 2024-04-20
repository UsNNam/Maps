package com.example.mapsproject;


import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LatLng2AddressFragment extends Fragment {

     EditText kinhdo, vido;
    Button buttonConvert;
    TextView ketquaAddressLine;
    ImageButton copyButton;
    MainActivity main;
    public static LatLng2AddressFragment newInstance(String strArg1) {
        LatLng2AddressFragment fragment = new LatLng2AddressFragment();
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
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment1, container, false);

         buttonConvert = view.findViewById(R.id.buttonConvert);
        kinhdo = view.findViewById(R.id.longtitude);
        vido = view.findViewById(R.id.latitude);

        ketquaAddressLine = view.findViewById(R.id.resultAddressLine);
        copyButton = view.findViewById(R.id.copyButton);

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = null;
                Double vido_double, kinhdo_double;
                
               
                    try {
                        kinhdo_double = Double.parseDouble(kinhdo.getText().toString());
                        
                        if (!checkValidInputCoordinate(kinhdo_double,180))
                        {
                            Toast.makeText(getActivity(), "Longtitude ranges from -180 to 180", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        

                    }
                    catch (NumberFormatException e)
                    {
                        Toast.makeText(getActivity(), "Enter a number in longtitude, please!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        vido_double = Double.parseDouble(vido.getText().toString());
                        
                        if (!checkValidInputCoordinate(vido_double,90))
                        {
                            Toast.makeText(getActivity(), "Latitude ranges from -90 to 90", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        Toast.makeText(getActivity(), "Enter a number in latitude, please", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    Geocoder geocoder = new Geocoder(getActivity(), locale.getDefault());
                    List<Address> listAddress = null;
                    try {
                        listAddress = geocoder.getFromLocation(vido_double, kinhdo_double, 1);
                    } catch (IOException e) {
                        ketquaAddressLine.setText("No result was found");
                    }
                    if (listAddress.size()>0) {
    
                            ketquaAddressLine.setText(listAddress.get(0).getAddressLine(0));
                        }
                        else
                        {
                            ketquaAddressLine.setText("No result was found");
                        }
                    }
             
            
        });
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("address", ketquaAddressLine.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


    return view;
 }
    public boolean checkValidInputCoordinate(double coordinate, double limit)
    {
        if (Math.abs(coordinate) > limit)
        {
            return false;
        }
        return true;
    }
}