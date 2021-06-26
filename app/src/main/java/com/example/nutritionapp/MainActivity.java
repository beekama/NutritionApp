package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nutritionapp.configuration.PersonalInformation;
import com.example.nutritionapp.customFoods.CustomFoodOverview;
import com.example.nutritionapp.foodJournal.FoodJournalOverview;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.Utils;
import com.example.nutritionapp.recommendation.Recommendations;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);

        /* load database on Application start */
        final Database db = new Database(this);

        /* Setup Toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle =findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);

        /* set title on actionBar - default "Nutrition App" (App-Name):
        //toolbar.setTitle("");
        //setTitle("lalala");

        /* drawer (navigation sidebar) */
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /* Night Mode */
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        View navHeader = navigationView.getHeaderView(0);
        ImageButton switchToNightd = navHeader.findViewById(R.id.switchToNight);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) switchToNightd.setImageResource(R.mipmap.ic_sun_foreground);
        switchToNightd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("doof", String.valueOf(currentNightMode),null);
                Resources resources = getResources();
                switch (currentNightMode){
                    case Configuration.UI_MODE_NIGHT_NO:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        //change background to ic_day:
                        switchToNightd.setImageResource(R.mipmap.ic_night_foreground);
                        break;
                    case Configuration.UI_MODE_NIGHT_YES:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        //change background to ic_night:
                        switchToNightd.setImageResource(R.mipmap.ic_night_foreground);
                        break;
                    //case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                //finish();
                //startActivity(getIntent());
            }
        });


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

    /* goto selected item in navigation sidebar */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){
            case R.id.nav_foodJournal:
                Intent journal = new Intent(this, FoodJournalOverview.class);
                startActivity(journal, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_configuration:
                Intent configuration = new Intent(this, PersonalInformation.class);
                startActivity(configuration, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_customFoods:
                Intent createCustomFood = new Intent(this, CustomFoodOverview.class);
                startActivity(createCustomFood, Utils.getDefaultTransition(this));
                break;
            case R.id.nav_analysis:
                Intent analysis =  new Intent(this, Recommendations.class);
                startActivity(analysis, Utils.getDefaultTransition(this));
            case R.id.nav_about:
                Intent about = new Intent(this, AboutPage.class);
                startActivity(about);
            case R.id.nav_weight_tracking:
                Intent weight = new Intent(this, WeightTracking.class);
                startActivity(weight);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* toggle navigation sidebar */
    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
