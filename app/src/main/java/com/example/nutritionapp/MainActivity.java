package com.example.nutritionapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nutritionapp.configuration.PersonalInformation;
import com.example.nutritionapp.customFoods.CustomFoodOverview;
import com.example.nutritionapp.foodJournal.FoodJournalOverview;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.Recommendations;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        /* load database on Application start */
        @SuppressWarnings("unused") final Database db = new Database(this);

        /* Setup Toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle =findViewById(R.id.toolbar_title);

        toolbar.setTitle("");
        toolbarTitle.setText(R.string.mainActivityToolbarTitle);

        ImageView toolbarImageLeft = findViewById(R.id.toolbar_back);
        ImageView toolbarImageRight = findViewById(R.id.toolbar_forward);
        toolbarImageLeft.setImageResource(R.drawable.ic_grainleft);
        toolbarImageRight.setImageResource(R.drawable.ic_grainright);

        setSupportActionBar(toolbar);

        /* Journal */
        View foodJournalButtonView = findViewById(R.id.food_journal);
        foodJournalButtonView.setBackgroundResource(R.drawable.button_ripple_animation_blue);

        TextView foodJournalButtonTitle = foodJournalButtonView.findViewById(R.id.button_title);
        TextView foodJournalButtonLeftTag = foodJournalButtonView.findViewById(R.id.buttonDescription);

        foodJournalButtonTitle.setText(R.string.foodJournalButtonTitle);
        foodJournalButtonLeftTag.setText(R.string.foodJournalButtonLeftTag);

        foodJournalButtonView.setOnClickListener(v -> {
            Intent journal = new Intent(v.getContext(), FoodJournalOverview.class);
            startActivity(journal, Utils.getDefaultTransition(this));
        });

        /* Configuration */
        View configButtonView = findViewById(R.id.config);
        configButtonView.setBackgroundResource(R.drawable.button_ripple_animation_orange);

        TextView configButtonTitle = configButtonView.findViewById(R.id.button_title);
        TextView configButtonLeftTag = configButtonView.findViewById(R.id.buttonDescription);

        configButtonTitle.setText(R.string.configButtonTitle);
        configButtonLeftTag.setText(R.string.configButtonLeftTag);

        configButtonView.setOnClickListener(v -> {
            Intent configuration = new Intent(v.getContext(), PersonalInformation.class);
            startActivity(configuration, Utils.getDefaultTransition(this));
        });

        /* Custom Food Creation */
        View createCustomFoodsView = findViewById(R.id.create_foods);
        createCustomFoodsView.setBackgroundResource(R.drawable.button_ripple_animation_purple);

        TextView createCustomFoodButtonTitle = createCustomFoodsView.findViewById(R.id.button_title);
        TextView createCustomFoodTagLef = createCustomFoodsView.findViewById(R.id.buttonDescription);

        createCustomFoodButtonTitle.setText(R.string.createFoodsButton);
        createCustomFoodTagLef.setText(R.string.createFoodsButtonLeftText);

        createCustomFoodsView.setOnClickListener(v -> {
            Intent createCustomFood = new Intent(v.getContext(), CustomFoodOverview.class);
            startActivity(createCustomFood, Utils.getDefaultTransition(this));
        });

        /* Analysis */
        View showAnalysisButtonView = findViewById(R.id.recommendations);
        showAnalysisButtonView.setBackgroundResource(R.drawable.button_ripple_animation_red);

        TextView analysisButtonTitle = showAnalysisButtonView.findViewById(R.id.button_title);
        TextView analysisButtonDescription = showAnalysisButtonView.findViewById(R.id.buttonDescription);

        analysisButtonTitle.setText(R.string.analysisButtonTitle);
        analysisButtonDescription.setText(R.string.analysisButtonLeftTag);

        showAnalysisButtonView.setOnClickListener(v -> {
            Intent analysis =  new Intent(v.getContext(), Recommendations.class);
            startActivity(analysis, Utils.getDefaultTransition(this));
        });

    }
}
