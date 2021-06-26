package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton toolbarBack = findViewById(R.id.toolbar_back);
        toolbar.setTitle("");
        toolbarTitle.setText(R.string.about);
        setSupportActionBar(toolbar);
        toolbarBack.setOnClickListener((v -> finish()));
        toolbarBack.setImageResource(R.drawable.ic_arrow_back_black_24dp);

        LinearLayout layout = findViewById(R.id.aboutScrollViewLinearLayout);

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

            TextView tmpTitle = new TextView(this);
            TextView tmpText = new TextView(this);
            View hline = new View(this);
            hline.setBackgroundColor(this.getColor(R.color.hlineSilver));

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
            hline.setLayoutParams(hlineParams);

            layout.addView(tmpTitle);
            layout.addView(tmpText);
            if(i != sectionTitles.length - 1){
                layout.addView(hline);
            }
        }

    }
}