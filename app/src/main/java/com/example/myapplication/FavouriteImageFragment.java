package com.example.myapplication;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FavouriteImageFragment extends Fragment {

    private static final String this_TITLE = "TITLE";
    private static final String this_DATE = "DATE";
    private static final String this_EXPLANATION = "EXPLANATION";
    private static final String this_HDURL = "HDURL";
    private static final String this_FILEPATH = "FILEPATH";

    public FavouriteImageFragment() {
        //empty constructor
    }

    public static FavouriteImageFragment newInstance(Bundle arguments) {
        FavouriteImageFragment fragment = new FavouriteImageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourite_details_fragment, container, false);
        if (getArguments() != null) {
            setText(view, R.id.favouriteTitle, getArguments().getString(this_TITLE));
            setText(view, R.id.favouriteDate, getArguments().getString(this_DATE));
            setText(view, R.id.favouriteExplanation, getArguments().getString(this_EXPLANATION));
            setText(view, R.id.favouriteURL, getArguments().getString(this_HDURL));
            setImage(view, R.id.favouriteImage, getArguments().getString(this_FILEPATH));
        }
        return view;
    }

    private void setText(View view, int textViewId, String text) {
        TextView textView = view.findViewById(textViewId);
        textView.setText(text);
    }

    private void setImage(View view, int imageViewId, String imagePath) {
        ImageView imageView = view.findViewById(imageViewId);
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }
}
