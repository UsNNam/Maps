package com.example.mapsproject;


import static android.content.Context.CLIPBOARD_SERVICE;
import static android.text.TextUtils.isEmpty;

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

public class Address2LatLngFragment extends Fragment {
    Button buttonConvertAddress;
    EditText address;
    TextView resultLongtitude, resultLatitude;
    ImageButton copyButtonAddress;
    MainActivity main;
    public static Address2LatLngFragment newInstance(String strArg1) {
        Address2LatLngFragment fragment = new Address2LatLngFragment();
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
        View view=inflater.inflate(R.layout.fragment2, container, false);

        buttonConvertAddress = view.findViewById(R.id.buttonConvertAddress);
        address = view.findViewById(R.id.address);
        resultLatitude = view.findViewById(R.id.resultLatitude);
        resultLongtitude = view.findViewById(R.id.resultLongtitude);

        copyButtonAddress = view.findViewById(R.id.copyButtonAddress);

        buttonConvertAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String addressStr = address.getText().toString();
                if (isEmpty(addressStr))
                {
                    Toast.makeText(getActivity(), "Address is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Log.e(TAG, addressStr);
                try {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
//                    resultLatitude.setText(addressStr);
                    List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);

                    resultLatitude.setText("Latitude: " + addresses.get(0).getLatitude());
                    resultLongtitude.setText("Longtitude: " + addresses.get(0).getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                    resultLongtitude.setText("No result found");

                }
            }
        });
        copyButtonAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
                if(!resultLongtitude.getText().toString().equals("")
                && !resultLongtitude.getText().toString().equals(""))
                {
                    ClipData clip = ClipData.newPlainText("address", resultLongtitude.getText().toString().substring(12) + resultLatitude.getText().toString().substring(9));

                    clipboard.setPrimaryClip(clip);

                }
                Toast.makeText(getActivity(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}