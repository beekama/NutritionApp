package com.example.nutritionapp;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AboutPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutPageFragment newInstance(String param1, String param2) {
        AboutPageFragment fragment = new AboutPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_page, container, false); //todo rename

        LinearLayout layout = view.findViewById(R.id.aboutScrollViewLinearLayout);

        String[] sectionTitles = {
                "Developed By",
                "License Information",
                "Libraries License Information",
                "Data License Information",
                "Disclaimer"
        };
        String[] sectionTexts  = {
                "Kathrin Maurer\n" +
                        "Yannik Schmidt",
                "This Software is licensed GPLv3, it contains libraries with compatible licenses listed below. "
                        + "The source code can be found here: \n\nhttps://github.com/beekama/NutritionApp/",
                "MPAndroidChart by PhilJay licensed Apache 2.0\n" +
                        "Some other Project licensed GPLv2",
                "The Data used in this project is provided by the United States Food and Drug Administration (USDA), it is in the public domain.",
                "The information and help provided by this App is not medical advise. We do not have any affiliation with any industry entity or interest group, " +
                        "this App does not and will never have any monetization. We will never send your data off your phone."
        };

        for(int i = 0; i<sectionTitles.length; i++){

            TextView tmpTitle = new TextView(getContext());
            TextView tmpText = new TextView(getContext());
            View hLine = new View(getContext());
            hLine.setBackgroundColor(getResources().getColor(R.color.hlineSilver)); 

            tmpTitle.setText(sectionTitles[i]);
            tmpTitle.setTextAppearance(R.style.TextAppearance_MaterialComponents_Body1);
            tmpTitle.setTypeface(tmpTitle.getTypeface(), Typeface.BOLD_ITALIC);
            tmpTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

            tmpText.setText(sectionTexts[i]);
            tmpText.setAutoLinkMask(Linkify.WEB_URLS);
            tmpText.setMovementMethod(LinkMovementMethod.getInstance());

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams hlineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 2);

            hlineParams.topMargin = 30;
            hlineParams.bottomMargin = 20;

            titleParams.bottomMargin = 10;
            titleParams.topMargin = 10;

            tmpTitle.setLayoutParams(titleParams);
            tmpText.setLayoutParams(textParams);
            hLine.setLayoutParams(hlineParams);

            layout.addView(tmpTitle);
            layout.addView(tmpText);
            if(i != sectionTitles.length - 1){
                layout.addView(hLine);
            }
        }

        return view;
    }
}