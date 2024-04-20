package com.example.mapsproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class CoordinateConvertFragment extends Fragment {

    EditText kinhdo, vido;
    Button buttonConvert;
    TextView ketquaAddressLine;
    Button butConvertPage, butHistoryPage;
    ImageButton copyButton;
    MainActivity main;
    LatLng2AddressFragment firstFragment;

    public static CoordinateConvertFragment newInstance(String strArg1) {
        CoordinateConvertFragment fragment = new CoordinateConvertFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout coordinate_layout = (LinearLayout) inflater.inflate(R.layout.header_layout, null);

        butConvertPage = coordinate_layout.findViewById(R.id.butConvertPage);
        butHistoryPage = coordinate_layout.findViewById(R.id.butHistoryPage);
        butConvertPage.setSelected(true);
        butConvertPage.setEnabled(false);

        butHistoryPage.setEnabled(true);
        butHistoryPage.setSelected(false);

        butHistoryPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butConvertPage.setSelected(false);
                butConvertPage.setEnabled(true);
                butHistoryPage.setSelected(true);
                butHistoryPage.setEnabled(false);

                replaceFragmentContent(Address2LatLngFragment.newInstance("Address2LatLngFragment"));

                Log.e("Replaced fragment", "2");
            }
        });
        butConvertPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butHistoryPage.setSelected(false);
                butHistoryPage.setEnabled(true);
                butConvertPage.setSelected(true);
                butConvertPage.setEnabled(false);
                replaceFragmentContent(LatLng2AddressFragment.newInstance("latln2address"));
                Log.e("Replaced fragment", "1");
            }
        });

        firstFragment = LatLng2AddressFragment.newInstance("latln2address");
        FragmentTransaction fragmentManager = main.getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.container_body, firstFragment);
        fragmentManager.commit();


//        butConvertPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                butHistoryPage.setSelected(false);
//                butHistoryPage.setEnabled(true);
//                butConvertPage.setSelected(true);
//                butConvertPage.setEnabled(false);
//                setContentView(R.layout.address2coordinate_layout);
//
//            });
//
//
//
//        }
//    });

        return coordinate_layout;
    }

    protected void replaceFragmentContent(Fragment fragment) {

        if (fragment != null) {

            FragmentTransaction fmgr = main.getSupportFragmentManager().beginTransaction();
            fmgr.replace(R.id.container_body, fragment);
            fmgr.commit();

        }

    }


}







