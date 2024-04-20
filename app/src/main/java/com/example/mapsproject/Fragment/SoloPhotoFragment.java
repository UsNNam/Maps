package com.example.mapsproject.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mapsproject.MainActivity;
import com.example.mapsproject.PlaceDetailFragment;
import com.example.mapsproject.R;
import com.github.chrisbanes.photoview.PhotoView;

public class SoloPhotoFragment extends Fragment {


    private Context context;
    private PhotoView imageView;
    private ImageButton back;
    private MainActivity mainActivity;

    public static SoloPhotoFragment newInstance(String strArg) {
        SoloPhotoFragment fragment = new SoloPhotoFragment();
        Bundle args = new Bundle();
        args.putString("Place Detail Fragment", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            mainActivity = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout_solo_photo = (LinearLayout) inflater.inflate(R.layout.picture_high_resolution, container, false);

        imageView = layout_solo_photo.findViewById(R.id.imgSoloPhoto);

        back = layout_solo_photo.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMsgFromSearchToMain("SoloPhotoFragmentOff", null);
            }
        });

        return layout_solo_photo;
    }

    public void setPicture(String url) {
        Glide.with(context)
                .load(url)
                .into(imageView);
    }
}
